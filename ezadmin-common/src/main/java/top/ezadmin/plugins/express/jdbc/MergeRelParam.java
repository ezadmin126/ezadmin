package top.ezadmin.plugins.express.jdbc;

public class MergeRelParam {
    private String exsitSql;
    private String deleteSql;
    private String insertSql;
    private String newIds;

    public String getDeleteSql() {
        return deleteSql;
    }

    public void setDeleteSql(String deleteSql) {
        this.deleteSql = deleteSql;
    }

    public String getInsertSql() {
        return insertSql;
    }

    public void setInsertSql(String insertSql) {
        this.insertSql = insertSql;
    }

    public String getNewIds() {
        return newIds;
    }

    public void setNewIds(String newIds) {
        this.newIds = newIds;
    }

    public String getExsitSql() {
        return exsitSql;
    }

    public void setExsitSql(String exsitSql) {
        this.exsitSql = exsitSql;
    }
}
