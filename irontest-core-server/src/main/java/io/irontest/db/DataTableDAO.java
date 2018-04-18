package io.irontest.db;

import io.irontest.models.DataTable;
import io.irontest.models.DataTableCell;
import io.irontest.models.DataTableColumn;
import io.irontest.models.DataTableColumnType;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.Transaction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Zheng on 17/04/2018.
 */
public abstract class DataTableDAO {
    @CreateSqlObject
    protected abstract DataTableColumnDAO dataTableColumnDAO();

    @CreateSqlObject
    protected abstract DataTableCellDAO dataTableCellDAO();

    @CreateSqlObject
    protected abstract EndpointDAO endpointDAO();

    /**
     * Caption column is the initial column in a data table.
     * @param testcaseId
     */
    public void createCaptionColumn(long testcaseId) {
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
    public DataTable getTestcaseDataTable(long testcaseId, boolean fetchFirstRowOnly) {
        DataTable dataTable = new DataTable();

        List<DataTableColumn> columns = dataTableColumnDAO().findByTestcaseId(testcaseId);

        //  populate the data table rows Java model column by column
        List<LinkedHashMap<String, Object>> rows = new ArrayList<>();
        for (DataTableColumn column: columns) {
            List<DataTableCell> columnCells = dataTableCellDAO().findByColumnId(column.getId());
            for (DataTableCell columnCell: columnCells) {
                short rowSequence = columnCell.getRowSequence();
                if (rows.size() < rowSequence) {
                    rows.add(new LinkedHashMap<String, Object>());
                }
                Object cellObject;
                if (columnCell.getValue() != null) {
                    cellObject = columnCell.getValue();
                } else {
                    cellObject = endpointDAO().findById(columnCell.getEndpointId());
                }
                rows.get(rowSequence - 1).put(column.getName(), cellObject);

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
}
