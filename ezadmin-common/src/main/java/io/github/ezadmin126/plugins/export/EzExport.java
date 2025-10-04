package io.github.ezadmin126.plugins.export;

import java.util.List;

/**
 * 导出接口
 * 用于数据导出功能
 *
 * @author EzAdmin
 */
public interface EzExport {

    /**
     * 导出数据
     * @param name 文件名
     * @param head 表头信息 head0 文案  head1  宽度  head2 jdbctype
     * @param data 数据
     * @throws Exception 异常
     */
    byte[] export(String name, List<List<String>> head, List<List<Object>> data) throws Exception;
}
