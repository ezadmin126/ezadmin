package top.ezadmin.common.enums;

import top.ezadmin.common.utils.StringUtils;

public enum OrderEnum {
    DESC("1"),ASC("0"),NONE("");
    String order="";
    private OrderEnum(String i){
        order=i;
    }
    public static String getSort(Object i){

        for(OrderEnum e:values()){
            if(StringUtils.equals(e.order,i+"")||StringUtils.equalsIgnoreCase(e.name(),i+"")){
                return e.name();
            }
        }
       return  NONE.name();
    }

}
