package com.ezadmin.biz.edit.controller;

import com.ezadmin.EzBootstrap;
import com.ezadmin.biz.base.service.CoreService;
import com.ezadmin.common.annotation.EzMapping;
import com.ezadmin.common.utils.*;
import com.ezadmin.plugins.cache.MyCache;
import com.ezadmin.web.EzResult;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @title: CoreEditController
 * @Author EzAdmin
 * @Date: 2022/3/9 13:24
 */
@EzMapping("/ezadmin/core/")
public class CoreEditController {
    CoreService coreService = EzProxy.singleInstance(CoreService.class);
    @EzMapping("updateItemSort.html")
    public EzResult updateItemSort(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(StringUtils.isNotBlank(request.getParameter("type"))){
        	String itemType=request.getParameter("type");
        	request.getParameter("addSearch");
        	switch (itemType) {
			case "search":
	            JsoupConfigHolder.updateListSearchSort(request.getParameter("ENCRYPT_LIST_ID"),request.getParameter("items"));
				break;
			case "column":
				JsoupConfigHolder.updateListColumnSort(request.getParameter("ENCRYPT_LIST_ID"),request.getParameter("items"));
				break;
			case "nav":
				JsoupConfigHolder.updateListNavSort(request.getParameter("ENCRYPT_LIST_ID"),request.getParameter("items"));
				break;
			case "tablebutton":
				JsoupConfigHolder.updateListTableButtonSort(request.getParameter("ENCRYPT_LIST_ID"),request.getParameter("items"));
				break;
			case "rowbutton":
				JsoupConfigHolder.updateListRowButtonSort(request.getParameter("ENCRYPT_LIST_ID"),request.getParameter("items"));
				break;
            case "formitem":
                String json=request.getParameter("list");
                String ENCRYPT_FORM_ID=request.getParameter("ENCRYPT_FORM_ID");
                System.out.println(json+ENCRYPT_FORM_ID);

                //[ {"基础信息":"inline"}, {"初始数据":"block"} , {"个性化配置":"block"}]
                JsoupConfigHolder.updateFormItemSort(ENCRYPT_FORM_ID,json);
                break;
			default:
				break;
			}
            System.out.println(request.getParameter("addSearch"));
            return EzResult.instance().data("OK");
        }

		EzBootstrap.instance().getCache()
				.clearKeys("selectFormById");
		EzBootstrap.instance().getCache()
				.clearKeys("ListService");
        return EzResult.instance().data("OK");
    }

}
