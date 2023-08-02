package com.ezadmin.biz.dao.edit.service.impl;

import com.ezadmin.EzBootstrap;
import com.ezadmin.biz.base.service.CoreService;
import com.ezadmin.biz.dao.Dao;
import com.ezadmin.biz.emmber.form.DefalutEzFormBuilder;
import com.ezadmin.biz.emmber.form.EzFormBuilder;
import com.ezadmin.biz.list.emmber.list.DefaultEzList;
import com.ezadmin.biz.list.emmber.list.EzList;
import com.ezadmin.common.annotation.EzConfig;
import com.ezadmin.common.utils.*;
import com.ezadmin.web.Config;
import com.ezadmin.biz.dao.edit.service.EditService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
