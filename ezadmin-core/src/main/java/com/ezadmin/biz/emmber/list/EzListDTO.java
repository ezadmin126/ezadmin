package com.ezadmin.biz.emmber.list;

import com.alibaba.fastjson.annotation.JSONField;
import com.ezadmin.biz.model.EzSearchModel;
import com.ezadmin.common.utils.*;
import com.ezadmin.biz.emmber.ListRow;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.*;

/**
 *
 **/
public class EzListDTO {
    Logger LOG= LoggerFactory.getLogger(EzListDTO.class);

    private Integer listId;

    private DataSource currentDataSource;
    private String encodeListId;
    private Integer templateId;
    @JSONField(serialize = false)
    private String templateBodyList;
    private String listName;
    private String dataSource;
    private String appendHead;
    private String appendFoot;
    private String selectExpress;
    private String countExpress;
    private String defaultEmpty;
    private String firstCol;//FirstColEnum
    private Integer fixNumber=2;
    private Integer fixNumberRight=0;
    private String appendColumnUrl;


    private String appendRowUrl;
    private String defaultOrder;
    private String emptyShow;

    private boolean tableSearchFlag=false;

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    private String defaultGroup;
    private String treegrid;
    private String listEmptyName="查询无结果！请尝试使用其他搜索条件。";

    private boolean hasTableSearch=false;
    private boolean hasFootSum=false;

    private String validateRules;
    private String validateMessages;

    private String operLaydata;

    private String tableStyle;

    private Map<String,String> originMap=new HashMap<>();


    public static EzListDTO mapTo(Map<String,String> listDb){
        EzListDTO list=  new EzListDTO();
        list.setListId(NumberUtils.toInt(listDb.get("LIST_ID")));
        list.setListName(listDb.get("LIST_NAME"));
        list.setTemplateId(NumberUtils.toInt(listDb.get("TEMPLATE_ID")));

        list.setDataSource(listDb.get("DATASOURCE"));
        list.setAppendHead(listDb.get(JsoupUtil.APPEND_HEAD));
        list.setAppendFoot(listDb.get(JsoupUtil.APPEND_FOOT));
        list.setSelectExpress(listDb.get("SELECT_EXPRESS"));
        list.setCountExpress(listDb.get("COUNT_EXPRESS"));
        list.setFirstCol(listDb.get("FIRST_COL"));
        list.setTreegrid(listDb.get("TREEGRID"));
        list.setDefaultOrder(listDb.get("DEFAULT_ORDER"));
        list.setDefaultGroup(listDb.get("DEFAULT_GROUP"));
        list.setDefaultEmpty(listDb.get("DEFAULT_EMPTY"));
        list.setFixNumber(NumberUtils.toInt(listDb.get("FIX_NUMBER")));
        list.setFixNumberRight(NumberUtils.toInt(listDb.get("FIX_NUMBER_RIGHT")));
        list.setTableSearchFlag(Boolean.parseBoolean(listDb.get("TABLE_SEARCH_FLAG")));
        list.setAppendColumnUrl(listDb.get("APPEND_COLUMN_URL"));
        list.setAppendRowUrl(listDb.get("APPEND_ROW_URL"));
        list.setEmptyShow(listDb.get("EMPTY_SHOW"));
        list.setValidateMessages(listDb.get("VALIDATE_MESSAGES"));
        list.setValidateRules(listDb.get("VALIDATE_RULES"));
        list.setOperLaydata(listDb.get(JsoupUtil.LAYDATA));
        list.setTableStyle(listDb.get(JsoupUtil.TABLESTYLE));

        return list;
    }



    private List<Map<String,String>> navBarsList=new ArrayList<>();

    private List<EzSearchModel> searchItemList=new ArrayList<>();
    private List<Map<String,String>>  columnItemList=new ArrayList<>();
    private List<Map<String,String>>  rowButtonItemList=new ArrayList<>();
    private List<Map<String, String>> tableButtonItemList=new ArrayList<>();
    private List<Map<String, String>> dataList = new ArrayList<>();
    public String getDefaultOrder() {
        return defaultOrder;
    }

    public void setDefaultOrder(String defaultOrder) {
        this.defaultOrder = defaultOrder;
    }

    private List<ListRow>  dataRow=new ArrayList<>();

    public String getEzOrderName() {
        return ezOrderName;
    }

    public void setEzOrderName(String ezOrderName) {
        this.ezOrderName = ezOrderName;
    }

    private String  ezOrderName;

    public String getEzOrderValue() {
        return ezOrderValue;
    }

    public void setEzOrderValue(String ezOrderValue) {
        this.ezOrderValue = ezOrderValue;
    }

    private String ezOrderValue;

    private Page page;

    private String pageHtml;

    public String getTreegrid() {
        return treegrid;
    }

    public void setTreegrid(String treegrid) {
        this.treegrid = treegrid;
    }


    public String getDefaultEmpty() {
        return defaultEmpty;
    }

    public void setDefaultEmpty(String defaultEmpty) {
        this.defaultEmpty = defaultEmpty;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getAppendHead() {
        return appendHead;
    }

    public void setAppendHead(String appendHead) {
        this.appendHead = appendHead;
    }

    public String getAppendFoot() {
        return appendFoot;
    }

    public void setAppendFoot(String appendFoot) {
        this.appendFoot = appendFoot;
    }

    public String getSelectExpress() {
        return selectExpress;
    }

    public void setSelectExpress(String selectExpress) {
        this.selectExpress = selectExpress;
    }

    public String getCountExpress() {
        return countExpress;
    }

    public void setCountExpress(String countExpress) {
        this.countExpress = countExpress;
    }

    public String getFirstCol() {
        return firstCol;
    }

    public void setFirstCol(String firstCol) {
        this.firstCol = firstCol;
    }

    public List<Map<String,String>> getNavBarsList() {
        return navBarsList;
    }

    public void setNavBarsList(List<Map<String,String>> navBarsList) {
        this.navBarsList = navBarsList;
    }

    public List<EzSearchModel> getSearchItemList() {
        return searchItemList;
    }

    public void setSearchItemList(List<EzSearchModel> searchItemList) {
        this.searchItemList = searchItemList;
    }



    public List<Map<String, String>> getTableButtonItemList() {
        return tableButtonItemList;
    }

    public void setTableButtonItemList(List<Map<String, String>> tableButtonItemList) {
        this.tableButtonItemList = tableButtonItemList;
    }

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public List<Map<String, String>> getDataList() {
        return dataList;
    }

    public void setDataList(List<Map<String, String>> dataList) {
        this.dataList = dataList;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<Map<String,String>> getColumnItemList() {
        return columnItemList;
    }

    public void setColumnItemList(List<Map<String,String>> columnItemList) {
        this.columnItemList = columnItemList;
    }

    public List<Map<String,String>> getRowButtonItemList() {
        return rowButtonItemList;
    }

    public List<Map<String,String>> copyNavItemList() {
        if(Utils.isNotEmpty(navBarsList)){
            List<Map<String,String>> list=new ArrayList<>();
            for (int i = 0; i < navBarsList.size(); i++) {
                try {
                    Map<String,String> copy=new HashMap<>();
                    copy.putAll(navBarsList.get(i));
                    list.add(copy);
                }catch (Exception e){
                    LOG.error(JSONUtils.toJSONString(navBarsList.get(i)),e);
                    Utils.addLog(e.getMessage());
                    // list.add(item);
                }
            }
            return list;
        }
        return Collections.emptyList();
    }


    //减少内存占用
    @Deprecated
    public List<Map<String,String>> copyColumnVOItemList() {
        if(Utils.isNotEmpty(columnItemList)){
        List<Map<String,String>> list=new ArrayList<>();
            for (int i = 0; i < columnItemList.size(); i++) {
                try {
                    Map<String,String> m=new HashMap<>();
                    m.putAll(columnItemList.get(i));
                    list.add(m);

                }catch (Exception e){
                    LOG.error("",e);
                    Utils.addLog(e.getMessage());
                    list.add(columnItemList.get(i));
                }
            }
        return list;
        }
        return Collections.emptyList();
    }

    public void setRowButtonItemList(List<Map<String,String>> rowButtonItemList) {
        this.rowButtonItemList = rowButtonItemList;
    }

    public List<ListRow> getDataRow() {
        return dataRow;
    }

    public void setDataRow(List<ListRow> dataRow) {
        this.dataRow = dataRow;
    }

    public String getPageHtml() {
        return pageHtml;
    }

    public void setPageHtml(String pageHtml) {
        this.pageHtml = pageHtml;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }
    public String getTemplateBodyList() {
        return templateBodyList;
    }

    public void setTemplateBodyList(String templateBody) {
        this.templateBodyList = templateBody;
    }

    public Integer getFixNumber() {
        return fixNumber;
    }

    public void setFixNumber(Integer fixNumber) {
        this.fixNumber = fixNumber;
    }

    public String getAppendColumnUrl() {
        return appendColumnUrl;
    }

    public void setAppendColumnUrl(String appendColumnUrl) {
        this.appendColumnUrl = appendColumnUrl;
    }

    public String getAppendRowUrl() {
        return appendRowUrl;
    }

    public void setAppendRowUrl(String appendRowUrl) {
        this.appendRowUrl = appendRowUrl;
    }
    public String getEncodeListId() {
        return encodeListId;
    }

    public void setEncodeListId(String encodeListId) {
        this.encodeListId = encodeListId;
    }

    public String getListEmptyName() {
        return listEmptyName;
    }

    public void setListEmptyName(String listEmptyName) {
        this.listEmptyName = listEmptyName;
    }

    public String getEmptyShow() {
        return emptyShow;
    }

    public void setEmptyShow(String emptyShow) {
        this.emptyShow = emptyShow;
    }

    public boolean isHasTableSearch() {
        return hasTableSearch;
    }

    public void setHasTableSearch(boolean hasTableSearch) {
        this.hasTableSearch = hasTableSearch;
    }

    public boolean isHasFootSum() {
        return hasFootSum;
    }

    public void setHasFootSum(boolean hasFootSum) {
        this.hasFootSum = hasFootSum;
    }

    public boolean isTableSearchFlag() {
        return tableSearchFlag;
    }

    public void setTableSearchFlag(boolean tableSearchFlag) {
        this.tableSearchFlag = tableSearchFlag;
    }

    public Integer getFixNumberRight() {
        return fixNumberRight;
    }

    public void setFixNumberRight(Integer fixNumberRight) {
        this.fixNumberRight = fixNumberRight;
    }

    public String getValidateMessages() {
        return validateMessages;
    }

    public void setValidateMessages(String validateMessages) {
        this.validateMessages = validateMessages;
    }

    public String getValidateRules() {
        return validateRules;
    }

    public void setValidateRules(String validateRules) {
        this.validateRules = validateRules;
    }

    public Map  getOriginMap() {
        return originMap;
    }

    public void setOriginMap(Map<String, String> originMap) {
        this.originMap = originMap;
    }



    public DataSource getCurrentDataSource() {
        return currentDataSource;
    }

    public void setCurrentDataSource(DataSource currentDataSource) {
        this.currentDataSource = currentDataSource;
    }



    public String getOperLaydata() {
        return operLaydata;
    }

    public void setOperLaydata(String operLaydata) {
        this.operLaydata = operLaydata;
    }

    public String getTableStyle() {
        return tableStyle;
    }

    public void setTableStyle(String tableStyle) {
        this.tableStyle = tableStyle;
    }
}
