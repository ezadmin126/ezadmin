# ezadmin-common 列表单元测试使用说明

## 概述

`ezadmin-common` 模块已去除 Servlet 依赖，使用 `RequestContext` 替代 `HttpServletRequest`，支持在纯 Java 环境下进行单元测试。

## 快速开始

### 1. 添加测试依赖

```xml
<!-- H2 内存数据库 -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.200</version>
    <scope>test</scope>
</dependency>
<!-- SLF4J Simple -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>1.7.36</version>
    <scope>test</scope>
</dependency>
```

### 2. 初始化 EzBootstrap

```java
@BeforeClass
public static void setup() throws Exception {
    // 创建数据源
    JdbcDataSource h2DataSource = new JdbcDataSource();
    h2DataSource.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL");
    h2DataSource.setUser("sa");
    h2DataSource.setPassword("");

    // 配置 EzBootstrap
    EzBootstrapConfig config = new EzBootstrapConfig();
    Map<String, DataSource> datasourceMap = new HashMap<>();
    datasourceMap.put("defaultdatasource", h2DataSource);
    datasourceMap.put("datasource", h2DataSource);
    config.setDatasourceMap(datasourceMap);

    // 设置空的插件配置列表
    config.setPluginsFormConfigResources(new ArrayList<>());
    config.setPluginsListConfigResources(new ArrayList<>());
    config.setListConfigResources(new ArrayList<>());
    config.setFormConfigResources(new ArrayList<>());

    EzBootstrap.getInstance().init(config);
}
```

### 3. 创建 RequestContext

```java
private RequestContext createRequestContext() {
    RequestContext ctx = new RequestContext();
    ctx.setContextPath("");
    ctx.setServerName("localhost");
    ctx.setServerPort(8080);
    ctx.setMethod("GET");
    ctx.setRequestParams(new HashMap<>());
    ctx.setSessionParams(new HashMap<>());
    return ctx;
}
```

### 4. 调用测试

```java
// 方式一：通过 EzBootstrap 统一入口
RequestContext ctx = createRequestContext();
ctx.setRequestURI("/topezadmin/list/data-list/test");
ctx.getRequestParams().put("page", 1);
ctx.getRequestParams().put("perPageInt", 10);
EzResult result = EzBootstrap.getInstance().generate(ctx);

// 方式二：直接实例化 Controller
ListController controller = new ListController();
EzResult result = controller.data(ctx, "data", "list/test");
```

---

## JSON DSL 配置格式

DSL 配置文件位置：`topezadmin/config/layui/dsl/list/{id}.json`

### 完整示例

```json
{
  "id": "test",
  "title": "学生列表",
  "dataSource": "dataSource",
  "body": {
    "adminStyle": "layui",
    "emptyShow": "@",
    "showIndex": true,
    "selectable": true,
    "rowActionWidth": 175
  },
  "search": [...],
  "tableButton": [...],
  "rowButton": [...],
  "column": [...],
  "express": {
    "main": "...",
    "orderBy": "...",
    "groupBy": "...",
    "count": ""
  }
}
```

### 配置项说明

#### 基础配置

| 字段         | 类型     | 说明     |
|------------|--------|--------|
| id         | string | 列表唯一标识 |
| title      | string | 列表标题   |
| dataSource | string | 数据源名称  |

#### body 配置

| 字段             | 类型      | 说明              |
|----------------|---------|-----------------|
| adminStyle     | string  | 管理风格，默认 `layui` |
| emptyShow      | string  | 空值显示字符          |
| showIndex      | boolean | 是否显示序号列         |
| selectable     | boolean | 是否可选择           |
| rowActionWidth | number  | 行操作列宽度          |

---

### search 搜索配置

```json
{
  "item_name": "USER_NAME",
  "label": "用户名",
  "component": "input",
  "operator": "like",
  "alias": "a.username",
  "props": {
    "placeholder": "请输入用户名"
  }
}
```

| 字段        | 说明       |
|-----------|----------|
| item_name | 参数名      |
| label     | 显示标签     |
| component | 组件类型     |
| operator  | 操作符      |
| alias     | SQL 字段别名 |
| jdbcType  | JDBC 类型  |
| props     | 组件属性     |
| initData  | 初始化数据    |

#### 搜索组件类型 (component)

| 类型                | 说明    |
|-------------------|-------|
| `input`           | 文本输入框 |
| `hidden`          | 隐藏字段  |
| `select`          | 单选下拉  |
| `select-multiple` | 多选下拉  |
| `date`            | 日期选择  |
| `cascader`        | 级联选择  |

#### 操作符 (operator)

| 操作符       | SQL               | 说明   |
|-----------|-------------------|------|
| `eq`      | `= ?`             | 等于   |
| `like`    | `LIKE ?`          | 模糊匹配 |
| `in`      | `IN (?)`          | 包含   |
| `between` | `BETWEEN ? AND ?` | 范围   |

#### initData 数据源

```json
// 静态数据
"initData": {
  "dataJson": [
    {"label": "男", "value": "1"},
    {"label": "女", "value": "0"}
  ]
}

// SQL 查询
"initData": {
  "dataSource": "dataSource",
  "dataSql": "SELECT id value, name label FROM options"
}
```

---

### column 列配置

```json
{
  "item_name": "USER_NAME",
  "label": "用户名",
  "component": "tdText",
  "props": {
    "edit": "textarea",
    "url": "/form/form/edit?ID={{= d.ID }}"
  }
}
```

#### 列组件类型 (component)

| 类型                 | 说明   |
|--------------------|------|
| `tdText`           | 文本显示 |
| `tdLink`           | 链接   |
| `tdSelect`         | 下拉显示 |
| `tdSelectMultiple` | 多选显示 |
| `tdCascader`       | 级联显示 |

---

### tableButton 表格按钮

```json
{
  "label": "新增",
  "url": "/form/form/add",
  "item_name": "add",
  "component": "button-toolbar"
}
```

---

### rowButton 行按钮

```json
{
  "item_name": "edit",
  "label": "编辑{{=d.USER_NAME}}",
  "component": "button-normal",
  "props": {
    "opentype": "MODAL",
    "windowname": "编辑",
    "area": "50,100",
    "url": "/form/form/edit?ID={{= d.ID }}",
    "display": "{{ if(d.ID!=1){ }} block {{ } else { }} none {{ } }}"
  }
}
```

#### 行按钮组件类型

| 类型                | 说明     |
|-------------------|--------|
| `button-normal`   | 普通按钮   |
| `button-bread`    | 面包屑按钮  |
| `button-dropdown` | 下拉菜单按钮 |

#### opentype 打开方式

| 值        | 说明    |
|----------|-------|
| `MODAL`  | 弹窗打开  |
| `_BLANK` | 新窗口打开 |
| `_SELF`  | 当前窗口  |

---

### express SQL表达式

```json
"express": {
  "main": "StringBuilder sql=new StringBuilder(); sql.append(\"SELECT a.id ID, a.name NAME FROM users a WHERE a.status=1\"); return search(sql);",
  "orderBy": " order by a.id desc ",
  "groupBy": " group by a.id ",
  "count": ""
}
```

| 字段      | 说明             |
|---------|----------------|
| main    | 主查询表达式         |
| orderBy | 排序语句           |
| groupBy | 分组语句           |
| count   | 计数表达式 (空则自动生成) |

---

## 测试用例说明

| 测试方法                        | 说明                       |
|-----------------------------|--------------------------|
| `testDslListPage`           | DSL 列表页面渲染               |
| `testDslListData`           | DSL 列表数据接口               |
| `testDslListDataWithSearch` | 带搜索条件的列表数据               |
| `testDirectControllerDsl`   | 直接调用 ListController.dsl  |
| `testDirectControllerData`  | 直接调用 ListController.data |
| `testPagination`            | 分页功能                     |
| `testOrderBy`               | 排序功能                     |
| `testSelectInitData`        | 下拉选择数据加载                 |
| `testSessionParams`         | 会话参数传递                   |
| `testDateRangeSearch`       | 日期范围搜索                   |
| `testMultiSelectSearch`     | 多选搜索 (IN 查询)             |
| `testNotExistList`          | 不存在的列表ID处理               |

---

## URI 路由说明

| URI 格式                       | 说明         |
|------------------------------|------------|
| `/topezadmin/list/dsl-{id}`  | DSL 列表页面   |
| `/topezadmin/list/data-{id}` | DSL 列表数据接口 |

---

## 运行测试

```bash
# 运行所有列表测试
mvn test -Dtest=ListControllerTest

# 运行单个测试方法
mvn test -Dtest=ListControllerTest#testDslListData
```
