package top.ezadmin;

import top.ezadmin.common.utils.ConfigFileLoader;
import top.ezadmin.common.utils.ExpressFileLoader;

import java.util.List;
import java.util.Map;

/**
 * 演示 appendHead/appendFoot 数组自动拼接功能
 *
 * 功能说明：
 * 1. 如果 appendHead 或 appendFoot 是数组，系统会自动将其拼接成多行字符串
 * 2. 支持在数组中使用 $ 表达式引用外部文件
 * 3. 最终输出为换行分隔的字符串，便于在 HTML 模板中直接使用
 */
public class AppendFieldsDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("===== appendHead/appendFoot 数组拼接演示 =====\n");

        // 设置为开发模式，启用热加载
        EzBootstrap.config().setSqlCache(false);

        // 加载配置文件
        String configPath = "topezadmin/config/layui/dsl/list/test-array-express.json";
        Map<String, Object> config = ConfigFileLoader.loadConfigFile(configPath);

        System.out.println("1. 原始配置（数组形式）:");
        System.out.println("-----------------------------------");

        @SuppressWarnings("unchecked")
        List<Object> appendHead = (List<Object>) config.get("appendHead");
        System.out.println("appendHead 数组:");
        for (int i = 0; i < appendHead.size(); i++) {
            System.out.println("  [" + i + "]: " + appendHead.get(i));
        }

        @SuppressWarnings("unchecked")
        List<Object> appendFoot = (List<Object>) config.get("appendFoot");
        System.out.println("\nappendFoot 数组:");
        for (int i = 0; i < appendFoot.size(); i++) {
            System.out.println("  [" + i + "]: " + appendFoot.get(i));
        }

        System.out.println("\n2. 处理表达式引用:");
        System.out.println("-----------------------------------");

        // 处理表达式文件引用（$ 开头的值会被替换为文件内容）
        ExpressFileLoader.processExpressReferences(config, configPath);

        System.out.println("表达式引用已处理，$infolist_foot.js 已替换为文件内容");

        System.out.println("\n3. 拼接数组为字符串:");
        System.out.println("-----------------------------------");

        // 处理 appendHead 和 appendFoot，将数组拼接成换行的字符串
        ExpressFileLoader.processAppendFields(config);

        System.out.println("数组已拼接为多行字符串");

        System.out.println("\n4. 最终结果（字符串形式）:");
        System.out.println("-----------------------------------");

        String appendHeadStr = (String) config.get("appendHead");
        String appendFootStr = (String) config.get("appendFoot");

        System.out.println("appendHead 字符串:");
        System.out.println(appendHeadStr);

        System.out.println("\nappendFoot 字符串（前200字符）:");
        if (appendFootStr.length() > 200) {
            System.out.println(appendFootStr.substring(0, 200) + "...");
        } else {
            System.out.println(appendFootStr);
        }

        System.out.println("\n5. 使用场景:");
        System.out.println("-----------------------------------");
        System.out.println("在 HTML 模板中可以直接使用：");
        System.out.println("<head>");
        System.out.println("    ${appendHead}");
        System.out.println("</head>");
        System.out.println();
        System.out.println("<body>");
        System.out.println("    <!-- 页面内容 -->");
        System.out.println("    ${appendFoot}");
        System.out.println("</body>");

        System.out.println("\n===== 演示完成 =====");
    }
}