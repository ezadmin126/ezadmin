package top.ezadmin;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import top.ezadmin.common.utils.TreeUtil;
import top.ezadmin.dao.model.TreeConfig;

import java.util.*;

/**
 * TreeUtil 单元测试
 * <p>
 * 数据结构（平铺，顺序即为原始顺序）：
 * <pre>
 * 设备(1)
 *   └─ 设备A(3)
 *        └─ 设备A1(5)
 *   └─ 设备B(6)         ← 在原始列表中排在设备A之后
 * 耗材(2)
 *   └─ 耗材A(4)
 *        ├─ 耗材A1(7)
 *        └─ 耗材A2(8)
 * </pre>
 */
public class TreeUtilTest {

    private List<Map<String, Object>> dataList;
    private TreeConfig config;

    @Before
    public void setUp() {
        config = new TreeConfig();
        config.setTreeId("VALUE");
        config.setTreePid("PARENT_ID");
        config.setTreeLabel("LABEL");
        config.setTreeChildren("CHILDREN");
        config.setTreeIsParent("IS_PARENT");
        config.setRootPid("0");

        dataList = new ArrayList<>();
        dataList.add(node(1, 0, "设备"));
        dataList.add(node(2, 0, "耗材"));
        dataList.add(node(3, 1, "设备A"));
        dataList.add(node(4, 2, "耗材A"));
        dataList.add(node(5, 3, "设备A1"));
        dataList.add(node(6, 1, "设备B"));   // 同一父节点下，顺序在设备A之后
        dataList.add(node(7, 4, "耗材A1"));
        dataList.add(node(8, 4, "耗材A2"));
    }

    // ------------------------------------------------------------------
    // build 基本构建
    // ------------------------------------------------------------------

    @Test
    public void testBuild_rootCount() {
        List<Map<String, Object>> result = TreeUtil.build(dataList, config);
        Assert.assertEquals("根节点应有 2 个", 2, result.size());
    }

    @Test
    public void testBuild_rootOrder() {
        List<Map<String, Object>> result = TreeUtil.build(dataList, config);
        Assert.assertEquals("根节点第 1 个应为「设备」", "设备", label(result.get(0)));
        Assert.assertEquals("根节点第 2 个应为「耗材」", "耗材", label(result.get(1)));
    }

    @Test
    public void testBuild_childrenOrder() {
        List<Map<String, Object>> result = TreeUtil.build(dataList, config);
        List<Map<String, Object>> shebeiChildren = children(result.get(0));
        Assert.assertEquals("「设备」应有 2 个子节点", 2, shebeiChildren.size());
        Assert.assertEquals("「设备」第 1 个子节点应为「设备A」", "设备A", label(shebeiChildren.get(0)));
        Assert.assertEquals("「设备」第 2 个子节点应为「设备B」", "设备B", label(shebeiChildren.get(1)));
    }

    @Test
    public void testBuild_grandChildren() {
        List<Map<String, Object>> result = TreeUtil.build(dataList, config);
        List<Map<String, Object>> haocaiAChildren = children(children(result.get(1)).get(0));
        Assert.assertEquals("「耗材A」应有 2 个子节点", 2, haocaiAChildren.size());
        Assert.assertEquals("「耗材A」第 1 个子节点应为「耗材A1」", "耗材A1", label(haocaiAChildren.get(0)));
        Assert.assertEquals("「耗材A」第 2 个子节点应为「耗材A2」", "耗材A2", label(haocaiAChildren.get(1)));
    }

    @Test
    public void testBuild_isParentFlag() {
        List<Map<String, Object>> result = TreeUtil.build(dataList, config);
        Assert.assertEquals("有子节点的节点 IS_PARENT 应为 true", true, result.get(0).get("IS_PARENT"));
        // 叶子节点不应有 children
        Map<String, Object> shebeiA = children(result.get(0)).get(0);  // 设备A
        Map<String, Object> shebeiA1 = children(shebeiA).get(0);       // 设备A1（叶子）
        List<Map<String, Object>> shebeiA1Children = children(shebeiA1);
        Assert.assertTrue("叶子节点不应有 children", shebeiA1Children == null || shebeiA1Children.isEmpty());
    }

    @Test
    public void testBuild_emptyList() {
        List<Map<String, Object>> result = TreeUtil.build(new ArrayList<>(), config);
        Assert.assertTrue("空列表应返回空结果", result.isEmpty());
    }

    @Test
    public void testBuild_repeatCall_noAccumulation() {
        // 第一次构建
        TreeUtil.build(dataList, config);
        // 第二次构建，children 不应累积
        List<Map<String, Object>> result = TreeUtil.build(dataList, config);
        List<Map<String, Object>> shebeiChildren = children(result.get(0));
        Assert.assertEquals("重复调用后「设备」子节点数量应仍为 2", 2, shebeiChildren.size());
    }

    // ------------------------------------------------------------------
    // 原始顺序保留（核心修复验证）
    // ------------------------------------------------------------------

    @Test
    public void testBuild_preservesOriginalOrder_reversedInput() {
        // 将原始列表倒序排列后构建，验证树中顺序跟随输入
        List<Map<String, Object>> reversed = new ArrayList<>(dataList);
        Collections.reverse(reversed);
        List<Map<String, Object>> result = TreeUtil.build(reversed, config);

        // 倒序输入后，根节点应为「耗材」在前、「设备」在后
        Assert.assertEquals("倒序输入时根节点第 1 个应为「耗材」", "耗材", label(result.get(0)));
        Assert.assertEquals("倒序输入时根节点第 2 个应为「设备」", "设备", label(result.get(1)));
    }

    @Test
    public void testBuild_childrenFollowInputOrder() {
        // 把设备B插到设备A前面，验证子节点顺序与输入一致
        List<Map<String, Object>> customOrder = new ArrayList<>();
        customOrder.add(node(1, 0, "设备"));
        customOrder.add(node(2, 0, "耗材"));
        customOrder.add(node(6, 1, "设备B"));   // 设备B 先于 设备A
        customOrder.add(node(3, 1, "设备A"));
        customOrder.add(node(5, 3, "设备A1"));
        customOrder.add(node(4, 2, "耗材A"));
        customOrder.add(node(7, 4, "耗材A1"));
        customOrder.add(node(8, 4, "耗材A2"));

        List<Map<String, Object>> result = TreeUtil.build(customOrder, config);
        List<Map<String, Object>> shebeiChildren = children(result.get(0));
        Assert.assertEquals("输入中设备B在前，子节点第 1 个应为「设备B」", "设备B", label(shebeiChildren.get(0)));
        Assert.assertEquals("输入中设备A在后，子节点第 2 个应为「设备A」", "设备A", label(shebeiChildren.get(1)));
    }

    // ------------------------------------------------------------------
    // 关键词过滤
    // ------------------------------------------------------------------

    @Test
    public void testBuild_filterNoKeyword() {
        List<Map<String, Object>> result = TreeUtil.build(dataList, config, "");
        Assert.assertEquals("空关键词应返回完整树根节点 2 个", 2, result.size());
    }

    @Test
    public void testBuild_filterMatchRoot() {
        // 「设备」匹配根节点，整棵子树应保留
        List<Map<String, Object>> result = TreeUtil.build(dataList, config, "设备");
        Assert.assertEquals("搜索「设备」应只返回 1 个根节点", 1, result.size());
        Assert.assertEquals("应为「设备」", "设备", label(result.get(0)));
        // 子树完整保留
        Assert.assertEquals("「设备」子节点应全部保留", 2, children(result.get(0)).size());
    }

    @Test
    public void testBuild_filterMatchLeaf() {
        // 「设备A1」只匹配叶子节点，应保留其祖先路径
        List<Map<String, Object>> result = TreeUtil.build(dataList, config, "设备A1");
        Assert.assertEquals("搜索「设备A1」应返回 1 个根节点", 1, result.size());
        Assert.assertEquals("根节点应为「设备」（祖先路径保留）", "设备", label(result.get(0)));

        List<Map<String, Object>> lv2 = children(result.get(0));
        Assert.assertEquals("「设备」下应只剩 1 个子节点（设备B 不含匹配）", 1, lv2.size());
        Assert.assertEquals("二级节点应为「设备A」", "设备A", label(lv2.get(0)));

        List<Map<String, Object>> lv3 = children(lv2.get(0));
        Assert.assertEquals("三级节点应为「设备A1」", "设备A1", label(lv3.get(0)));
    }

    @Test
    public void testBuild_filterMatchMultiple() {
        // 「A」同时匹配多个节点
        List<Map<String, Object>> result = TreeUtil.build(dataList, config, "A");
        Assert.assertEquals("搜索「A」应返回 2 个根节点", 2, result.size());
    }

    @Test
    public void testBuild_filterMatchParentKeepsAllChildren() {
        // 「耗材A」匹配中间节点，其所有子节点应完整保留（不再递归过滤子树）
        List<Map<String, Object>> result = TreeUtil.build(dataList, config, "耗材A");
        List<Map<String, Object>> haocaiA = children(children(result.get(0)).get(0));
        Assert.assertEquals("「耗材A」匹配，其 2 个子节点均应保留", 2, haocaiA.size());
    }

    @Test
    public void testBuild_filterNoMatch() {
        List<Map<String, Object>> result = TreeUtil.build(dataList, config, "不存在的关键词XYZ");
        Assert.assertTrue("无匹配时应返回空列表", result.isEmpty());
    }

    // ------------------------------------------------------------------
    // filter 独立方法
    // ------------------------------------------------------------------

    @Test
    public void testFilter_standalone() {
        List<Map<String, Object>> tree = TreeUtil.build(dataList, config);
        List<Map<String, Object>> filtered = TreeUtil.filter(tree, "设备B", config);
        Assert.assertEquals("过滤「设备B」应返回 1 个根节点", 1, filtered.size());
        List<Map<String, Object>> lv2 = children(filtered.get(0));
        Assert.assertEquals("「设备」下应只剩「设备B」", 1, lv2.size());
        Assert.assertEquals("应为「设备B」", "设备B", label(lv2.get(0)));
    }

    @Test
    public void testFilter_blankKeyword_returnsOriginal() {
        List<Map<String, Object>> tree = TreeUtil.build(dataList, config);
        List<Map<String, Object>> filtered = TreeUtil.filter(tree, "  ", config);
        Assert.assertSame("空关键词应原样返回", tree, filtered);
    }

    // ------------------------------------------------------------------
    // 工具方法
    // ------------------------------------------------------------------

    private static Map<String, Object> node(int id, int parentId, String label) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("VALUE", id);
        m.put("PARENT_ID", parentId);
        m.put("LABEL", label);
        return m;
    }

    private static String label(Map<String, Object> node) {
        return (String) node.get("LABEL");
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> children(Map<String, Object> node) {
        return (List<Map<String, Object>>) node.get("CHILDREN");
    }
}
