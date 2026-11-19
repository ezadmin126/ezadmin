package top.ezadmin.dao.model;

public class CustomSearchSingle {
    /**
     * 连接类型  AND  OR
     */
    private String t;
    /**
     * 字段
     */
    private String f;
    /**
     * 操作符  =  !=  >  <  >=  <=  like
     */
    private String o;
    /**
     * 值
     */
    private String v;

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }
}