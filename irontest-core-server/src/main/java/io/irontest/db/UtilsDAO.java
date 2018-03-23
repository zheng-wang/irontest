package io.irontest.db;

import io.irontest.models.DataTable;
import io.irontest.models.DataTableCell;
import io.irontest.models.DataTableColumn;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.Transaction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static io.irontest.IronTestConstants.ENDPOINT_PASSWORD_ENCRYPTION_KEY;

/**
 * Created by Zheng Wang on 4/18/16.
 */
public abstract class UtilsDAO {
    @SqlQuery("select TRIM(CHAR(0) FROM UTF8TOSTRING(DECRYPT('AES', '" + ENDPOINT_PASSWORD_ENCRYPTION_KEY + "', :encryptedPassword)))")
    public abstract String decryptEndpointPassword(@Bind("encryptedPassword") String encryptedPassword);

    @CreateSqlObject
    protected abstract DataTableColumnDAO dataTableColumnDAO();

    @CreateSqlObject
    protected abstract DataTableCellDAO dataTableCellDAO();

    @CreateSqlObject
    protected abstract EndpointDAO endpointDAO();

    /**
     * @param testcaseId
     * @param fetchFirstRowOnly if true, only the first data table row (if exists) will be fetched; if false, all rows will be fetched.
     * @return
     */
    @Transaction
    public DataTable getTestcaseDataTable(long testcaseId, boolean fetchFirstRowOnly) {
        DataTable dataTable = null;

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
