package top.ezadmin.config.dto;

import java.util.List;

public class TableConfig {
    private RowButtonConfig rowbutton;
    private List<ColumnConfig> columns;

    public RowButtonConfig getRowbutton() {
        return rowbutton;
    }

    public void setRowbutton(RowButtonConfig rowbutton) {
        this.rowbutton = rowbutton;
    }

    public List<ColumnConfig> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnConfig> columns) {
        this.columns = columns;
    }
}