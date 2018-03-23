package io.irontest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.irontest.models.endpoint.Endpoint;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            if (column.getName().equals(columnName)) {
                columnType = column.getType();
                break;
            }
        }
        return columnType;
    }

    public Map<String, String> getStringPropertiesInRow(int rowIndex) {
        Map<String, String> result = new HashMap<>();
        LinkedHashMap<String, Object> row = rows.get(rowIndex);
        for (Map.Entry<String, Object> property: row.entrySet()) {
            if (!"Caption".equals(property.getKey()) && "String".equals(getColumnTypeByName(property.getKey()))) {
                result.put(property.getKey(), (String) property.getValue());
            }
        }
        return result;
    }

    public Map<String, Endpoint> getEndpointPropertiesInRow(int rowIndex) {
        Map<String, Endpoint> result = new HashMap<>();
        LinkedHashMap<String, Object> row = rows.get(rowIndex);
        for (Map.Entry<String, Object> property: row.entrySet()) {
            if (!"String".equals(getColumnTypeByName(property.getKey()))) {
                result.put(property.getKey(), (Endpoint) property.getValue());
            }
        }
        return result;
    }
}
