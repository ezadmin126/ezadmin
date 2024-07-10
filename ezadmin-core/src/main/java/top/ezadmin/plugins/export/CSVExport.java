package top.ezadmin.plugins.export;

import top.ezadmin.common.utils.EzDateUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * : CSVExport
 * @author EzAdmin
 */
public class CSVExport implements EzExport {

    @Override
    public void export(String name, List<List<String>> head, List<List<Object>> data,  HttpServletResponse response) throws Exception {
        CSVPrinter printer = null;
        try {
            String fileName = name + "-" + EzDateUtils.todayDatetime();
            response.setContentType("text/csv;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            // 文件名乱码
            String finalfileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "-");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + finalfileName + ".csv");
            // 内容乱码
            response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            printer = new CSVPrinter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8), CSVFormat.EXCEL);

            printer.printRecord(head);
            for (int i = 0; i < data.size(); i++) {
                printer.printRecord(data.get(i));
            }
            printer.flush();
           // response.getWriter().flush();
        } finally {
            if (printer != null)
                printer.close();
        }
    }
}
