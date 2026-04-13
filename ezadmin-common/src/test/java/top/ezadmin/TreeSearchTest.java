package top.ezadmin;

import org.junit.Test;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.dao.model.TreeConfig;

import java.util.*;

public class TreeSearchTest {

    // 简单的树打印方法
    private void printTree(List<Map<String, Object>> nodes, String indent) {
        if (nodes == null) return;
        for (Map<String, Object> node : nodes) {
            System.out.println(indent + "- " + node.get("LABEL") + " (VALUE=" + node.get("VALUE") + ")");
            List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("CHILDREN");
            if (children != null && !children.isEmpty()) {
                printTree(children, indent + "  ");
            }
        }
    }

    @Test
    public void testTreeSearch() {
        // 构建测试数据
        List<Map<String, Object>> dataList = new ArrayList<>();

        // 设备
        Map<String, Object> item1 = new HashMap<>();
        item1.put("STATUS", 1);
        item1.put("LABEL", "设备");
        item1.put("VALUE", 1);
        item1.put("PARENT_ID", 0);
        dataList.add(item1);

        // 耗材
        Map<String, Object> item2 = new HashMap<>();
        item2.put("STATUS", 1);
        item2.put("LABEL", "耗材");
        item2.put("VALUE", 2);
        item2.put("PARENT_ID", 0);
        dataList.add(item2);

        // 设备1
        Map<String, Object> item3 = new HashMap<>();
        item3.put("STATUS", 1);
        item3.put("LABEL", "设备1");
        item3.put("VALUE", 3);
        item3.put("PARENT_ID", 1);
        dataList.add(item3);

        // 耗材1
        Map<String, Object> item4 = new HashMap<>();
        item4.put("STATUS", 1);
        item4.put("LABEL", "耗材1");
        item4.put("VALUE", 4);
        item4.put("PARENT_ID", 2);
        dataList.add(item4);

        // 设备11
        Map<String, Object> item5 = new HashMap<>();
        item5.put("STATUS", 1);
        item5.put("LABEL", "设备11");
        item5.put("VALUE", 5);
        item5.put("PARENT_ID", 3);
        dataList.add(item5);

        // 耗材11
        Map<String, Object> item6 = new HashMap<>();
        item6.put("STATUS", 1);
        item6.put("LABEL", "耗材11");
        item6.put("VALUE", 6);
        item6.put("PARENT_ID", 4);
        dataList.add(item6);

        // 耗材12
        Map<String, Object> item7 = new HashMap<>();
        item7.put("STATUS", 1);
        item7.put("LABEL", "耗材12");
        item7.put("VALUE", 7);
        item7.put("PARENT_ID", 4);
        dataList.add(item7);

        // 配置树结构
        TreeConfig config = new TreeConfig();
        config.setTreeId("VALUE");
        config.setTreePid("PARENT_ID");
        config.setTreeLabel("LABEL");
        config.setTreeChildren("CHILDREN");
        config.setTreeIsParent("IS_PARENT");
        config.setRootPid("0");

        // 测试1: 无搜索关键词，应该返回完整树
        System.out.println("=== 测试1: 无搜索关键词 ===");
        List<Map<String, Object>> result1 = Utils.flatLabelValueTree(dataList, "", "", null, config);
        System.out.println("结果数量: " + result1.size() + " (期望: 2)");
        printTree(result1, "");

        // 测试2: 搜索"11"，应该返回2个一级分类（设备和耗材）
        System.out.println("\n=== 测试2: 搜索 '11' ===");
        List<Map<String, Object>> result2 = Utils.flatLabelValueTree(dataList, "", "", "11", config);
        System.out.println("结果数量: " + result2.size() + " (期望: 2)");
        printTree(result2, "");

        // 验证结果
        assert result2.size() == 2 : "搜索'11'应该返回2个一级分类，实际返回: " + result2.size();

        // 验证第一个是"设备"
        Map<String, Object> category1 = result2.get(0);
        System.out.println("\n第一个分类: " + category1.get("LABEL"));
        assert "设备".equals(category1.get("LABEL")) : "第一个分类应该是'设备'";

        List<Map<String, Object>> children1 = (List<Map<String, Object>>) category1.get("CHILDREN");
        assert children1 != null && children1.size() == 1 : "设备应该有1个子节点";
        assert "设备1".equals(children1.get(0).get("LABEL")) : "子节点应该是'设备1'";

        List<Map<String, Object>> grandChildren1 = (List<Map<String, Object>>) children1.get(0).get("CHILDREN");
        assert grandChildren1 != null && grandChildren1.size() == 1 : "设备1应该有1个子节点";
        assert "设备11".equals(grandChildren1.get(0).get("LABEL")) : "孙节点应该是'设备11'";

        // 验证第二个是"耗材"
        Map<String, Object> category2 = result2.get(1);
        System.out.println("第二个分类: " + category2.get("LABEL"));
        assert "耗材".equals(category2.get("LABEL")) : "第二个分类应该是'耗材'";

        List<Map<String, Object>> children2 = (List<Map<String, Object>>) category2.get("CHILDREN");
        assert children2 != null && children2.size() == 1 : "耗材应该有1个子节点";
        assert "耗材1".equals(children2.get(0).get("LABEL")) : "子节点应该是'耗材1'";

        List<Map<String, Object>> grandChildren2 = (List<Map<String, Object>>) children2.get(0).get("CHILDREN");
        assert grandChildren2 != null && grandChildren2.size() == 1 : "耗材1应该有1个子节点（耗材12应该被过滤掉）";
        assert "耗材11".equals(grandChildren2.get(0).get("LABEL")) : "孙节点应该是'耗材11'";

        // 测试3: 搜索"设备"，应该只返回设备分类及其所有子节点
        System.out.println("\n=== 测试3: 搜索 '设备' ===");
        List<Map<String, Object>> result3 = Utils.flatLabelValueTree(dataList, "", "", "设备", config);
        System.out.println("结果数量: " + result3.size() + " (期望: 1)");
        printTree(result3, "");

        assert result3.size() == 1 : "搜索'设备'应该返回1个一级分类，实际返回: " + result3.size();
        assert "设备".equals(result3.get(0).get("LABEL")) : "应该是'设备'分类";

        List<Map<String, Object>> deviceChildren = (List<Map<String, Object>>) result3.get(0).get("CHILDREN");
        assert deviceChildren != null && deviceChildren.size() == 1 : "设备应该有1个子节点";
        assert "设备1".equals(deviceChildren.get(0).get("LABEL")) : "子节点应该是'设备1'";

        List<Map<String, Object>> deviceGrandChildren = (List<Map<String, Object>>) deviceChildren.get(0).get("CHILDREN");
        assert deviceGrandChildren != null && deviceGrandChildren.size() == 1 : "设备1应该有1个子节点";
        assert "设备11".equals(deviceGrandChildren.get(0).get("LABEL")) : "孙节点应该是'设备11'";

        // 测试4: 搜索"耗材"，应该只返回耗材分类及其所有子节点
        System.out.println("\n=== 测试4: 搜索 '耗材' ===");
        List<Map<String, Object>> result4 = Utils.flatLabelValueTree(dataList, "", "", "耗材", config);
        System.out.println("结果数量: " + result4.size() + " (期望: 1)");
        printTree(result4, "");

        assert result4.size() == 1 : "搜索'耗材'应该返回1个一级分类，实际返回: " + result4.size();
        assert "耗材".equals(result4.get(0).get("LABEL")) : "应该是'耗材'分类";

        List<Map<String, Object>> materialChildren = (List<Map<String, Object>>) result4.get(0).get("CHILDREN");
        assert materialChildren != null && materialChildren.size() == 1 : "耗材应该有1个子节点";
        assert "耗材1".equals(materialChildren.get(0).get("LABEL")) : "子节点应该是'耗材1'";

        List<Map<String, Object>> materialGrandChildren = (List<Map<String, Object>>) materialChildren.get(0).get("CHILDREN");
        assert materialGrandChildren != null && materialGrandChildren.size() == 2 : "耗材1应该有2个子节点（耗材11和耗材12都保留）";

        // 测试5: 搜索"1"，应该返回所有包含"1"的节点
        System.out.println("\n=== 测试5: 搜索 '1' ===");
        List<Map<String, Object>> result5 = Utils.flatLabelValueTree(dataList, "", "", "1", config);
        System.out.println("结果数量: " + result5.size() + " (期望: 2)");
        printTree(result5, "");

        assert result5.size() == 2 : "搜索'1'应该返回2个一级分类，实际返回: " + result5.size();

        // 测试6: 搜索不存在的关键词，应该返回空列表
        System.out.println("\n=== 测试6: 搜索 '不存在' ===");
        List<Map<String, Object>> result6 = Utils.flatLabelValueTree(dataList, "", "", "不存在", config);
        System.out.println("结果数量: " + result6.size() + " (期望: 0)");
        printTree(result6, "");

        assert result6.size() == 0 : "搜索不存在的关键词应该返回0个结果，实际返回: " + result6.size();

        // 测试7: 搜索"设备1"，应该返回设备作为父节点，设备1及其子节点
        System.out.println("\n=== 测试7: 搜索 '设备1' ===");
        List<Map<String, Object>> result7 = Utils.flatLabelValueTree(dataList, "", "", "设备1", config);
        System.out.println("结果数量: " + result7.size() + " (期望: 1)");
        printTree(result7, "");

        assert result7.size() == 1 : "搜索'设备1'应该返回1个一级分类，实际返回: " + result7.size();
        assert "设备".equals(result7.get(0).get("LABEL")) : "一级节点应该是'设备'（作为路径保留）";

        List<Map<String, Object>> device1Children = (List<Map<String, Object>>) result7.get(0).get("CHILDREN");
        assert device1Children != null && device1Children.size() == 1 : "设备应该有1个子节点";
        assert "设备1".equals(device1Children.get(0).get("LABEL")) : "子节点应该是'设备1'（匹配节点）";

        List<Map<String, Object>> device1GrandChildren = (List<Map<String, Object>>) device1Children.get(0).get("CHILDREN");
        assert device1GrandChildren != null && device1GrandChildren.size() == 1 : "设备1应该有1个子节点（所有子节点都保留）";
        assert "设备11".equals(device1GrandChildren.get(0).get("LABEL")) : "孙节点应该是'设备11'";

        // 测试8: 搜索"12"，应该只返回耗材分类（因为耗材12匹配）
        System.out.println("\n=== 测试8: 搜索 '12' ===");
        List<Map<String, Object>> result8 = Utils.flatLabelValueTree(dataList, "", "", "12", config);
        System.out.println("结果数量: " + result8.size() + " (期望: 1)");
        printTree(result8, "");

        assert result8.size() == 1 : "搜索'12'应该返回1个一级分类，实际返回: " + result8.size();
        assert "耗材".equals(result8.get(0).get("LABEL")) : "应该是'耗材'分类";

        List<Map<String, Object>> material12Children = (List<Map<String, Object>>) result8.get(0).get("CHILDREN");
        assert material12Children != null && material12Children.size() == 1 : "耗材应该有1个子节点";

        List<Map<String, Object>> material12GrandChildren = (List<Map<String, Object>>) material12Children.get(0).get("CHILDREN");
        assert material12GrandChildren != null && material12GrandChildren.size() == 1 : "耗材1应该有1个子节点（只保留耗材12）";
        assert "耗材12".equals(material12GrandChildren.get(0).get("LABEL")) : "孙节点应该是'耗材12'";

        System.out.println("\n✓ 所有测试通过！");
    }
}
