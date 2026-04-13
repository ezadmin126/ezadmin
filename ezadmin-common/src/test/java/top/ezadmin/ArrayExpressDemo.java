package top.ezadmin;

import top.ezadmin.common.utils.ConfigFileLoader;
import top.ezadmin.common.utils.ExpressFileLoader;

import java.util.List;
import java.util.Map;

/**
 * 演示数组表达式引用功能
 */
public class ArrayExpressDemo {

    public static void main(String[] args) throws Exception {
        // 设置为开发模式，启用热加载
        EzBootstrap.config().setSqlCache(false);

        // 加载配置文件
        String configPath = "topezadmin/config/layui/dsl/list/test-array-express.json";
        Map<String, Object> config = ConfigFileLoader.loadConfigFile(configPath);

        System.out.println("===== 配置文件加载完成 =====");
        System.out.println("配置ID: " + config.get("id"));
        System.out.println("配置标题: " + config.get("title"));

        // 查看处理前的数组
        @SuppressWarnings("unchecked")
        List<Object> appendFoot = (List<Object>) config.get("appendFoot");
        System.out.println("\n处理前的 appendFoot 数组:");
        for (int i = 0; i < appendFoot.size(); i++) {
            System.out.println("  [" + i + "]: " + appendFoot.get(i));
        }

        // 处理表达式引用
        ExpressFileLoader.processExpressReferences(config, configPath);
        System.out.println("\n===== 表达式引用处理完成 =====");

        // 查看处理后的数组
        System.out.println("\n处理后的 appendFoot 数组:");
        for (int i = 0; i < appendFoot.size(); i++) {
            Object item = appendFoot.get(i);
            if (item instanceof String) {
                String str = (String) item;
                if (str.length() > 100) {
                    System.out.println("  [" + i + "]: " + str.substring(0, 100) + "... (" + str.length() + " 字符)");
                } else {
                    System.out.println("  [" + i + "]: " + str);
                }
            }
        }

        // 查看嵌套结构
        @SuppressWarnings("unchecked")
        Map<String, Object> nestedExample = (Map<String, Object>) config.get("nestedExample");
        @SuppressWarnings("unchecked")
        List<Object> items = (List<Object>) nestedExample.get("items");

        System.out.println("\n处理后的嵌套结构:");
        for (int i = 0; i < items.size(); i++) {
            Object item = items.get(i);
            if (item instanceof String) {
                System.out.println("  items[" + i + "]: " + item);
            } else if (item instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> obj = (Map<String, Object>) item;
                System.out.println("  items[" + i + "]: {");
                for (Map.Entry<String, Object> entry : obj.entrySet()) {
                    System.out.println("    " + entry.getKey() + ": " + entry.getValue());
                }
                System.out.println("  }");
            }
        }
    }
}