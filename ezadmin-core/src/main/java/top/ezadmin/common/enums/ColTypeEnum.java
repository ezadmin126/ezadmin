package top.ezadmin.common.enums;

public enum ColTypeEnum {


    checkbox("th-checkbox","firstcol-checkbox") ,normal("th-normal","firstcol-normal"),space("th-space","firstcol-space"),
    radio("th-radio","firstcol-radio") ,
    numbers("th-numbers","firstcol-numbers")  ;
    String code;
    String bodycode ;
    public String code(){
        return code;
    }
    public String bodycode(){
        return bodycode;
    }
   private ColTypeEnum(String c,String b){
       bodycode=b;
        code=c ;
    }
    public static boolean isCheckbox(String s) {
        return checkbox.name().equalsIgnoreCase(s)||checkbox.code.equalsIgnoreCase(s);
    }
    public static boolean isRadio(String s) {

        return radio.name().equalsIgnoreCase(s)||radio.code.equalsIgnoreCase(s);
    }
    public static boolean isNumbers(String s) {

        return numbers.name().equalsIgnoreCase(s)||numbers.code.equalsIgnoreCase(s);
    }
    public static boolean isFirst(String s) {
        return isCheckbox(s)||isRadio(s)||isNumbers(s);
    }

}
