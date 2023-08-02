package com.ezadmin.biz.base.controller;


import com.ezadmin.EzBootstrap;
import com.ezadmin.common.annotation.EzMapping;
import com.ezadmin.common.utils.JsoupConfigHolder;
import com.ezadmin.common.utils.ThymeleafUtils;
import com.ezadmin.plugins.cache.MyCache;
import com.ezadmin.web.EzResult;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@EzMapping("/ezadmin/mock")
public class TestDataController extends BaseController {

     EzBootstrap bootstrap=EzBootstrap.instance();
    @EzMapping("/simpletree.html")
    public EzResult index(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
            List<Map<String,String>> list=new ArrayList<>();
            String pid=request.getParameter("PARENT_ID");

            Map<String,String> p1=new HashMap<>();
            p1.put("pid",pid);
            p1.put("id",pid+"0");
            p1.put("name",pid+"0name");

            list.add(p1);



            Map<String,String> p2=new HashMap<>();
            p1.put("pid",pid);
            p1.put("id",pid+"2");
            p1.put("name",pid+"2name");

            list.add(p1);

           return EzResult.instance().data(list);
    }
//    @EzMapping("/alltree.html")
//    public String index(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
//
//    }
}
