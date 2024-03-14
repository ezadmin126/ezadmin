package top.ezadmin.common.enums;

import top.ezadmin.common.utils.StringUtils;

public enum JdbcTypeEnum {
     NUMBER("NUMBER"),
    NUMBERNULL("NUMBERNULL"),
    DATE_TO_NUMBER("DATE_TO_NUMBER"),
    VARCHAR("VARCHAR"),
    DATE("DATE"),
    DATETIME("DATETIME"),
    CLOB("CLOB")
    , BODY("BODY"),
    NUMBER0("NUMBER0"),
    NUMBER1("NUMBER1") ,
    NUMBER2("NUMBER2") ,

    NUMBER3("NUMBER3") ,
    NUMBER4("NUMBER4")

    ;
     String name;
     private JdbcTypeEnum(String name){
         this.name=name;
     }

     public static boolean isNumber0(String jdbcType) {
         return NUMBER0.getName().equalsIgnoreCase(jdbcType);
     }
     public static boolean isNumber2(String jdbcType) {
         return NUMBER2.getName().equalsIgnoreCase(jdbcType);
     }

     public String getName(){return name;}

     public static boolean isDate(String type){
          return DATE.getName().equalsIgnoreCase(type) ;
     }
    public static boolean isDateTime(String type){
        return  DATETIME.getName().equalsIgnoreCase(type);
    }
     public static boolean isNumber(String type){
          return NUMBER.getName().equalsIgnoreCase(type);
     }
    public static boolean isNumberType(String type){
         if(StringUtils.isBlank(type)){
             return false;
         }
        return StringUtils.startsWith(NUMBER.getName(),StringUtils.upperCase(type));
    }

     public static JdbcTypeEnum get(String j){
         for(JdbcTypeEnum e:values()){
             if(e.getName().equalsIgnoreCase(j)){
return e;
             }
         }
         return VARCHAR;
     }
     public static boolean isDatetime(String type){
          return  DATETIME.getName().equalsIgnoreCase(type);
     }
}
