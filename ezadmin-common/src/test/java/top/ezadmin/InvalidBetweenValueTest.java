package top.ezadmin;

import org.junit.Test;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.dao.model.CustomSearchDTO;
import top.ezadmin.plugins.sqlgenerate.MySqlGenerate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 测试 BETWEEN 操作符使用了无效值的情况
 */
public class InvalidBetweenValueTest {

    /**
     * 测试：BETWEEN 操作符但值不是范围格式
     * 例如：手机号、电话号字段使用 BETWEEN 操作符，但值是 "aa"
     */
    @Test
    public void testBetweenWithInvalidValue() {
        System.out.println("===== 测试 BETWEEN 操作符使用无效值 =====");

        // 问题的 JSON
        String invalidJson = "{\"g\":[{\"t\":\"AND\",\"c\":[{\"s\":[{\"f\":\"TRADER_CONTACT_MOBILE\",\"o\":\"BETWEEN\",\"t\":\"AND\",\"v\":\"aa\"},{\"f\":\"TRADER_CONTACT_TELEPHONE\",\"o\":\"BETWEEN\",\"t\":\"OR\",\"v\":\"aa\"}]}]}]}";

        System.out.println("问题的 JSON: " + invalidJson);

        // 解析验证
        CustomSearchDTO parsed = JSONUtils.parseObject(invalidJson, CustomSearchDTO.class);
        assertNotNull("JSON应该能正常解析", parsed);
        assertEquals("应该有1个分组", 1, parsed.getG().size());
        assertEquals("应该有2个条件", 2, parsed.getG().get(0).getC().get(0).getS().size());

        // 生成 SQL
        MySqlGenerate sqlGenerate = new MySqlGenerate();
        Map<String, String> result = sqlGenerate.customSearchJsonToSql(invalidJson, new HashMap<>());
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应为null", customWhere);

        // 因为 "aa" 不包含 " - "，所以：
        // 1. 在 customWhere 方法中不会被分割
        // 2. value="aa", valueS="", valueE=""
        // 3. between 方法收到空的 valueS 和 valueE，会返回空字符串
        // 4. 最终生成空的 SQL 条件

        System.out.println("⚠️  警告：BETWEEN 操作符的值 'aa' 不是有效的范围格式");
        System.out.println("⚠️  建议：手机号和电话号应该使用 LIKE 或 EQ 操作符");
        System.out.println("✅ 测试完成\n");
    }

    /**
     * 测试：正确的用法 - 应该使用 LIKE 操作符
     */
    @Test
    public void testCorrectUsageWithLike() {
        System.out.println("===== 测试正确的用法 - 使用 LIKE 操作符 =====");

        // 正确的 JSON - 使用 LIKE 操作符搜索手机号和电话号
        String correctJson = "{\"g\":[{\"t\":\"AND\",\"c\":[{\"s\":[{\"f\":\"TRADER_CONTACT_MOBILE\",\"o\":\"LIKE\",\"t\":\"AND\",\"v\":\"138\"},{\"f\":\"TRADER_CONTACT_TELEPHONE\",\"o\":\"LIKE\",\"t\":\"OR\",\"v\":\"010\"}]}]}]}";

        System.out.println("正确的 JSON: " + correctJson);

        MySqlGenerate sqlGenerate = new MySqlGenerate();
        Map<String, String> result = sqlGenerate.customSearchJsonToSql(correctJson, new HashMap<>());
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应为null", customWhere);
        assertTrue("应该包含 TRADER_CONTACT_MOBILE", customWhere.contains("TRADER_CONTACT_MOBILE"));
        assertTrue("应该包含 TRADER_CONTACT_TELEPHONE", customWhere.contains("TRADER_CONTACT_TELEPHONE"));
        assertTrue("应该包含 LIKE", customWhere.toUpperCase().contains("LIKE"));
        assertTrue("应该包含手机号前缀138", customWhere.contains("138"));
        assertTrue("应该包含电话号前缀010", customWhere.contains("010"));

        System.out.println("✅ 使用 LIKE 操作符生成了正确的 SQL\n");
    }

    /**
     * 测试：EQ 操作符精确匹配
     */
    @Test
    public void testCorrectUsageWithEq() {
        System.out.println("===== 测试正确的用法 - 使用 EQ 操作符 =====");

        // 使用 EQ 操作符精确匹配
        String eqJson = "{\"g\":[{\"t\":\"AND\",\"c\":[{\"s\":[{\"f\":\"TRADER_CONTACT_MOBILE\",\"o\":\"EQ\",\"t\":\"AND\",\"v\":\"13812345678\"}]}]}]}";

        System.out.println("使用 EQ 的 JSON: " + eqJson);

        MySqlGenerate sqlGenerate = new MySqlGenerate();
        Map<String, String> result = sqlGenerate.customSearchJsonToSql(eqJson, new HashMap<>());
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应为null", customWhere);
        assertTrue("应该包含 TRADER_CONTACT_MOBILE", customWhere.contains("TRADER_CONTACT_MOBILE"));
        assertTrue("应该包含完整手机号", customWhere.contains("13812345678"));
        assertTrue("应该包含等号", customWhere.contains("="));

        System.out.println("✅ 使用 EQ 操作符生成了正确的 SQL\n");
    }

    /**
     * 生成推荐的 JSON 格式
     */
    @Test
    public void generateRecommendedJson() {
        System.out.println("===== 推荐的 JSON 格式 =====\n");

        System.out.println("场景1：模糊搜索手机号（以138开头）");
        String likeJson = "{\"g\":[{\"t\":\"AND\",\"c\":[{\"s\":[{\"f\":\"TRADER_CONTACT_MOBILE\",\"o\":\"LIKE\",\"t\":\"AND\",\"v\":\"138\"}]}]}]}";
        System.out.println("JSON: " + likeJson);

        MySqlGenerate sqlGenerate = new MySqlGenerate();
        Map<String, String> result1 = sqlGenerate.customSearchJsonToSql(likeJson, new HashMap<>());
        System.out.println("SQL: " + result1.get("customWhere"));

        System.out.println("\n场景2：精确匹配手机号");
        String eqJson = "{\"g\":[{\"t\":\"AND\",\"c\":[{\"s\":[{\"f\":\"TRADER_CONTACT_MOBILE\",\"o\":\"EQ\",\"t\":\"AND\",\"v\":\"13812345678\"}]}]}]}";
        System.out.println("JSON: " + eqJson);

        Map<String, String> result2 = sqlGenerate.customSearchJsonToSql(eqJson, new HashMap<>());
        System.out.println("SQL: " + result2.get("customWhere"));

        System.out.println("\n场景3：手机号或电话号模糊匹配（OR 连接）");
        String orJson = "{\"g\":[{\"t\":\"AND\",\"c\":[{\"s\":[{\"f\":\"TRADER_CONTACT_MOBILE\",\"o\":\"LIKE\",\"t\":\"AND\",\"v\":\"138\"},{\"f\":\"TRADER_CONTACT_TELEPHONE\",\"o\":\"LIKE\",\"t\":\"OR\",\"v\":\"010\"}]}]}]}";
        System.out.println("JSON: " + orJson);

        Map<String, String> result3 = sqlGenerate.customSearchJsonToSql(orJson, new HashMap<>());
        System.out.println("SQL: " + result3.get("customWhere"));

        System.out.println("\n✅ 推荐格式生成完成\n");
    }
}
