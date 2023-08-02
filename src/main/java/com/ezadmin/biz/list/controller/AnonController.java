package com.ezadmin.biz.list.controller;

/**
 * @title: AnonController
 * @Author EzAdmin
 * @Date: 2021/11/7 12:13
 */

import com.ezadmin.common.annotation.EzConfig;
import com.ezadmin.common.annotation.EzMapping;
import com.ezadmin.common.utils.EzProxy;
import com.ezadmin.web.Config;
import com.ezadmin.biz.base.service.CoreService;
 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EzMapping("/ezadmin/anon/")
public class AnonController {
    Logger log = LoggerFactory.getLogger(ListController.class);
    CoreService coreService = EzProxy.singleInstance(CoreService.class);
    @EzConfig
    Config config;


}
