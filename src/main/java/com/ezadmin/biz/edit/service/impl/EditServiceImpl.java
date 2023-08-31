package com.ezadmin.biz.edit.service.impl;

import com.ezadmin.biz.edit.service.EditService;
import com.ezadmin.common.utils.*;

import java.util.Map;

/**
 * @title: EditServiceImpl
 * @Author EzAdmin
 * @Date: 2022/3/9 13:11
 */
public class EditServiceImpl implements EditService {



    public String insertListById(String encodeId,Map<String, String> list ) throws  Exception {
        JsoupConfigHolder.updateCoreByListId(encodeId,"",list);
        return "";
    }

}
