package top.ezadmin.common.enums;

public enum ParamNameEnum {

    itemParamValue("itemParamValue","单字段搜索关键词"),
    itemParamOrderValue("itemParamOrderValue","排序字段"),
    itemParamValueStart("itemParamValueStart","区间开始值"),
    itemParamValueEnd("itemParamValueEnd","区间结束值"),
    itemSearchKey("itemSearchKey","联合搜索字段值"),
    itemSearchValue("itemSearchValue","联合搜索关键词"),
    itemSearchConcatValue("itemSearchConcatValue","多字段搜索字段值"),
    itemSearchDateKey("itemSearchDateKey","联合时间搜索字段值"),
    itemSearchDateValueStart("itemSearchDateValueStart","联合时间搜索开始值"),
    itemSearchDateValueEnd("itemSearchDateValueEnd","联合时间搜索结束值")

    ;

    private String name;private String desc;
    private ParamNameEnum(String name,String desc){
        this.name=name;
        this.desc=desc;
    }
    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

}
