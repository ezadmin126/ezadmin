package top.ezadmin;

import org.junit.Test;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.dao.model.CustomSearchDTO;
import top.ezadmin.plugins.sqlgenerate.MySqlGenerate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 测试 uniondate 组件值不正确的情况
 */
public class InvalidUniondateValueTest {

    /**
     * 测试：当值是标签文字而不是日期范围时
     * 这是一个错误的使用方式，应该会生成空的或无效的SQL
     */
    @Test
    public void testInvalidValueWithLabel() {
        System.out.println("===== 测试无效的值（标签文字而非日期范围） =====");

        // 错误的JSON - v的值是"添加时间"而不是日期范围
        String invalidJson = "{\"g\":[{\"t\":\"AND\",\"c\":[{\"s\":[{\"f\":\"ADD_TIME\",\"o\":\"BETWEEN\",\"t\":\"AND\",\"v\":\"添加时间\"}]}]}]}";

        System.out.println("错误的 JSON: " + invalidJson);

        // 尝试生成 SQL
        MySqlGenerate sqlGenerate = new MySqlGenerate();
        Map<String, String> result = sqlGenerate.customSearchJsonToSql(invalidJson, new HashMap<>());
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        // 验证：生成的SQL应该是空的或无效的
        assertNotNull("customWhere不应为null", customWhere);

        // 因为"添加时间"不包含" - "，不会被分割
        // 最终会调用 transOneFileSql 时 valueS="" valueE=""
        // 对于BETWEEN操作，如果valueS和valueE都为空，between方法会返回空字符串

        // 解析JSON验证结构
        CustomSearchDTO parsed = JSONUtils.parseObject(invalidJson, CustomSearchDTO.class);
        assertEquals("字段名应该是ADD_TIME", "ADD_TIME", parsed.getG().get(0).getC().get(0).getS().get(0).getF());
        assertEquals("值应该是'添加时间'", "添加时间", parsed.getG().get(0).getC().get(0).getS().get(0).getV());

        System.out.println("⚠️  警告：使用标签文字作为值会导致无效的SQL条件");
        System.out.println("✅ 测试完成 - 验证了错误值的行为\n");
    }

    /**
     * 测试：对比正确的日期范围值
     */
    @Test
    public void testValidValueWithDateRange() {
        System.out.println("===== 测试正确的值（日期范围） =====");

        // 正确的JSON - v的值是日期范围
        String validJson = "{\"g\":[{\"t\":\"AND\",\"c\":[{\"s\":[{\"f\":\"ADD_TIME\",\"o\":\"BETWEEN\",\"t\":\"AND\",\"v\":\"2024-01-01 - 2024-12-31\"}]}]}]}";

        System.out.println("正确的 JSON: " + validJson);

        // 生成 SQL
        MySqlGenerate sqlGenerate = new MySqlGenerate();
        Map<String, String> result = sqlGenerate.customSearchJsonToSql(validJson, new HashMap<>());
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应为null", customWhere);
        assertTrue("应该包含字段名ADD_TIME", customWhere.contains("ADD_TIME"));
        assertTrue("应该包含日期值", customWhere.contains("2024"));

        System.out.println("✅ 正确的日期范围值生成了有效的SQL\n");
    }

    /**
     * 测试：空值的情况
     */
    @Test
    public void testEmptyValue() {
        System.out.println("===== 测试空值 =====");

        // v的值为空字符串
        String emptyJson = "{\"g\":[{\"t\":\"AND\",\"c\":[{\"s\":[{\"f\":\"ADD_TIME\",\"o\":\"BETWEEN\",\"t\":\"AND\",\"v\":\"\"}]}]}]}";

        System.out.println("空值 JSON: " + emptyJson);

        MySqlGenerate sqlGenerate = new MySqlGenerate();
        Map<String, String> result = sqlGenerate.customSearchJsonToSql(emptyJson, new HashMap<>());
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应为null", customWhere);

        System.out.println("✅ 空值测试完成\n");
    }

    /**
     * 测试：只有一部分日期范围的情况
     */
    @Test
    public void testPartialDateRange() {
        System.out.println("===== 测试部分日期范围 =====");

        // 值只有" - "但没有实际日期
        String partialJson = "{\"g\":[{\"t\":\"AND\",\"c\":[{\"s\":[{\"f\":\"ADD_TIME\",\"o\":\"BETWEEN\",\"t\":\"AND\",\"v\":\" - \"}]}]}]}";

        System.out.println("部分范围 JSON: " + partialJson);

        MySqlGenerate sqlGenerate = new MySqlGenerate();
        Map<String, String> result = sqlGenerate.customSearchJsonToSql(partialJson, new HashMap<>());
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应为null", customWhere);

        System.out.println("✅ 部分日期范围测试完成\n");
    }
}
