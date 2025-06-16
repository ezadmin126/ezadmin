package top.ezadmin.config.dto;

import java.util.List;

public class RowButtonConfig {
    private String width;
    private String fixed;
    private List<ButtonConfig> buttons;

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getFixed() {
        return fixed;
    }

    public void setFixed(String fixed) {
        this.fixed = fixed;
    }

    public List<ButtonConfig> getButtons() {
        return buttons;
    }

    public void setButtons(List<ButtonConfig> buttons) {
        this.buttons = buttons;
    }
}