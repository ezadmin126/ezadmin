package top.ezadmin;

import org.junit.Before;
import org.junit.Test;
import top.ezadmin.common.enums.JdbcTypeEnum;
import top.ezadmin.common.enums.OperatorEnum;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.JsoupUtil;
import top.ezadmin.dao.model.CustomSearchDTO;
import top.ezadmin.dao.model.CustomSearchGroup;
import top.ezadmin.dao.model.CustomSearchOrder;
import top.ezadmin.dao.model.CustomSearchSingle;
import top.ezadmin.plugins.sqlgenerate.MySqlGenerate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * customSearchJsonToSql 方法的单元测试
 */
public class CustomSearchJsonToSqlTest {

    private MySqlGenerate sqlGenerate;
    private Map<String, Map<String, Object>> searchNameMap;

    @Before
    public void setUp() {
        sqlGenerate = new MySqlGenerate();

        // 构建 searchNameMap，模拟搜索字段配置
        searchNameMap = new HashMap<>();

        // 模拟一个用户名字段配置 (VARCHAR类型)
        Map<String, Object> usernameSearch = new HashMap<>();
        usernameSearch.put(JsoupUtil.ITEM_NAME, "username");
        usernameSearch.put(JsoupUtil.JDBCTYPE, JdbcTypeEnum.VARCHAR.getName());
        usernameSearch.put(JsoupUtil.ALIAS, "u");
        searchNameMap.put("username", usernameSearch);

        // 模拟一个年龄字段配置 (NUMBER类型)
        Map<String, Object> ageSearch = new HashMap<>();
        ageSearch.put(JsoupUtil.ITEM_NAME, "age");
        ageSearch.put(JsoupUtil.JDBCTYPE, JdbcTypeEnum.NUMBER.getName());
        ageSearch.put(JsoupUtil.ALIAS, "u");
        searchNameMap.put("age", ageSearch);

        // 模拟一个状态字段配置 (NUMBER类型，无别名)
        Map<String, Object> statusSearch = new HashMap<>();
        statusSearch.put(JsoupUtil.ITEM_NAME, "status");
        statusSearch.put(JsoupUtil.JDBCTYPE, JdbcTypeEnum.NUMBER.getName());
        statusSearch.put(JsoupUtil.ALIAS, "");
        searchNameMap.put("status", statusSearch);

        // 模拟一个创建时间字段配置 (DATETIME类型)
        Map<String, Object> createTimeSearch = new HashMap<>();
        createTimeSearch.put(JsoupUtil.ITEM_NAME, "create_time");
        createTimeSearch.put(JsoupUtil.JDBCTYPE, JdbcTypeEnum.DATETIME.getName());
        createTimeSearch.put(JsoupUtil.ALIAS, "");
        searchNameMap.put("create_time", createTimeSearch);
    }

    /**
     * 测试空JSON或null的情况
     */
    @Test
    public void testNullOrEmptyJson() {
        System.out.println("===== 测试 null 或空 JSON =====");

        // 测试 null
        Map<String, String> result1 = sqlGenerate.customSearchJsonToSql(null, searchNameMap);
        assertNotNull("结果不应该为null", result1);
        assertEquals("customWhere应该为空字符串", "", result1.get("customWhere"));
        assertEquals("customOrder应该为空字符串", " ", result1.get("customOrder"));
        System.out.println("null测试通过");

        // 测试空字符串
        Map<String, String> result2 = sqlGenerate.customSearchJsonToSql("", searchNameMap);
        assertNotNull("结果不应该为null", result2);
        assertEquals("customWhere应该为空字符串", "", result2.get("customWhere"));
        assertEquals("customOrder应该为空字符串", " ", result2.get("customOrder"));
        System.out.println("空字符串测试通过");

        // 测试空白字符串
        Map<String, String> result3 = sqlGenerate.customSearchJsonToSql("   ", searchNameMap);
        assertNotNull("结果不应该为null", result3);
        assertEquals("customWhere应该为空字符串", "", result3.get("customWhere"));
        assertEquals("customOrder应该为空字符串", " ", result3.get("customOrder"));
        System.out.println("空白字符串测试通过");

        System.out.println("✅ null或空JSON测试通过\n");
    }

    /**
     * 测试单个简单搜索条件（等于）
     */
    @Test
    public void testSingleSearchConditionEq() {
        System.out.println("===== 测试单个搜索条件（等于） =====");

        CustomSearchDTO dto = new CustomSearchDTO();
        List<CustomSearchSingle> singleList = new ArrayList<>();

        CustomSearchSingle single = new CustomSearchSingle();
        single.setT("AND");
        single.setF("username");
        single.setO("EQ");  // 使用枚举名称而不是符号
        single.setV("admin");
        singleList.add(single);

        dto.setS(singleList);

        String json = JSONUtils.toJSONString(dto);
        System.out.println("JSON: " + json);

        Map<String, String> result = sqlGenerate.customSearchJsonToSql(json, searchNameMap);
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应该为null", customWhere);
        assertTrue("应该包含字段名", customWhere.contains("username"));
        assertTrue("应该包含值", customWhere.contains("admin"));
        assertTrue("应该包含等号", customWhere.contains("="));

        System.out.println("✅ 单个搜索条件测试通过\n");
    }

    /**
     * 测试多个搜索条件（AND连接）
     * <p>
     * 注意：当前实现中，GTE/LTE/NE 操作符在 customSearchJsonToSql 中都会被转换为 =
     * （参见 SqlGenerate.java:223-227，它们都调用 eq 方法）
     */
    @Test
    public void testMultipleSearchConditionsWithAnd() {
        System.out.println("===== 测试多个搜索条件（AND） =====");

        CustomSearchDTO dto = new CustomSearchDTO();
        List<CustomSearchSingle> singleList = new ArrayList<>();

        // 第一个条件: username = 'admin'
        CustomSearchSingle single1 = new CustomSearchSingle();
        single1.setT("AND");
        single1.setF("username");
        single1.setO("EQ");  // 使用枚举名称
        single1.setV("admin");
        singleList.add(single1);

        // 第二个条件: age >= 18（注意：当前实现会生成 age = 18）
        CustomSearchSingle single2 = new CustomSearchSingle();
        single2.setT("AND");
        single2.setF("age");
        single2.setO(OperatorEnum.GTE.name());  // 使用枚举名称（GTE），不是操作符符号（>=）
        single2.setV("18");
        singleList.add(single2);

        // 第三个条件: status = 1
        CustomSearchSingle single3 = new CustomSearchSingle();
        single3.setT("AND");
        single3.setF("status");
        single3.setO("EQ");  // 使用枚举名称
        single3.setV("1");
        singleList.add(single3);

        dto.setS(singleList);

        String json = JSONUtils.toJSONString(dto);
        System.out.println("JSON: " + json);

        Map<String, String> result = sqlGenerate.customSearchJsonToSql(json, searchNameMap);
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应该为null", customWhere);
        assertTrue("应该包含username", customWhere.contains("username"));
        assertTrue("应该包含age", customWhere.contains("age"));
        assertTrue("应该包含status", customWhere.contains("status"));
        assertTrue("应该包含AND连接", customWhere.toUpperCase().contains("AND"));

        System.out.println("✅ 多个搜索条件（AND）测试通过\n");
    }

    /**
     * 测试多个搜索条件（OR连接）
     */
    @Test
    public void testMultipleSearchConditionsWithOr() {
        System.out.println("===== 测试多个搜索条件（OR） =====");

        CustomSearchDTO dto = new CustomSearchDTO();
        List<CustomSearchSingle> singleList = new ArrayList<>();

        // 第一个条件: username = 'admin'
        CustomSearchSingle single1 = new CustomSearchSingle();
        single1.setT("AND");
        single1.setF("username");
        single1.setO("EQ");  // 使用枚举名称
        single1.setV("admin");
        singleList.add(single1);

        // 第二个条件: username = 'user'（OR连接）
        CustomSearchSingle single2 = new CustomSearchSingle();
        single2.setT("OR");
        single2.setF("username");
        single2.setO("EQ");  // 使用枚举名称
        single2.setV("user");
        singleList.add(single2);

        dto.setS(singleList);

        String json = JSONUtils.toJSONString(dto);
        System.out.println("JSON: " + json);

        Map<String, String> result = sqlGenerate.customSearchJsonToSql(json, searchNameMap);
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应该为null", customWhere);
        assertTrue("应该包含OR连接", customWhere.toUpperCase().contains("OR"));

        System.out.println("✅ 多个搜索条件（OR）测试通过\n");
    }

    /**
     * 测试LIKE操作符
     */
    @Test
    public void testLikeOperator() {
        System.out.println("===== 测试 LIKE 操作符 =====");

        CustomSearchDTO dto = new CustomSearchDTO();
        List<CustomSearchSingle> singleList = new ArrayList<>();

        CustomSearchSingle single = new CustomSearchSingle();
        single.setT("AND");
        single.setF("username");
        single.setO("LIKE");  // 使用枚举名称
        single.setV("admin");
        singleList.add(single);

        dto.setS(singleList);

        String json = JSONUtils.toJSONString(dto);
        System.out.println("JSON: " + json);

        Map<String, String> result = sqlGenerate.customSearchJsonToSql(json, searchNameMap);
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应该为null", customWhere);
        assertTrue("应该包含LIKE", customWhere.toUpperCase().contains("LIKE"));
        assertTrue("应该包含concat函数", customWhere.contains("concat"));

        System.out.println("✅ LIKE操作符测试通过\n");
    }

    /**
     * 测试IN操作符
     */
    @Test
    public void testInOperator() {
        System.out.println("===== 测试 IN 操作符 =====");

        CustomSearchDTO dto = new CustomSearchDTO();
        List<CustomSearchSingle> singleList = new ArrayList<>();

        CustomSearchSingle single = new CustomSearchSingle();
        single.setT("AND");
        single.setF("status");
        single.setO("IN");  // 使用枚举名称
        single.setV("1,2,3");
        singleList.add(single);

        dto.setS(singleList);

        String json = JSONUtils.toJSONString(dto);
        System.out.println("JSON: " + json);

        Map<String, String> result = sqlGenerate.customSearchJsonToSql(json, searchNameMap);
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应该为null", customWhere);
        assertTrue("应该包含IN", customWhere.toUpperCase().contains("IN"));
        assertTrue("应该包含括号", customWhere.contains("(") && customWhere.contains(")"));

        System.out.println("✅ IN操作符测试通过\n");
    }

    /**
     * 测试分组条件（简单分组）
     */
    @Test
    public void testGroupCondition() {
        System.out.println("===== 测试分组条件 =====");

        CustomSearchDTO dto = new CustomSearchDTO();

        // 创建一个分组
        List<CustomSearchGroup> groupList = new ArrayList<>();
        CustomSearchGroup group = new CustomSearchGroup();
        group.setT("AND");

        // 分组内的子条件
        List<CustomSearchDTO> childList = new ArrayList<>();
        CustomSearchDTO child = new CustomSearchDTO();

        List<CustomSearchSingle> singleList = new ArrayList<>();
        CustomSearchSingle single1 = new CustomSearchSingle();
        single1.setT("AND");
        single1.setF("age");
        single1.setO("GTE");  // 使用枚举名称
        single1.setV("18");
        singleList.add(single1);

        CustomSearchSingle single2 = new CustomSearchSingle();
        single2.setT("OR");
        single2.setF("status");
        single2.setO("EQ");  // 使用枚举名称
        single2.setV("1");
        singleList.add(single2);

        child.setS(singleList);


        childList.add(child);

        group.setC(childList);
        groupList.add(group);
        groupList.add(group);

        dto.setG(groupList);

        String json = JSONUtils.toJSONString(dto);
        System.out.println("JSON: " + json);

        Map<String, String> result = sqlGenerate.customSearchJsonToSql(json, searchNameMap);
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应该为null", customWhere);
        assertTrue("应该包含括号", customWhere.contains("(") && customWhere.contains(")"));
        assertTrue("应该包含age", customWhere.contains("age"));
        assertTrue("应该包含status", customWhere.contains("status"));

        System.out.println("✅ 分组条件测试通过\n");
    }

    /**
     * 测试嵌套分组条件
     */
    @Test
    public void testNestedGroupCondition() {
        System.out.println("===== 测试嵌套分组条件 =====");

        CustomSearchDTO dto = new CustomSearchDTO();

        // 外层分组
        List<CustomSearchGroup> outerGroupList = new ArrayList<>();
        CustomSearchGroup outerGroup = new CustomSearchGroup();
        outerGroup.setT("AND");

        List<CustomSearchDTO> outerChildList = new ArrayList<>();
        CustomSearchDTO outerChild = new CustomSearchDTO();

        // 内层分组
        List<CustomSearchGroup> innerGroupList = new ArrayList<>();
        CustomSearchGroup innerGroup = new CustomSearchGroup();
        innerGroup.setT("OR");

        List<CustomSearchDTO> innerChildList = new ArrayList<>();
        CustomSearchDTO innerChild = new CustomSearchDTO();

        List<CustomSearchSingle> innerSingleList = new ArrayList<>();
        CustomSearchSingle innerSingle = new CustomSearchSingle();
        innerSingle.setT("AND");
        innerSingle.setF("username");
        innerSingle.setO("EQ");  // 使用枚举名称
        innerSingle.setV("admin");
        innerSingleList.add(innerSingle);

        innerChild.setS(innerSingleList);
        innerChildList.add(innerChild);
        innerGroup.setC(innerChildList);
        innerGroupList.add(innerGroup);

        outerChild.setG(innerGroupList);
        outerChildList.add(outerChild);
        outerGroup.setC(outerChildList);
        outerGroupList.add(outerGroup);

        dto.setG(outerGroupList);

        String json = JSONUtils.toJSONString(dto);
        System.out.println("JSON: " + json);

        Map<String, String> result = sqlGenerate.customSearchJsonToSql(json, searchNameMap);
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应该为null", customWhere);
        assertTrue("应该包含嵌套括号", customWhere.contains("("));

        System.out.println("✅ 嵌套分组条件测试通过\n");
    }

    /**
     * 测试单个排序条件
     */
    @Test
    public void testSingleOrderBy() {
        System.out.println("===== 测试单个排序条件 =====");

        CustomSearchDTO dto = new CustomSearchDTO();
        List<CustomSearchOrder> orderList = new ArrayList<>();

        CustomSearchOrder order = new CustomSearchOrder();
        order.setF("create_time");
        order.setO("DESC");
        orderList.add(order);

        dto.setO(orderList);

        String json = JSONUtils.toJSONString(dto);
        System.out.println("JSON: " + json);

        Map<String, String> result = sqlGenerate.customSearchJsonToSql(json, searchNameMap);
        String customOrder = result.get("customOrder");

        System.out.println("生成的 ORDER BY: " + customOrder);

        assertNotNull("customOrder不应该为null", customOrder);
        assertTrue("应该包含ORDER BY", customOrder.toUpperCase().contains("ORDER BY"));
        assertTrue("应该包含create_time", customOrder.contains("create_time"));
        assertTrue("应该包含DESC", customOrder.toUpperCase().contains("DESC"));

        System.out.println("✅ 单个排序条件测试通过\n");
    }

    /**
     * 测试多个排序条件
     */
    @Test
    public void testMultipleOrderBy() {
        System.out.println("===== 测试多个排序条件 =====");

        CustomSearchDTO dto = new CustomSearchDTO();
        List<CustomSearchOrder> orderList = new ArrayList<>();

        CustomSearchOrder order1 = new CustomSearchOrder();
        order1.setF("status");
        order1.setO("ASC");
        orderList.add(order1);

        CustomSearchOrder order2 = new CustomSearchOrder();
        order2.setF("create_time");
        order2.setO("DESC");
        orderList.add(order2);

        dto.setO(orderList);

        String json = JSONUtils.toJSONString(dto);
        System.out.println("JSON: " + json);

        Map<String, String> result = sqlGenerate.customSearchJsonToSql(json, searchNameMap);
        String customOrder = result.get("customOrder");

        System.out.println("生成的 ORDER BY: " + customOrder);

        assertNotNull("customOrder不应该为null", customOrder);
        assertTrue("应该包含ORDER BY", customOrder.toUpperCase().contains("ORDER BY"));
        assertTrue("应该包含status", customOrder.contains("status"));
        assertTrue("应该包含create_time", customOrder.contains("create_time"));
        assertTrue("应该包含逗号分隔", customOrder.contains(","));

        System.out.println("✅ 多个排序条件测试通过\n");
    }

    /**
     * 测试综合场景（搜索条件 + 分组 + 排序）
     */
    @Test
    public void testComplexScenario() {
        System.out.println("===== 测试综合场景（搜索 + 分组 + 排序） =====");

        CustomSearchDTO dto = new CustomSearchDTO();

        // 添加单个搜索条件
        List<CustomSearchSingle> singleList = new ArrayList<>();
        CustomSearchSingle single = new CustomSearchSingle();
        single.setT("AND");
        single.setF("status");
        single.setO("EQ");  // 使用枚举名称
        single.setV("1");
        singleList.add(single);
        dto.setS(singleList);

        // 添加分组条件
        List<CustomSearchGroup> groupList = new ArrayList<>();
        CustomSearchGroup group = new CustomSearchGroup();
        group.setT("AND");

        List<CustomSearchDTO> childList = new ArrayList<>();
        CustomSearchDTO child = new CustomSearchDTO();

        List<CustomSearchSingle> childSingleList = new ArrayList<>();
        CustomSearchSingle childSingle = new CustomSearchSingle();
        childSingle.setT("OR");
        childSingle.setF("username");
        childSingle.setO("LIKE");  // 使用枚举名称
        childSingle.setV("admin");
        childSingleList.add(childSingle);

        child.setS(childSingleList);
        childList.add(child);
        group.setC(childList);
        groupList.add(group);
        dto.setG(groupList);

        // 添加排序条件
        List<CustomSearchOrder> orderList = new ArrayList<>();
        CustomSearchOrder order = new CustomSearchOrder();
        order.setF("create_time");
        order.setO("DESC");
        orderList.add(order);
        dto.setO(orderList);

        String json = JSONUtils.toJSONString(dto);
        System.out.println("JSON: " + json);

        Map<String, String> result = sqlGenerate.customSearchJsonToSql(json, searchNameMap);
        String customWhere = result.get("customWhere");
        String customOrder = result.get("customOrder");

        System.out.println("生成的 WHERE: " + customWhere);
        System.out.println("生成的 ORDER BY: " + customOrder);

        assertNotNull("customWhere不应该为null", customWhere);
        assertNotNull("customOrder不应该为null", customOrder);
        assertTrue("WHERE应该包含status", customWhere.contains("status"));
        assertTrue("WHERE应该包含username", customWhere.contains("username"));
        assertTrue("ORDER BY应该包含create_time", customOrder.contains("create_time"));

        System.out.println("✅ 综合场景测试通过\n");
    }

    /**
     * 测试异常JSON格式（应该捕获异常并返回空结果）
     */
    @Test
    public void testInvalidJson() {
        System.out.println("===== 测试异常JSON格式 =====");

        String invalidJson = "{invalid json}";

        // customSearchJsonToSql 方法中有 try-catch，会捕获异常并打印堆栈，然后返回空结果
        // 虽然会打印异常堆栈，但不会抛出异常
        Map<String, String> result = sqlGenerate.customSearchJsonToSql(invalidJson, searchNameMap);

        assertNotNull("结果不应该为null", result);
        assertEquals("customWhere应该为空", "", result.get("customWhere"));
        assertEquals("customOrder应该为空", " ", result.get("customOrder"));

        System.out.println("✅ 异常JSON测试通过（异常已被捕获）\n");
    }

    /**
     * 测试空的searchNameMap
     */
    @Test
    public void testEmptySearchNameMap() {
        System.out.println("===== 测试空的 searchNameMap =====");

        CustomSearchDTO dto = new CustomSearchDTO();
        List<CustomSearchSingle> singleList = new ArrayList<>();

        CustomSearchSingle single = new CustomSearchSingle();
        single.setT("AND");
        single.setF("unknown_field");
        single.setO("EQ");  // 使用枚举名称
        single.setV("value");
        singleList.add(single);

        dto.setS(singleList);

        String json = JSONUtils.toJSONString(dto);

        // 使用空的 searchNameMap
        Map<String, String> result = sqlGenerate.customSearchJsonToSql(json, new HashMap<>());
        String customWhere = result.get("customWhere");

        System.out.println("生成的 WHERE: " + customWhere);

        assertNotNull("customWhere不应该为null", customWhere);
        // 即使字段不在 searchNameMap 中，也应该生成 SQL（只是没有 jdbcType 和 alias）
        assertTrue("应该包含字段名", customWhere.contains("unknown_field"));

        System.out.println("✅ 空searchNameMap测试通过\n");
    }

    /**
     * 测试null的searchNameMap
     */
    @Test
    public void testNullSearchNameMap() {
        System.out.println("===== 测试 null 的 searchNameMap =====");

        CustomSearchDTO dto = new CustomSearchDTO();
        List<CustomSearchSingle> singleList = new ArrayList<>();

        CustomSearchSingle single = new CustomSearchSingle();
        single.setT("AND");
        single.setF("field");
        single.setO("EQ");  // 使用枚举名称
        single.setV("value");
        singleList.add(single);

        dto.setS(singleList);

        String json = JSONUtils.toJSONString(dto);

        // 使用 null 的 searchNameMap，不应该抛出异常
        Map<String, String> result = sqlGenerate.customSearchJsonToSql(json, null);

        assertNotNull("结果不应该为null", result);
        assertNotNull("customWhere不应该为null", result.get("customWhere"));

        System.out.println("✅ null searchNameMap测试通过\n");
    }

    /**
     * 测试空的order字段（f或o为空）
     */
    @Test
    public void testEmptyOrderFields() {
        System.out.println("===== 测试空的排序字段 =====");

        CustomSearchDTO dto = new CustomSearchDTO();
        List<CustomSearchOrder> orderList = new ArrayList<>();

        // f 为空
        CustomSearchOrder order1 = new CustomSearchOrder();
        order1.setF("");
        order1.setO("DESC");
        orderList.add(order1);

        // o 为空
        CustomSearchOrder order2 = new CustomSearchOrder();
        order2.setF("create_time");
        order2.setO("");
        orderList.add(order2);

        // 正常的排序
        CustomSearchOrder order3 = new CustomSearchOrder();
        order3.setF("status");
        order3.setO("ASC");
        orderList.add(order3);

        dto.setO(orderList);

        String json = JSONUtils.toJSONString(dto);
        System.out.println("JSON: " + json);

        Map<String, String> result = sqlGenerate.customSearchJsonToSql(json, searchNameMap);
        String customOrder = result.get("customOrder");

        System.out.println("生成的 ORDER BY: " + customOrder);

        // 应该只包含有效的排序（order3）
        assertTrue("应该包含ORDER BY", customOrder.toUpperCase().contains("ORDER BY"));
        assertTrue("应该包含status", customOrder.contains("status"));
        assertFalse("不应该包含create_time（因为o为空）", customOrder.contains("create_time"));

        System.out.println("✅ 空排序字段测试通过\n");
    }
}
