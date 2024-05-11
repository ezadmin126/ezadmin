package top.ezadmin.common.enums;

public enum UrlOpenTypeEnum {
    MODEL("1","弹窗"),
    FORM("11","带确认按钮的表单"),
    CONFIRM_MODEL("9","带确认的弹窗提示"),
    _BLANK("2","新浏览器窗口"),
    LOCATION("3","当前窗口"),
    PARENT("4","父窗口打开标签"),
    APPEND_PARAM("5","携带当前搜索参数窗口打开"),
    _BLANK_PARAM("6","携带当前搜索参数新浏览器窗口"),
    _BLANK_PARAM_COLUMN("10","只携带配置显示的参数，用于导出"),
    AJAX("7","异步请求"),
    CONFIRM_AJAX("8","带确认的异步请求"),
    SCRIPT("9","自定义脚本"),
    ;
    String code;String desc;
    private UrlOpenTypeEnum(String code,String desc){
        this.code=code;
        this.desc=desc;
    }
    public String code(){
        return code;
    }
    public String desc(){
        return desc;
    }
}
