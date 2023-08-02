package com.ezadmin.biz.base.service;



import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface CoreService {


    Map<String, String> selectListOne(String sql,Object[] obj) throws Exception;

}
