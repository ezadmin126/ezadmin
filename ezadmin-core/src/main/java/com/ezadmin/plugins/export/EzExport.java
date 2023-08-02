package com.ezadmin.plugins.export;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @title: Export
 * @Author EzAdmin
 * @Date: 2023/5/29 16:02
 */
public interface EzExport {
    void export(String name,List<List<String>> head , List<List<Object>> data, HttpServletResponse response) throws Exception;
}
