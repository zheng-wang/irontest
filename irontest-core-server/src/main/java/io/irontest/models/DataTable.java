package io.irontest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.resources.ResourceJsonViews;

import java.util.*;

/**
 * Created by Zheng on 16/03/2018.
 */
public class DataTable {
    @JsonView(ResourceJsonViews.DataTableUIGrid.class)
    private List<DataTableColumn> columns = new ArrayList<>();
    @JsonView(ResourceJsonViews.DataTableUIGrid.class)
    private List<LinkedHashMap<String, Object>> rows = new ArrayList<>();

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
    public DataTableColumnType getColumnTypeByName(String columnName) {
        DataTableColumnType columnType = null;
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
            if (!DataTableColumn.COLUMN_NAME_CAPTION.equals(property.getKey()) &&
                    DataTableColumnType.STRING == getColumnTypeByName(property.getKey())) {
                result.put(property.getKey(), (String) property.getValue());
            }
        }
        return result;
    }

    public Map<String, Endpoint> getEndpointPropertiesInRow(int rowIndex) {
        Map<String, Endpoint> result = new HashMap<>();
        LinkedHashMap<String, Object> row = rows.get(rowIndex);
        for (Map.Entry<String, Object> property: row.entrySet()) {
            if (DataTableColumnType.STRING != getColumnTypeByName(property.getKey())) {
                result.put(property.getKey(), (Endpoint) property.getValue());
            }
        }
        return result;
    }
}
