package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.models.DataTable;
import io.irontest.models.DataTableCell;
import io.irontest.models.DataTableColumn;
import io.irontest.models.DataTableColumnType;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.*;

public interface DataTableDAO extends CrossReferenceDAO {
    /**
     * Caption column is the initial column in a data table.
     * @param testcaseId
     */
    default void createCaptionColumn(long testcaseId) {
        DataTableColumn dataTableColumn = new DataTableColumn();
        dataTableColumn.setName(DataTableColumn.COLUMN_NAME_CAPTION);
        dataTableColumn.setSequence((short) 1);
        dataTableColumnDAO().insert(testcaseId, dataTableColumn, DataTableColumnType.STRING.toString());
    }

    /**
     * @param testcaseId
     * @param fetchFirstRowOnly if true, only the first data table row (if exists) will be fetched; if false, all rows will be fetched.
     * @return
     */
    @Transaction
    default DataTable getTestcaseDataTable(long testcaseId, boolean fetchFirstRowOnly) {
        DataTable dataTable = new DataTable();

        List<DataTableColumn> columns = dataTableColumnDAO().findByTestcaseId(testcaseId);

        //  populate the data table rows Java model column by column
        List<LinkedHashMap<String, DataTableCell>> rows = new ArrayList<>();
        Map<Short, LinkedHashMap<String, DataTableCell>> rowSequenceMap = new HashMap<>();  //  map rowSequence to row object (because rowSequence is not consecutive)
        for (DataTableColumn column: columns) {
            List<DataTableCell> cellsInColumn = dataTableCellDAO().findByColumnId(column.getId());
            for (DataTableCell cellInColumn: cellsInColumn) {
                short rowSequence = cellInColumn.getRowSequence();

                if (column.getType() != DataTableColumnType.STRING && cellInColumn.getEndpoint() != null) {
                    cellInColumn.setEndpoint(endpointDAO().findById(cellInColumn.getEndpoint().getId()));
                }

                if (!rowSequenceMap.containsKey(rowSequence)) {
                    LinkedHashMap<String, DataTableCell> row = new LinkedHashMap<>();
                    rowSequenceMap.put(rowSequence, row);
                    rows.add(row);
                }
                rowSequenceMap.get(rowSequence).put(column.getName(), cellInColumn);

                if (fetchFirstRowOnly && rows.size() == 1) {
                    break;
                }
            }
        }

        if (columns.size() > 0) {
            dataTable = new DataTable();
            dataTable.setColumns(columns);
            dataTable.setRows(rows);
        }

        return dataTable;
    }

    @Transaction
    default void duplicateByTestcase(long sourceTestcaseId, long targetTestcaseId) {
        dataTableColumnDAO().duplicateByTestcase(sourceTestcaseId, targetTestcaseId);
        List<DataTableColumn> sourceColumns = dataTableColumnDAO().findByTestcaseId(sourceTestcaseId);
        List<DataTableColumn> targetColumns = dataTableColumnDAO().findByTestcaseId(targetTestcaseId);
        for (DataTableColumn targetColumn: targetColumns) {
            long sourceColumnId = -1;
            for (DataTableColumn sourceColumn: sourceColumns) {
                if (sourceColumn.getName().equals(targetColumn.getName())) {
                    sourceColumnId = sourceColumn.getId();
                    break;
                }
            }
            dataTableCellDAO().duplicateByColumn(sourceColumnId, targetColumn.getId());
        }
    }

    @Transaction
    default void insertByImport(long testcaseId, DataTable dataTable) throws JsonProcessingException {
        for (DataTableColumn column: dataTable.getColumns()) {
            long columnId = dataTableColumnDAO().insert(testcaseId, column.getName(), column.getType().toString());
            for (LinkedHashMap<String, DataTableCell> row: dataTable.getRows()) {
                for (Map.Entry<String, DataTableCell> cellEntry: row.entrySet()) {
                    if (cellEntry.getKey().equals(column.getName())) {
                        dataTableCellDAO().insert(columnId, cellEntry.getValue());
                        break;
                    }
                }
            }
        }
    }
}
