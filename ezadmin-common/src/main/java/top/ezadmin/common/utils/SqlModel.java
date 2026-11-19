package top.ezadmin.common.utils;


import java.util.ArrayList;
import java.util.List;

public class SqlModel {
    private List<Object> params = new ArrayList<>();
    private String returnParamName;
    private String returnParamType;
    private String sql;
    private String sqlType;

    private final static SqlModel EMPTY_INSTANCE = new SqlModel();

    public static SqlModel getInstance() {
        return EMPTY_INSTANCE;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "SqlModel{" +
                "params=" + params +
                ", returnParamName='" + returnParamName + '\'' +
                ", returnParamType='" + returnParamType + '\'' +
                ", sql='" + sql + '\'' +
                ", sqlType='" + sqlType + '\'' +
                '}';
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getReturnParamType() {
        return returnParamType;
    }

    public void setReturnParamType(String returnParamType) {
        this.returnParamType = returnParamType;
    }

    public String getReturnParamName() {
        return returnParamName;
    }

    public void setReturnParamName(String returnParamName) {
        this.returnParamName = returnParamName;
    }
}
