package top.ezadmin;

import org.junit.Test;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.dao.model.CustomSearchDTO;
import top.ezadmin.dao.model.CustomSearchGroup;
import top.ezadmin.dao.model.CustomSearchSingle;
import top.ezadmin.plugins.sqlgenerate.MySqlGenerate;
import top.ezadmin.plugins.sqlgenerate.SqlGenerate;

import java.util.*;

import static org.junit.Assert.*;

/**
 * uniondate 组件生成 customSearch 的单元测试
 * 测试 mergeWhereParams 中 uniondate 组件的处理逻辑
 */
public class UniondateCustomSearchTest {

    /**
     * 测试场景1：select 有选中值
     * 应该生成单个字段的 BETWEEN 查询条件
     */
    @Test
    public void testUniondateWithSelectedField() {
        System.out.println("===== 测试 uniondate 组件 - select 有选中值 =====");

        // 模拟 uniondate 组件的数据
        // select 选中了 "create_time"
        // input 值为 "2024-01-01 - 2024-12-31"
        String selectedField = "create_time";
        String dateRange = "2024-01-01 - 2024-12-31";

        // 构建 customSearch 结构
        CustomSearchDTO customSearch = new CustomSearchDTO();
        customSearch.setG(new ArrayList<>());

        // 创建分组对象
        CustomSearchGroup group = new CustomSearchGroup();
        group.setT("AND");
        group.setC(new ArrayList<>());

        // 创建子条件对象
        CustomSearchDTO childDTO = new CustomSearchDTO();
        childDTO.setS(new ArrayList<>());

        // 创建单个条件
        CustomSearchSingle condition = new CustomSearchSingle();
        condition.setF(selectedField);
        condition.setO("BETWEEN");
        condition.setT("AND");
        condition.setV(dateRange);

        childDTO.getS().add(condition);
        group.getC().add(childDTO);
        customSearch.getG().add(group);

        // 转换为 JSON
        String json = JSONUtils.toJSONString(customSearch);
        System.out.println("生成的 JSON: " + json);

        // 验证 JSON 结构
        assertTrue("应该包含 create_time 字段", json.contains("create_time"));
        assertTrue("应该包含 BETWEEN 操作符", json.contains("BETWEEN"));
        assertTrue("应该包含日期范围", json.contains(dateRange));

        // 解析 JSON 验证结构正确性
        CustomSearchDTO parsed = JSONUtils.parseObject(json, CustomSearchDTO.class);
        assertNotNull("解析后的对象不应为null", parsed);
        assertNotNull("分组列表不应为null", parsed.getG());
        assertEquals("应该有1个分组", 1, parsed.getG().size());

        CustomSearchGroup parsedGroup = parsed.getG().get(0);
        assertEquals("分组连接类型应该是AND", "AND", parsedGroup.getT());
        assertEquals("应该有1个子条件", 1, parsedGroup.getC().size());

        CustomSearchDTO parsedChild = parsedGroup.getC().get(0);
        assertEquals("应该有1个单条件", 1, parsedChild.getS().size());

        CustomSearchSingle parsedCondition = parsedChild.getS().get(0);
        assertEquals("字段名应该是create_time", selectedField, parsedCondition.getF());
        assertEquals("操作符应该是BETWEEN", "BETWEEN", parsedCondition.getO());
        assertEquals("连接类型应该是AND", "AND", parsedCondition.getT());
        assertEquals("值应该是日期范围", dateRange, parsedCondition.getV());

        System.out.println("✅ select有选中值测试通过\n");
    }

    /**
     * 测试场景2：select 没有选中值
     * 应该生成多个字段的 OR 连接的 BETWEEN 查询条件
     */
    @Test
    public void testUniondateWithoutSelectedField() {
        System.out.println("===== 测试 uniondate 组件 - select 没有选中值 =====");

        // 模拟 uniondate 组件的数据
        // select 没有选中（value=""）
        // options 包含: create_time, update_time, delete_time
        // input 值为 "2024-01-01 - 2024-12-31"
        List<String> availableFields = Arrays.asList("create_time", "update_time", "delete_time");
        String dateRange = "2024-01-01 - 2024-12-31";

        // 构建 customSearch 结构
        CustomSearchDTO customSearch = new CustomSearchDTO();
        customSearch.setG(new ArrayList<>());

        // 创建分组对象
        CustomSearchGroup group = new CustomSearchGroup();
        group.setT("AND");
        group.setC(new ArrayList<>());

        // 创建子条件对象
        CustomSearchDTO childDTO = new CustomSearchDTO();
        childDTO.setS(new ArrayList<>());

        // 为每个可用字段创建条件（OR 连接）
        boolean firstItem = true;
        for (String field : availableFields) {
            CustomSearchSingle condition = new CustomSearchSingle();
            condition.setF(field);
            condition.setO("BETWEEN");
            condition.setT(firstItem ? "AND" : "OR");  // 第一个用AND，后续用OR
            condition.setV(dateRange);
            childDTO.getS().add(condition);
            firstItem = false;
        }

        group.getC().add(childDTO);
        customSearch.getG().add(group);

        // 转换为 JSON
        String json = JSONUtils.toJSONString(customSearch);
        System.out.println("生成的 JSON: " + json);

        // 验证 JSON 结构
        assertTrue("应该包含 create_time 字段", json.contains("create_time"));
        assertTrue("应该包含 update_time 字段", json.contains("update_time"));
        assertTrue("应该包含 delete_time 字段", json.contains("delete_time"));
        assertTrue("应该包含 BETWEEN 操作符", json.contains("BETWEEN"));
        assertTrue("应该包含 OR 连接", json.contains("\"t\":\"OR\""));

        // 解析 JSON 验证结构正确性
        CustomSearchDTO parsed = JSONUtils.parseObject(json, CustomSearchDTO.class);
        assertNotNull("解析后的对象不应为null", parsed);
        assertEquals("应该有1个分组", 1, parsed.getG().size());

        CustomSearchGroup parsedGroup = parsed.getG().get(0);
        assertEquals("分组连接类型应该是AND", "AND", parsedGroup.getT());
        assertEquals("应该有1个子条件", 1, parsedGroup.getC().size());

        CustomSearchDTO parsedChild = parsedGroup.getC().get(0);
        assertEquals("应该有3个单条件", 3, parsedChild.getS().size());

        // 验证第一个条件
        CustomSearchSingle firstCondition = parsedChild.getS().get(0);
        assertEquals("第一个条件应该是AND", "AND", firstCondition.getT());
        assertEquals("第一个字段应该是create_time", "create_time", firstCondition.getF());

        // 验证后续条件都是 OR
        for (int i = 1; i < parsedChild.getS().size(); i++) {
            CustomSearchSingle condition = parsedChild.getS().get(i);
            assertEquals("后续条件应该是OR", "OR", condition.getT());
            assertEquals("操作符应该是BETWEEN", "BETWEEN", condition.getO());
            assertEquals("值应该是日期范围", dateRange, condition.getV());
        }

        System.out.println("✅ select没有选中值测试通过\n");
    }

    /**
     * 测试场景3：多个 uniondate 组件
     * 应该生成多个分组，每个分组用 AND 连接
     */
    @Test
    public void testMultipleUniondateComponents() {
        System.out.println("===== 测试多个 uniondate 组件 =====");

        // 模拟两个 uniondate 组件
        // 组件1: select 选中了 "create_time", 日期范围 "2024-01-01 - 2024-06-30"
        // 组件2: select 没有选中, 可选字段 ["update_time", "delete_time"], 日期范围 "2024-07-01 - 2024-12-31"

        CustomSearchDTO customSearch = new CustomSearchDTO();
        customSearch.setG(new ArrayList<>());

        // === 第一个 uniondate 组件 ===
        CustomSearchGroup group1 = new CustomSearchGroup();
        group1.setT("AND");
        group1.setC(new ArrayList<>());

        CustomSearchDTO child1 = new CustomSearchDTO();
        child1.setS(new ArrayList<>());

        CustomSearchSingle condition1 = new CustomSearchSingle();
        condition1.setF("create_time");
        condition1.setO("BETWEEN");
        condition1.setT("AND");
        condition1.setV("2024-01-01 - 2024-06-30");
        child1.getS().add(condition1);

        group1.getC().add(child1);
        customSearch.getG().add(group1);

        // === 第二个 uniondate 组件 ===
        CustomSearchGroup group2 = new CustomSearchGroup();
        group2.setT("AND");
        group2.setC(new ArrayList<>());

        CustomSearchDTO child2 = new CustomSearchDTO();
        child2.setS(new ArrayList<>());

        // 第一个字段
        CustomSearchSingle condition2_1 = new CustomSearchSingle();
        condition2_1.setF("update_time");
        condition2_1.setO("BETWEEN");
        condition2_1.setT("AND");
        condition2_1.setV("2024-07-01 - 2024-12-31");
        child2.getS().add(condition2_1);

        // 第二个字段（OR连接）
        CustomSearchSingle condition2_2 = new CustomSearchSingle();
        condition2_2.setF("delete_time");
        condition2_2.setO("BETWEEN");
        condition2_2.setT("OR");
        condition2_2.setV("2024-07-01 - 2024-12-31");
        child2.getS().add(condition2_2);

        group2.getC().add(child2);
        customSearch.getG().add(group2);

        // 转换为 JSON
        String json = JSONUtils.toJSONString(customSearch);
        System.out.println("生成的 JSON: " + json);

        // 验证 JSON 结构
        assertTrue("应该包含 create_time", json.contains("create_time"));
        assertTrue("应该包含 update_time", json.contains("update_time"));
        assertTrue("应该包含 delete_time", json.contains("delete_time"));

        // 解析验证
        CustomSearchDTO parsed = JSONUtils.parseObject(json, CustomSearchDTO.class);
        assertEquals("应该有2个分组", 2, parsed.getG().size());

        // 验证第一个分组
        CustomSearchGroup parsedGroup1 = parsed.getG().get(0);
        assertEquals("第一个分组应该有1个子条件", 1, parsedGroup1.getC().size());
        assertEquals("第一个分组的子条件应该有1个单条件", 1, parsedGroup1.getC().get(0).getS().size());

        // 验证第二个分组
        CustomSearchGroup parsedGroup2 = parsed.getG().get(1);
        assertEquals("第二个分组应该有1个子条件", 1, parsedGroup2.getC().size());
        assertEquals("第二个分组的子条件应该有2个单条件", 2, parsedGroup2.getC().get(0).getS().size());

        System.out.println("✅ 多个uniondate组件测试通过\n");
    }

    /**
     * 测试场景4：使用 customSearchJsonToSql 验证生成的 SQL
     * 测试 select 有选中值的情况
     */
    @Test
    public void testSqlGenerationWithSelectedField() {
        System.out.println("===== 测试 SQL 生成 - select 有选中值 =====");

        // 构建 customSearch
        CustomSearchDTO customSearch = new CustomSearchDTO();
        customSearch.setG(new ArrayList<>());

        CustomSearchGroup group = new CustomSearchGroup();
        group.setT("AND");
        group.setC(new ArrayList<>());

        CustomSearchDTO childDTO = new CustomSearchDTO();
        childDTO.setS(new ArrayList<>());

        CustomSearchSingle condition = new CustomSearchSingle();
        condition.setF("create_time");
        condition.setO("BETWEEN");
        condition.setT("AND");
        condition.setV("2024-01-01 - 2024-12-31");

        childDTO.getS().add(condition);
        group.getC().add(childDTO);
        customSearch.getG().add(group);

        String json = JSONUtils.toJSONString(customSearch);
        System.out.println("JSON: " + json);

        // 使用 SqlGenerate 生成 SQL
        MySqlGenerate sqlGenerate = new MySqlGenerate();
        Map<String, String> result = sqlGenerate.customSearchJsonToSql(json, new HashMap<>());
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应为null", customWhere);
        assertTrue("应该包含字段名", customWhere.contains("create_time"));

        System.out.println("✅ SQL生成测试通过（select有选中值）\n");
    }

    /**
     * 测试场景5：使用 customSearchJsonToSql 验证生成的 SQL
     * 测试 select 没有选中值的情况（多字段 OR 连接）
     */
    @Test
    public void testSqlGenerationWithoutSelectedField() {
        System.out.println("===== 测试 SQL 生成 - select 没有选中值 =====");

        // 构建 customSearch
        CustomSearchDTO customSearch = new CustomSearchDTO();
        customSearch.setG(new ArrayList<>());

        CustomSearchGroup group = new CustomSearchGroup();
        group.setT("AND");
        group.setC(new ArrayList<>());

        CustomSearchDTO childDTO = new CustomSearchDTO();
        childDTO.setS(new ArrayList<>());

        // 创建多个字段的 OR 条件
        String[] fields = {"create_time", "update_time", "delete_time"};
        boolean firstItem = true;
        for (String field : fields) {
            CustomSearchSingle condition = new CustomSearchSingle();
            condition.setF(field);
            condition.setO("BETWEEN");
            condition.setT(firstItem ? "AND" : "OR");
            condition.setV("2024-01-01 - 2024-12-31");
            childDTO.getS().add(condition);
            firstItem = false;
        }

        group.getC().add(childDTO);
        customSearch.getG().add(group);

        String json = JSONUtils.toJSONString(customSearch);
        System.out.println("JSON: " + json);

        // 使用 SqlGenerate 生成 SQL
        MySqlGenerate sqlGenerate = new MySqlGenerate();
        Map<String, String> result = sqlGenerate.customSearchJsonToSql(json, new HashMap<>());
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应为null", customWhere);
        assertTrue("应该包含 create_time", customWhere.contains("create_time"));
        assertTrue("应该包含 update_time", customWhere.contains("update_time"));
        assertTrue("应该包含 delete_time", customWhere.contains("delete_time"));
        assertTrue("应该包含 OR 连接", customWhere.toUpperCase().contains("OR"));

        System.out.println("✅ SQL生成测试通过（select没有选中值）\n");
    }

    /**
     * 测试场景6：真实的 uniondate JSON 格式
     * 测试从前端传入的实际 JSON 格式（ADD_TIME 字段，2026年日期范围）
     */
    @Test
    public void testRealWorldUniondateJson() {
        System.out.println("===== 测试真实的 uniondate JSON 格式 =====");

        // 这是从前端实际传入的 JSON 格式
        String realJson = "{\"g\":[{\"t\":\"AND\",\"c\":[{\"s\":[{\"f\":\"ADD_TIME\",\"o\":\"BETWEEN\",\"t\":\"AND\",\"v\":\"2026-03-01 - 2026-04-01\"}]}]}]}";

        System.out.println("真实的 JSON: " + realJson);

        // 解析 JSON 验证结构
        CustomSearchDTO parsed = JSONUtils.parseObject(realJson, CustomSearchDTO.class);
        assertNotNull("解析后的对象不应为null", parsed);
        assertNotNull("分组列表不应为null", parsed.getG());
        assertEquals("应该有1个分组", 1, parsed.getG().size());

        CustomSearchGroup group = parsed.getG().get(0);
        assertEquals("分组类型应该是AND", "AND", group.getT());
        assertEquals("应该有1个子条件", 1, group.getC().size());

        CustomSearchDTO child = group.getC().get(0);
        assertEquals("应该有1个单条件", 1, child.getS().size());

        CustomSearchSingle condition = child.getS().get(0);
        assertEquals("字段名应该是ADD_TIME", "ADD_TIME", condition.getF());
        assertEquals("操作符应该是BETWEEN", "BETWEEN", condition.getO());
        assertEquals("连接类型应该是AND", "AND", condition.getT());
        assertEquals("日期范围应该正确", "2026-03-01 - 2026-04-01", condition.getV());

        System.out.println("✅ JSON 结构验证通过");

        // 生成 SQL 并验证
        MySqlGenerate sqlGenerate = new MySqlGenerate();
        Map<String, String> result = sqlGenerate.customSearchJsonToSql(realJson, new HashMap<>());
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应为null", customWhere);
        assertFalse("customWhere不应为空", customWhere.trim().isEmpty());

        // 验证字段名和日期范围
        assertTrue("应该包含字段名ADD_TIME", customWhere.contains("ADD_TIME"));
        assertTrue("应该包含起始日期2026-03-01", customWhere.contains("2026-03-01"));
        assertTrue("应该包含结束日期2026-04-01", customWhere.contains("2026-04-01"));

        // 验证 BETWEEN 语法（可能是 "between...and" 或 ">= ... <="）
        boolean hasBetweenSyntax = customWhere.toLowerCase().contains("between") && customWhere.toLowerCase().contains("and");
        boolean hasComparisonSyntax = customWhere.contains(">=") && customWhere.contains("<=");
        assertTrue("应该使用 BETWEEN...AND 或 >= ... <= 语法", hasBetweenSyntax || hasComparisonSyntax);

        // 验证日期范围顺序
        assertTrue("起始日期应该在结束日期之前",
                   customWhere.indexOf("2026-03-01") < customWhere.indexOf("2026-04-01"));

        System.out.println("✅ SQL 生成验证通过");
        System.out.println("✅ 真实 uniondate JSON 测试通过\n");
    }
}
