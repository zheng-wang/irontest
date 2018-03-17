package io.irontest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Zheng on 16/03/2018.
 */
public class DataTable {
    private List<DataTableColumn> columns;
    private List<LinkedHashMap<String, Object>> rows;

    public List<DataTableColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<DataTableColumn> columns) {
        this.columns = columns;
    }

    public List<LinkedHashMap<String, Object>> getRows() {
        return rows;
    }

    public void setRows(List<LinkedHashMap<String, Object>> rows) {
        this.rows = rows;
    }

    @JsonIgnore
    public String getColumnTypeByName(String columnName) {
        String columnType = null;
        for (DataTableColumn column: columns) {
            if (column.getType().equals(columnName)) {
                columnType = column.getType();
                break;
            }
        }
        return columnType;
    }
}
