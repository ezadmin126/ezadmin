package top.ezadmin.config.dto;

public class ColumnConfig {
    private String name;
    private String width;
    private String minwidth;
    private String fixed;
    private String jdbctype;
    private String url;
    private String body;
    private String label; // column标签体内容

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getMinwidth() {
        return minwidth;
    }

    public void setMinwidth(String minwidth) {
        this.minwidth = minwidth;
    }

    public String getFixed() {
        return fixed;
    }

    public void setFixed(String fixed) {
        this.fixed = fixed;
    }

    public String getJdbctype() {
        return jdbctype;
    }

    public void setJdbctype(String jdbctype) {
        this.jdbctype = jdbctype;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}