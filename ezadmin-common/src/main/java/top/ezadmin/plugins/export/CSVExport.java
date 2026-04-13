package top.ezadmin.plugins.export;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import top.ezadmin.common.utils.EzDateUtils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV导出实现
 *
 * @author EzAdmin
 */
public class CSVExport implements EzExport {

    @Override
    public EzExportResult export(String name, List<List<String>> head, List<List<Object>> data) throws Exception {
        CSVPrinter printer = null;
        try {
            String fileName = name + "-" + EzDateUtils.todayDatetime();
            // 文件名乱码处理
            String finalfileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "-");
            
            // 使用ByteArrayOutputStream来生成CSV内容
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // 写入BOM头，解决中文乱码问题
            outputStream.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            
            printer = new CSVPrinter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), CSVFormat.EXCEL);

            List<String> headList = new ArrayList<>();
            for (int i = 0; i < head.size(); i++) {
                headList.add(head.get(i).get(0));
            }
            printer.printRecord(headList);
            for (int i = 0; i < data.size(); i++) {
                printer.printRecord(data.get(i));
            }
            printer.flush();
            
            // 这里可以返回生成的CSV内容，或者通过其他方式处理
            // 例如：
            byte[] b= outputStream.toByteArray();
            EzExportResult r=new EzExportResult();
            r.setFile(b);
            r.setContentType("text/csv; charset=utf-8");
            return r;
        } finally {
            if (printer != null)
                printer.close();
        }
    }
}
