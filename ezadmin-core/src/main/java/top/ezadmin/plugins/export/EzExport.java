package top.ezadmin.plugins.export;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * : Export
 * @author EzAdmin
 */
public interface EzExport {


    /**
     *
     * @param name
     * @param head  head0 文案  head1  宽度  head2 jdbctype
     * @param data
     * @param response
     * @throws Exception
     */
    void export(String name,List<List<String>> head , List<List<Object>> data, HttpServletResponse response) throws Exception;
}
