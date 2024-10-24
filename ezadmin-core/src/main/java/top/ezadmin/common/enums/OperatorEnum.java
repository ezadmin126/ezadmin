package top.ezadmin.common.enums;

import top.ezadmin.common.utils.StringUtils;

public enum  OperatorEnum {
    EQ("=","等于"),
    //
    NE("!=","不等于"),
    //
   // LT("<","小于"),
    //
    LTE( "<=","小于等于"),
    BETWEEN("BETWEEN","区间"),
    //
   // GT(">","大于"),
    //
    GTE(">=", "大于等于"),
    //
    LIKE("LIKE","包含"),
    //
    NOTLIKE("NOT LIKE","不包含"),
    //
    //START_WITH("LIKE","开始于"),
    //
    //NOT_START_WITH("NOT LIKE ","不开始于"),
    //
    //END_WITH("LIKE","结束于"),
    //
    //NOT_END_WITH("NOT LIKE ","不结束于"),
    //
    IN("IN","IN"),
    //
    NOT_IN("NOT IN","NOT IN"),
    ALL_EQ("=","ALL_EQ"),
    ALL_LIKE("LIKE","ALL_LIKE"),
    ALL_FIND_IN_SET("ALL_FIND_IN_SET","ALL_FIND_IN_SET");
//    //
//    IS_NULL(" IS NULL ","无效的"),
//    //
//    IS_NOT_NULL("IS NOT NULL","有效的"),
//    //
//    EMPTY(" ='' ","空的"),
//    NOT_EMPTY(" !='' ","非空的");


    public String getOperC() {
        return operC;
    }

    String operC;
    String nameC;
    private OperatorEnum(String oper,String name){
        operC=oper ;
        nameC=name;
    }

    public static OperatorEnum match(String oper) {
        if(StringUtils.isBlank(oper)){
            return LIKE;
        }
        for(OperatorEnum e:values()){
            if(StringUtils.equalsIgnoreCase(e.name(),oper.trim())){
                return e;
            }
        }
        return LIKE;
    }

    public   String getNameC(){
        return nameC;
    }

}
