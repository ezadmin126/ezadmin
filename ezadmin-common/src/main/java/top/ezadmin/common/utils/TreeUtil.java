package top.ezadmin.common.utils;

import top.ezadmin.dao.model.TreeConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 树形结构工具类
 * <p>
 * 将平铺的 List&lt;Map&gt; 按原始列表顺序转换为树形结构。
 * 使用 LinkedHashMap 保证子节点顺序与原始数据一致，
 * 修复了原 Utils.flatLabelValueTree 因 HashMap 无序导致顺序丢失的问题。
 * </p>
 */
public class TreeUtil {

    private TreeUtil() {
    }

    /**
     * 将平铺列表构建为树形结构，保留原始列表顺序。
     *
     * @param list   平铺节点列表
     * @param config 树字段配置
     * @return 树形结构根节点列表
     */
    public static List<Map<String, Object>> build(List<Map<String, Object>> list, TreeConfig config) {
        return doBuild(list, config, null);
    }

    /**
     * 将平铺列表构建为树形结构，保留原始列表顺序，并按关键词过滤。
     *
     * @param list          平铺节点列表
     * @param config        树字段配置
     * @param searchKeyword 搜索关键词（匹配 label 字段，忽略大小写），空则不过滤
     * @return 树形结构根节点列表
     */
    public static List<Map<String, Object>> build(List<Map<String, Object>> list, TreeConfig config, String searchKeyword) {
        return doBuild(list, config, searchKeyword);
    }

    /**
     * 对已构建好的树形结构进行关键词过滤。
     * <p>匹配到的节点及其所有子节点均保留；未匹配节点若子树中有匹配则保留祖先路径。</p>
     *
     * @param tree          已构建的树形节点列表
     * @param searchKeyword 搜索关键词
     * @param config        树字段配置
     * @return 过滤后的树形节点列表
     */
    public static List<Map<String, Object>> filter(List<Map<String, Object>> tree,
                                                   String searchKeyword,
                                                   TreeConfig config) {
        if (StringUtils.isBlank(searchKeyword) || tree == null || tree.isEmpty()) {
            return tree;
        }
        return doFilter(tree, searchKeyword.toLowerCase(), config);
    }

    // -------------------------------------------------------------------------
    // 核心实现
    // -------------------------------------------------------------------------

    private static List<Map<String, Object>> doBuild(List<Map<String, Object>> list,
                                                     TreeConfig config,
                                                     String searchKeyword) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        String rootId = StringUtils.isNotBlank(config.getRootPid()) ? config.getRootPid() : "0";
        List<Map<String, Object>> rootChildren = new ArrayList<>();

        // LinkedHashMap 按原始列表插入顺序维护索引，保证后续遍历顺序与原始数据一致
        Map<String, Map<String, Object>> idMap = new LinkedHashMap<>(list.size() * 2);
        for (Map<String, Object> node : list) {
            // 清除旧的 children / isParent，防止重复调用时数据累积
            node.remove(config.getTreeChildren());
            node.remove(config.getTreeIsParent());
            idMap.put(Utils.trimNull(node.get(config.getTreeId())), node);
        }

        // 按原始顺序遍历，将节点挂载到父节点
        for (Map<String, Object> node : idMap.values()) {
            String parentId = Utils.trimNull(node.get(config.getTreePid()));
            if (rootId.equals(parentId)) {
                rootChildren.add(node);
            } else {
                Map<String, Object> parent = idMap.get(parentId);
                if (parent != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> siblings =
                            (List<Map<String, Object>>) parent.computeIfAbsent(
                                    config.getTreeChildren(), k -> new ArrayList<>());
                    siblings.add(node);
                    parent.put(config.getTreeIsParent(), true);
                }
                // parentId 不在 idMap 中的孤儿节点直接丢弃
            }
        }

        if (StringUtils.isNotBlank(searchKeyword)) {
            return doFilter(rootChildren, searchKeyword.toLowerCase(), config);
        }

        return rootChildren;
    }

    // -------------------------------------------------------------------------
    // 关键词过滤
    // -------------------------------------------------------------------------

    /**
     * 递归过滤：保留匹配节点（含其全部子树）及匹配子孙所在的祖先路径。
     */
    private static List<Map<String, Object>> doFilter(List<Map<String, Object>> nodes,
                                                      String lowerKeyword,
                                                      TreeConfig config) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> node : nodes) {
            String label = Utils.trimNull(node.get(config.getTreeLabel())).toLowerCase();
            if (label.contains(lowerKeyword)) {
                // 当前节点匹配：保留节点及完整子树
                result.add(node);
            } else {
                // 当前节点不匹配：递归检查子节点
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children =
                        (List<Map<String, Object>>) node.get(config.getTreeChildren());
                if (children != null && !children.isEmpty()) {
                    List<Map<String, Object>> filteredChildren = doFilter(children, lowerKeyword, config);
                    if (!filteredChildren.isEmpty()) {
                        // 子节点有匹配：浅拷贝当前节点并替换 children，保留祖先路径
                        Map<String, Object> copy = new LinkedHashMap<>(node);
                        copy.put(config.getTreeChildren(), filteredChildren);
                        result.add(copy);
                    }
                }
            }
        }
        return result;
    }
}
