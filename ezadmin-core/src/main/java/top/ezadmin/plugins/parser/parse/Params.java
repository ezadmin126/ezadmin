package top.ezadmin.plugins.parser.parse;

public class Params {
    private static final String VARCHAR="VARCHAR";
    private String paramKey;
    private Object paramValue;
    private String jdbcType=VARCHAR;

    @Override
    public String toString() {
        return "Params{" +
                "paramKey='" + paramKey + '\'' +
                ", paramValue=" + paramValue +
                ", jdbcType='" + jdbcType + '\'' +
                '}';
    }
    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }
    public Object getParamValue() {
        return paramValue;
    }

    public void setParamValue(Object paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }
}
