# EzAdmin 5.0.0 升级说明

5.0.0 为不兼容升级，仅支持 Java 17+ 和 JSON DSL。项目构建只保留 `ezadmin-common` 与 `ezadmin-java17-spring-starter`，starter 使用 Spring Boot 3 / Jakarta Servlet。

## 配置架构

列表和表单均由 `DslLoader` 读取，文件优先，文件不存在时读取数据库：

| 类型 | 资源路径 | 数据库配置 |
| --- | --- | --- |
| 列表 | `topezadmin/config/layui/dsl/list/{id}.json` | `T_EZADMIN_LIST.LIST_DSL` 及表达式字段 |
| 表单 | `topezadmin/config/layui/dsl/form/{id}.json` | `T_EZADMIN_FORM.FORM_DSL` 及表达式字段 |

配置由源码文件或部署流程维护。`cacheFlag: false` 继续支持本地 DSL、表达式文件和渲染模板热加载。生产缓存可通过 `EzBootstrap.clear()` 清理。

DSL 组件和页面使用的 HTML 是渲染模板，继续保留；它们不再承担旧版 HTML 业务配置解析。SQL 转配置默认实现为 `DslEzSqlParserImpl`，只输出 JSON DSL。

## 已移除

- Java 8 starter 及旧 HTML SQL 转配置实现。
- HTML 列表/表单 DAO、旧插件配置加载、旧发布/编辑/历史配置读取。
- 旧版 HTML 配置控制器及页面、旧文件监听器和配置扫描属性。
- HTML 在线编辑、DSL 可视化编辑、DSL 源码编辑、AI 配置修改与对应静态资源。
- 在线配置文件/数据库写入辅助 API 和仅用于配置编辑的 JSON Patch、Jsoup 依赖。

旧配置和编辑入口不再分发，包括 `listEdit/*`、`formEdit/*`、`edit/*`、`dsl/*`、`list/edit-*`、`form/edit-*`，以及旧 HTML 的列表、树、表单提交等路由。需要切换到 DSL 路由，不提供兼容跳转。

## 保留的业务接口

| 路由 | 用途 |
| --- | --- |
| `list/page-{id}` | DSL 列表页面 |
| `list/data-{id}` | DSL 列表数据 |
| `list/countpage-{id}` | DSL 列表计数 |
| `list/exportpage-{id}` | DSL 数据导出 |
| `list/pageCustomSearch-{id}` | DSL 自定义查询界面 |
| `form/page-{id}` | DSL 表单页面 |
| `form/data-{id}` | DSL 表单初始化数据 |
| `form/submit-{id}`、`form/add-{id}` | 业务数据提交/新增 |
| `form/delete-{id}`、`form/status-{id}` | 业务数据删除/状态更新 |

以上路径使用配置的 URL 前缀，默认为 `/topezadmin/`。业务数据编辑、富文本输入、上传及上传文件排序继续保留。

## 升级步骤

1. 使用 Java 17+，依赖 `ezadmin-java17-spring-starter:5.0.0`。
2. 将旧 HTML 业务配置重写为 JSON DSL；旧 `T_EZADMIN_PUBLISH`、`T_EZADMIN_EDIT`、`T_EZADMIN_HISTORY` 不再读取。
3. 更新菜单、页面跳转和数据请求到上述 DSL 路由。
4. 删除旧 HTML 资源扫描属性、`editLocation`、`refreshClass` 及在线 AI 编辑相关的 `apiUrl`、`apiKey`、`model`、`temperature` 配置。
5. 如使用数据库来源，部署 DSL 数据表与配置数据，并在更新后清理缓存。

本次代码修改没有执行业务数据库迁移、删除数据库表或发布制品。
