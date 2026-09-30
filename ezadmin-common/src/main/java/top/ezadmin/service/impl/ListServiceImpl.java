package top.ezadmin.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzBootstrap;
import top.ezadmin.common.enums.*;
import top.ezadmin.common.utils.*;
import top.ezadmin.dao.Dao;
import top.ezadmin.plugins.parser.CommentsSqlParser;
import top.ezadmin.plugins.parser.MapParser;
import top.ezadmin.plugins.parser.parse.ResultModel;
import top.ezadmin.service.ListService;
import top.ezadmin.web.RequestContext;

import javax.sql.DataSource;
import java.util.*;

public class ListServiceImpl implements ListService {
    private static final Logger LOG = LoggerFactory.getLogger(ListServiceImpl.class);

    @Override
    public void initComponentData(RequestContext context, Map<String, Object> component) {
        if (Utils.isEmpty(component)) {
            return;
        }
        //data
        String dataUrl = "";
        Map<String, Object> initData = (Map<String, Object>) component.get("initData");
        if (Utils.isNotEmpty(initData)) {

            if (initData.containsKey("dataJson") && initData.get("dataJson") != null) {
                List<Map<String, Object>> result = (List<Map<String, Object>>) initData.get("dataJson");
                component.put("data", result);
                component.put("dataJson", JSONUtils.toJSONString(result));
            } else if (initData.containsKey("dataSql") && initData.get("dataSql") != null) {
                String dataSql = (String) initData.get("dataSql");
                Map<String, Object> params = new HashMap<>();
                params.putAll(context.getRequestParams());
                params.putAll(context.getSessionParams());
                ResultModel model = CommentsSqlParser.parse(dataSql, params);

                String dataSource = Utils.trimEmptyDefault(initData.get("dataSource"), "datasource");
                //
                DataSource dataSourceBean = EzBootstrap.getInstance().getDataSourceByKey(dataSource);
                if (dataSourceBean == null) {
                    dataSourceBean = EzBootstrap.getInstance().getEzDataSource();
                }
                try {
                    List<Map<String, Object>> result = Dao.getInstance().executeQuery(dataSourceBean,
                            model.getResult(), model.getParamsStatic(), false);
                    component.put("data", result);
                    component.put("dataJson", JSONUtils.toJSONString(result));
                } catch (Exception e) {
                    LOG.error("执行SQL错误", e);
                }
            } else if (initData.containsKey("dataTreeSql") && initData.get("dataTreeSql") != null) {
                //递归获取数据，根据parentId
                String dataTreeSql = (String) initData.get("dataTreeSql");
                String dataTreeSqlRoot = Utils.trimEmptyDefault(initData.get("dataTreeSqlRoot"), dataTreeSql);

                Map<String, Object> params = new HashMap<>();
                params.putAll(context.getRequestParams());
                params.putAll(context.getSessionParams());

                String dataSource = Utils.trimEmptyDefault(initData.get("dataSource"), "datasource");
                DataSource dataSourceBean = EzBootstrap.getInstance().getDataSourceByKey(dataSource);
                if (dataSourceBean == null) {
                    dataSourceBean = EzBootstrap.getInstance().getEzDataSource();
                }

                try {
                    // 查询第一层级数据
                    // 优先使用 dataTreeSqlRoot，如果没有配置则使用 dataTreeSql
                    String rootSql = StringUtils.isNotBlank(dataTreeSqlRoot) ? dataTreeSqlRoot : dataTreeSql;
                    ResultModel model = CommentsSqlParser.parse(rootSql, params);
                    List<Map<String, Object>> rootNodes = Dao.getInstance().executeQuery(dataSourceBean,
                            model.getResult(), model.getParamsStatic(), false);

                    // 创建结果列表，先添加第一层数据
                    List<Map<String, Object>> allData = new ArrayList<>();
                    if (Utils.isNotEmpty(rootNodes)) {
                        allData.addAll(rootNodes);
                    }

                    // 获取最大递归深度，默认5层
                    int maxDepth = NumberUtils.toInt(Utils.trimNull(initData.get("maxDepth")), 5);

                    // 递归查询子节点（从第2层开始，深度为1）
                    // 使用 dataTreeSql 作为子层级查询的SQL模板
                    // 将所有子层级数据收集到 allData 中
                    buildTreeRecursively(dataSourceBean, dataTreeSql, rootNodes, params, 1, maxDepth, allData);


                    // 返回扁平的列表，包含所有层级的数据
                    component.put("data", allData);
                    component.put("dataJson", JSONUtils.toJSONString(allData));
                } catch (Exception e) {
                    LOG.error("执行SQL错误", e);
                }
            } else if (initData.containsKey("dataUrl")) {
                //暂时先塞道属性里面去
                dataUrl = (String) initData.get("dataUrl");
            }
        }
        //props
        try {
            Map<String, Object> props = (Map<String, Object>) component.get("props");
            if (props == null) {
                props = new HashMap<>();
            }
            if (props != null && component.get("component") != null && component.get("component").equals("input")) {
                props.putIfAbsent("lay-affix", "clear");
            }
            //props里面每个值都做一个 mapparse转换
            props.entrySet().forEach(entry -> {
                if (entry.getValue() instanceof String) {
                    String url=MapParser.parseDefaultEmpty(Utils.trimNull(entry.getValue()), context.getRequestParams()).getResult();
                    if(entry.getKey().equalsIgnoreCase("url")){
                        url= Utils.fixedUrl(url);
                    }
                    entry.setValue(url);
                }
            });
            if (StringUtils.isNotBlank(dataUrl)) {
                props.putIfAbsent("dataUrl", dataUrl);
            }
            component.put("props", props);
            component.put("propsJson", JSONUtils.toJSONString(props));
        } catch (Exception e) {

        }


    }

    private void buildTreeRecursively(DataSource dataSource, String sqlTemplate,
                                      List<Map<String, Object>> parentNodes,
                                      Map<String, Object> baseParams,
                                      int currentDepth, int maxDepth,
                                      List<Map<String, Object>> resultList) {
        // 如果达到最大深度或父节点为空，停止递归
        if (currentDepth >= maxDepth || Utils.isEmpty(parentNodes)) {
            return;
        }

        // 收集当前层级所有父节点的 ID，批量查询一次（每层只执行一条 SQL）
        List<String> parentIdList = new ArrayList<>();
        for (Map<String, Object> parentNode : parentNodes) {
            String parentId = Utils.trimNull(parentNode.get("value"));
            if (StringUtils.isNotBlank(parentId)) {
                parentIdList.add(parentId);
            }
        }
        if (parentIdList.isEmpty()) {
            return;
        }

        try {
            // 将 "= #{parentId}" / "= ${parentId}" 替换为 "IN (${__parentIds__})"
            // 使 SQL 支持批量 IN 查询，避免逐条查询带来的 N×D 次数据库访问
            String batchSql = sqlTemplate
                    .replaceAll("(?i)=\\s*#\\{parentId[^}]*\\}", "IN (\\${__parentIds__})")
                    .replaceAll("(?i)=\\s*\\$\\{parentId[^}]*\\}", "IN (\\${__parentIds__})");

            // 构建单引号转义的 IN 列表，例如：'id1','id2','id3'
            StringBuilder inList = new StringBuilder();
            for (int i = 0; i < parentIdList.size(); i++) {
                if (i > 0) inList.append(',');
                inList.append('\'').append(parentIdList.get(i).replace("'", "''")).append('\'');
            }

            Map<String, Object> queryParams = new HashMap<>();
            queryParams.putAll(baseParams);
            queryParams.put("__parentIds__", inList.toString());

            ResultModel model = CommentsSqlParser.parse(batchSql, queryParams);
            List<Map<String, Object>> allChildren = Dao.getInstance().executeQuery(
                    dataSource,
                    model.getResult(),
                    model.getParamsStatic(),
                    false
            );

            if (Utils.isNotEmpty(allChildren)) {
                resultList.addAll(allChildren);
                buildTreeRecursively(dataSource, sqlTemplate, allChildren, baseParams, currentDepth + 1, maxDepth, resultList);
            }
        } catch (Exception e) {
            LOG.error("批量递归查询树形数据错误，depth=" + currentDepth + ", parentIds=" + parentIdList, e);
        }
    }
}
