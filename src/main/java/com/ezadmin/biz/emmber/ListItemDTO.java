package com.ezadmin.biz.emmber;

import com.ezadmin.common.enums.ItemTypeEnum;
import com.ezadmin.common.enums.JdbcTypeEnum;
import com.ezadmin.common.enums.OperatorEnum;
import com.ezadmin.common.enums.UrlOpenTypeEnum;
import com.ezadmin.common.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ListItemDTO {
    private ListItemDTO(){}
    //button,button_row,daterange,head,input,pagination,select
    public static ListItemDTO  createSearch() {
        ListItemDTO item= new  ListItemDTO();
        item.itemType(ItemTypeEnum.SEARCH).itemCellCode("input");
        return item;
    }
    public static ListItemDTO  createTableButton() {
        ListItemDTO item= new  ListItemDTO();
        item.itemType(ItemTypeEnum.TABLE_BUTTON).itemCellCode("button");
        return item;
    }
    public static ListItemDTO  createTableHead() {
        ListItemDTO item= new  ListItemDTO();
        item.itemType(ItemTypeEnum.THEAD).itemCellCode("head");
        return item;
    }
    public static ListItemDTO  createRowButton() {
        ListItemDTO item= new  ListItemDTO();
        item.itemType(ItemTypeEnum.ROW_BUTTON).itemCellCode("button_row");
        return item;
    }


    public Map<String,String> map(){
        Map<String,String> map=new HashMap<>();
        map.put("ITEM_LABEL",itemLabel);
        map.put("ITEM_NAME",itemName);
        map.put("ITEM_PLACEHOLDER",itemPlaceHolder);
        map.put("ITEM_CELL_CODE",itemCellCode);
        map.put("DATA_SOURCE",data_source);
        map.put("ITEM_TYPE",itemType);
        map.put("ITEM_URL", itemUrl);
        map.put("ITEM_SORT",itemSort);
        map.put("ITEM_SEARCH_TYPE",itemSearchType);
        map.put("ITEM_SEARCH_OPER",operator.name());
        map.put("ITEM_JDBC_TYPE",jdbcType.name());
        map.put("ITEM_DESC",itemDesc);
        map.put("ITEM_OPEN_TYPE",itemOpenType);
        map.put("ITEM_OPEN_TITLE",itemOpenTitle);
        map.put("ITEM_ORDER",itemOrder);
        map.put("ITEM_TBODY_CODE",itemBodyCode);
        map.put("ITEM_ALIAS",itemAlias);
        return map;
    }
    private String itemId;
    private String itemLabel;
    private String itemName;
    private String itemPlaceHolder;
    private String itemCellCode;
    private String data_source;
    private String itemType;
    private String itemUrl;

    public String getItemSort() {
        return itemSort;
    }

    public void setItemSort(String itemSort) {
        this.itemSort = itemSort;
    }

    private String itemSort;
    private String itemSearchType;
    private OperatorEnum operator=OperatorEnum.EQ;
    private JdbcTypeEnum jdbcType=JdbcTypeEnum.VARCHAR;
    private String itemDesc;
    private String itemOpenType;
    private String itemOpenTitle;
    private String itemOrder;
    private String itemBodyCode="text";
    private String itemAlias="";


    /**
     * 名称 表头或者搜索的标签名称
     * @param itemLabel
     * @return
     */
    public ListItemDTO itemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
        return this;
    }

    /**
     *
     * @param itemName 与查询SQL的字段保持一致，
     */
    public ListItemDTO itemName(String itemName) {
        this.itemName = StringUtils.upperCase(itemName);
        return this;
    }
    /**
     *
     * @param itemAlias 与查询SQL的字段的别名保持一致，主要是为了拼接where语句。
     *                  不使用子查询，提高系统性能
     */
    public ListItemDTO itemAlias(String itemAlias) {
        this.itemAlias =  itemAlias ;
        return this;
    }
    /**
     *
     * @param itemPlaceHolder
     * @return
     */
    public ListItemDTO itemPlaceHolder(String itemPlaceHolder) {
        this.itemPlaceHolder = itemPlaceHolder;
        return this;
    }

    /**
     * 位于 template/${style}/list  下的文件的名称，内置：
     * button,button_row,daterange,head,input,pagination,select等
     * @param itemCellCode
     * @return
     */
    public ListItemDTO itemCellCode(String itemCellCode) {
        this.itemCellCode = itemCellCode;
        return this;
    }

    /**
     * status  下拉 启用禁用
     * yesno   下拉 是否
     * json格式  [{"VALUE":"待完善","KEY":"0"},{"VALUE":"审核中","KEY":"1"},{"VALUE":"审核不通过","KEY":"2"},{"VALUE":"审核通过","KEY":"3"}]
     * sql格式 select  data_name AS `VALUE`,da_id as `KEY` from t_core_database
     * @return
     */
    public ListItemDTO dataConfig(String dataConfig) {
        this.data_source = data_source;
        return this;
    }

    protected ListItemDTO   itemType(ItemTypeEnum itemType) {
        this.itemType = itemType.getType()+"";
        return this;
    }

    /**
     *
     * @param itemUrl  支持${}语法
     */
    public ListItemDTO itemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
        return this;
    }

//    public String getItemSort() {
//        return itemSort;
//    }

    /**
     *
     * @param itemSort 默认值是 先后顺序，此处可以强制设置排序
     */
    public ListItemDTO itemSort(String itemSort) {
        this.itemSort = itemSort;
        return this;
    }



    /**
     *
     * @param jdbcType 数据库字段类型，部分场景需要用到
     * @return
     */
    public ListItemDTO jdbcType(JdbcTypeEnum jdbcType) {
        this.jdbcType = jdbcType;
        return this;
    }


//    /**
//     *
//     * @param itemDesc  描述字段
//     * @return
//     */
//    public ListItemDTO setItemDesc(String itemDesc) {
//        this.itemDesc = itemDesc;
//        return this;
//    }

    /**
     *   <OPTION    value="1"   >窗口</OPTION>
     *   <OPTION    value="2"   >新窗口blank</OPTION>
     *   <OPTION    value="3"   >当前窗口</OPTION>
     *   <OPTION    value="4"   >父窗口</OPTION>
     *   <OPTION    value="5"   >携带当前搜索参数窗口打开</OPTION>
     * @param itemOpenType
     */
    public ListItemDTO itemOpenType(UrlOpenTypeEnum itemOpenType) {
        this.itemOpenType = itemOpenType.code();
        return this;
    }


    /**
     *
     * @param itemOpenTitle 窗口打开的标题
     * @return
     */
    public ListItemDTO itemOpenTitle(String itemOpenTitle) {
        this.itemOpenTitle = itemOpenTitle;
        return this;
    }


    /**
     *
     * @param itemOrder 是否开启排序  1是  0 否
     * @return
     */
    public ListItemDTO itemOrder(String itemOrder) {
        this.itemOrder = itemOrder;
        return this;
    }

    /**
     * button,link,select,text(默认) 数据展现方式
     * @return
     */
    public ListItemDTO bodyLink() {
        this.itemBodyCode = "link";
        return this;
    }
    public ListItemDTO bodySelect() {
        this.itemBodyCode = "select";
        return this;
    }
    public ListItemDTO bodyButton() {
        this.itemBodyCode = "button";
        return this;
    }

    /**
     *
     * @param operator  SQL中的表达式
     * @return
     */
    public ListItemDTO operator(OperatorEnum operator) {
        this.operator = operator;
        return this;
    }
}
