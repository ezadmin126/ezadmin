package top.ezadmin.dao.model;

import java.util.List;

public class TableRow {

    private List<String> rowButtons  ;
    private List<String> tds  ;


    public List<String> getRowButtons() {
        return rowButtons;
    }

    public void setRowButtons(List<String> rowButtons) {
        this.rowButtons = rowButtons;
    }
}
