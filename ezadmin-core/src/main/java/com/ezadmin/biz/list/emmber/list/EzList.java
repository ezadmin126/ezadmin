package com.ezadmin.biz.list.emmber.list;



import com.ezadmin.biz.emmber.list.*;
import com.ezadmin.biz.model.EzSearchModel;
import com.ezadmin.common.utils.Page;

import java.util.List;
import java.util.Map;

public interface EzList {

    static String itemParamValue="itemParamValue";


    EzListDTO getEzListDto();
    EzListDTO loading();
    EzListDTO loadingBase();
    List<Map<String,String>> loadingNavBar();
    List<EzSearchModel> loadingSearch();
    List<Map<String, String>> loadingTableButton();
    List<Map<String,String>> loadingColumn();
    List<Map<String,String>> loadingRowButton();
    Page getPagination();
    List<Map<String, String>> loadingData() throws Exception;

    void renderHtml();
    List<List<Object>> renderExcel();

}
