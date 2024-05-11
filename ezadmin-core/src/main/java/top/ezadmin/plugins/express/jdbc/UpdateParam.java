package top.ezadmin.plugins.express.jdbc;

import java.util.ArrayList;
import java.util.List;

public class UpdateParam {
    private List list=new ArrayList();
    private String where;
    private String table;
    private String idName="ID";


    public UpdateParam add(Object obj){
        list.add(obj);
        return this;
    }
    public UpdateParam table(String obj){
        table=obj;
        return this;
    }
    public UpdateParam where(String obj){
        where=obj;
        return this;
    }



    public List getList() {
        return list;
    }

    public String getTable() {
        return table;
    }

    public String getWhere() {
        return where;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }
}
