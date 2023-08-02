package com.ezadmin.biz.base.service.impl;


import com.ezadmin.biz.base.service.CoreService;
import com.ezadmin.common.annotation.EzCacheAnnotation;
import com.ezadmin.common.utils.StringUtils;
import com.ezadmin.common.utils.Utils;
import com.ezadmin.EzBootstrap;
import com.ezadmin.biz.dao.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.util.*;

public class CoreServiceImpl implements CoreService {
    Logger logger= LoggerFactory.getLogger(CoreServiceImpl.class);

    Dao dao=Dao.getInstance();
    EzBootstrap bootstrap=EzBootstrap.instance();




    public  Map<String, String> selectListOne(String sql,Object[] obj) throws Exception {
        try {
            if (StringUtils.isNotBlank(sql)) {
                List<Map<String, String>> list = dao.executeQueryString(bootstrap.getOriginDataSource(), sql, obj);
                if (Utils.isNotEmpty(list)) {
                    return list.get(0);
                }
            }
        }catch (Exception e){
            Utils.addLog(sql,e);
        }
        return Collections.emptyMap();
    }




//    public long executeInsertByMap(Connection connection, String table, Map<String, String> form) throws Exception {
//        StringBuilder ff = new StringBuilder();
//        try {
//           StringBuilder columnName = new StringBuilder();
//           StringBuilder params = new StringBuilder();
//           List<Object> list = new ArrayList<>();
//           for (Map.Entry<String,String>entry:form.entrySet()){
//               columnName.append("," + entry.getKey());
//               params.append(",?");
//               list.add(entry.getValue());
//           }
//
//           ff.append("insert into " + table + " (");
//           ff.append(columnName.substring(1));
//           ff.append(" ) values (");
//           ff.append(params.substring(1));
//           ff.append(")");
//           long id = dao.executeUpdateNotClose(connection, ff.toString(), list.stream().toArray());
//           return id;
//       }catch (Exception e){
//           logger.error(ff.toString(),e);
//           throw e;
//       }
//    }


    @EzCacheAnnotation
    public List<Map<String, String>> selectBySql(DataSource datasource,String sql,Object[] obj) throws Exception {
        List<Map<String, String>> keyvalues = dao.executeQueryString(datasource,sql, obj);
        return keyvalues;
    }

}
