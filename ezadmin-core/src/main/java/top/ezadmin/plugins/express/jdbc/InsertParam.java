package top.ezadmin.plugins.express.jdbc;

import java.util.ArrayList;
import java.util.List;

public class InsertParam {
   private List list=new ArrayList();
   private String table;

   public InsertParam add(Object obj){
       list.add(obj);
       return this;
   }
    public InsertParam table(String obj){
         table=obj;
        return this;
    }




    public List getList() {
        return list;
    }


    public String getTable() {
        return table;
    }

}
