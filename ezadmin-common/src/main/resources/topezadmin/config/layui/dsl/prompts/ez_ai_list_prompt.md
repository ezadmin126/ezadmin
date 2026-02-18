# EzAdmin 低代码配置使用说明

ezadmin低代码是一个使用layui与qlexpress，将dsl配置转换成页面的工具，能够快速生成crud页面。

### 列表配置结构
```json
{
  "id": "列表ID",
  "name": "列表名称",
  "dataSource": "数据源Bean名称",
  "initApi": "自定义接口(可选)",
  "hideSearch": false,//是否隐藏搜索项与表头，主要用于表单内列表展示
  "body": {
    "emptyShow": "-",          // 空值显示
    "showIndex": true,          // 显示序号
    "selectable": true,         // 显示选择框
    "rowActionWidth": 175       // 操作列宽度
  },

  "tabList": [],               // 标签页
  "search": [[],[]],           // 搜索项：二维数组，第一层代表行，第二层代表该行的搜索字段
  "column": [],                // 列配置
  "tableButton": [],           // 表头按钮
  "rowButton": [],             // 行操作按钮

  "express": {
    "main": "查询SQL表达式",
    "orderBy": "order by xxx",
    "groupBy": "group by xxx",
    "count": "自定义count语句"
  },
  "appendHead": "",          // 列表头部追加内容
  "appendFoot": ""           // 列表尾部追加内容
}
```

#### search 二维数组格式说明

**格式规则：**
- search 是二维数组：`[[row1_searches], [row2_searches]]`
- 第一层数组的每个元素代表搜索表单中的一行
- 第二层数组包含该行显示的所有搜索字段
- 每行默认显示4个字段（不包含隐藏字段）
- 系统会自动为每个搜索字段设置默认 classAppend：`layui-col-sm6 layui-col-md4 layui-col-lg4 layui-col-xl3`

**classAppend 宽度说明：**
- 使用 Layui 栅格系统：`layui-col-{屏幕尺寸}{宽度}`
- 默认配置：`layui-col-sm6 layui-col-md4 layui-col-lg4 layui-col-xl3`
  - sm(小屏)：占50%宽度，每行2个
  - md(中屏)：占33.3%宽度，每行3个
  - lg(大屏)：占33.3%宽度，每行3个
  - xl(超大屏)：占25%宽度，每行4个

**示例：**
```json
"search": [
  [
    {"name": "NAME", "label": "姓名", "component": "input", "operator": "like"},
    {"name": "STATUS", "label": "状态", "component": "select"},
    {"name": "AGE", "label": "年龄", "component": "input"},
    {"name": "EMAIL", "label": "邮箱", "component": "input"}
  ],
  [
    {"name": "CREATE_TIME", "label": "创建时间", "component": "date", "operator": "between"},
    {"name": "DEPT_ID", "label": "部门", "component": "select"}
  ]
]
```
上面的配置会生成：
- 第一行：姓名、状态、年龄、邮箱（4个字段）
- 第二行：创建时间、部门（2个字段）
- 每个字段自动应用响应式宽度

## 三、组件配置

### 基本结构
```json
{
  "item_name": "字段名，必须大写",
  "label": "显示名称",
  "component": "组件类型",
  "alias": "SQL别名(可选)",
  "jdbcType": "数据类型(可选)",
  "operator": "操作符(可选,EQ/LIKE/IN等)",

  "initData": {
    "dataJson": [{"label":"选项1","value":"1"}],  //配置json
    "dataSql": "SELECT value,label FROM table",  //配置sql
    "dataSource": "数据源名称"   //配置sql对应的数据源
  },

  "props": {// Layui table 表头属性 或自定义属性
    "placeholder": "0-9999",
    "type": "number",
    "step": "1",
    "min": "0",
    "lay-affix": "number",
    "maxlength": "4",
    "width": 100,//列的宽度，仅限数字
    "description": "- 前台展示的浏览次数=预置浏览次数+用户实际浏览次数。",
    "validate": { //jquery validate 使用 validate
      "rule": {
        "range": [
          0,
          9999
        ]
      },
      "message": {
        "range": "请输入0-9999之间的数字"
      }
    }
  }
}
```
### column  props属性配置
| 属性名	| 描述	| 类型	| 默认值
| field	| 设置字段名。通常是表格数据列的唯一标识	| string	| -
| title	| 设置列的标题。	| string	| -
| fieldTitle|2.8+	| 设置列的字段标题。该属性在筛选列和导出场景中优先级高于 title 属性	| string	| -
| width	| 设置列宽。若不填写，则自动分配；若填写，则支持值为：数字、百分比。如：width: 200/width: '30%'	| number/string	| -
| minWidth	| 设置当前列的最小宽度，一般用于列宽自动分配的情况。其优先级高于基础属性中的cellMinWidth	| number	| 60
| maxWidth 	| 设置当前列的最大宽度。其优先级高于基础属性中的cellMaxWidth	| number	| -
| expandedWidth 	| 设置单元格被展开后的宽度。若设置的值的小于当前列宽，则展开后的列宽保持不变。注：当expandedMode属性为默认值时有效。	| number	| -
| expandedMode 	| 用于设置所有单元格默认展开方式，可选值有：	| string	| - tips悬浮展开方式default多行展开方式（默认） 优先级高于cellExpandedMode基础属性 
|fixed	| 设置列是否固定。可选值有：	| string	| - left固定在左侧right固定在右侧
|sort	| 设置列是否排序。可选值有：	| string	| - true开启排序false禁用排序
|align	| 设置列的对齐方式。可选值有：	| string	| - left左对齐right右对齐center居中

### body属性配置
| 属性名	| 描述	| 类型	| 默认值
| lineStyle	| 设置行样式。可选值有：	| string	| -
|skin	| 设置表格边框风格。可选值：grid|line|row|nob：	| string	| -
|size	| 设置表格尺寸。可选值：sm|md|lg	| string	| -
|even	| 设置表格是否为斑马纹。是否开启隔行背景。	| boolean	| -
|emptyShow	| 设置空数据时的显示内容。	| string	| -
|showIndex	| 设置是否显示序号列。	| boolean	| -
| selectable	| 设置是否显示选择框。	| boolean	| -
| rowActionWidth	| 设置操作列的宽度。	| number	| 175

**opentype取值：**
- `MODAL` - 模态框
- `FORM` - 全屏表单
- `FULL` - 全屏
- `_BLANK` - 新窗口
- `LOCATION` - 当前页跳转
- `PARENT` - 父窗口
- `AJAX` - Ajax请求
- `CONFIRM_AJAX` - 确认后Ajax
- `CONFIRM_MODEL` - 确认模态框

## 四、表达式系统

### 内置函数
- `select(sql)` - 查询数据
- `selectOne(sql)` - 查询单条
- `search(sql)` - 带搜索条件查询
- `count(sql)` - 查询总数
- `insert(sql)` / `insertSimple(param)` - 插入数据
- `update(sql)` / `updateSimple(param)` - 更新数据
- `$("param")` - 获取请求参数
- `$$("session")` - 获取session参数
- `isNotBlank(param)` / `isBlank(param)` - 判空

### 表达式示例

**列表查询：**
```javascript
StringBuilder sql = new StringBuilder();
sql.append("SELECT * FROM users WHERE delete_flag=0");
return search(sql);
```
系统会自动根据请求里面的参数拼接SQL语句

## 四点五、JSON Patch 修改规则（RFC6902）

### 重要：数组索引计算规则

在生成 JSON Patch 操作时，**必须严格按照数组的实际索引**计算路径，**不能忽略任何元素**，包括：
- `component: "hidden"` 的搜索字段
- 不可见的字段
- 任何其他字段

### 列表配置中的数组类型

列表配置中有多种数组需要注意索引计算：
1. **search** - 二维数组，第一层是行，第二层是该行的搜索字段
2. **column** - 一维数组，列配置列表
3. **tableButton** - 一维数组，表头按钮列表
4. **rowButton** - 一维数组，行操作按钮列表

### 索引计算示例

**错误示例（忽略hidden字段）：**
```json
"search": [
  [
    {"name":"ID", "component":"hidden"},      // 这是索引 0，不能忽略！
    {"name":"USERNAME", "component":"input"}  // 这是索引 1
  ]
]
```

如果要修改 `USERNAME` 字段，**错误的路径**是：`/search/0/0` （忽略了hidden字段）
**正确的路径**应该是：`/search/0/1` （包含hidden字段的索引）

### 完整的修改示例

**原始DSL：**
```json
{
  "id": "student-list",
  "name": "学生列表",
  "search": [
    [
      {"name":"STUDENT_ID", "label":"学生ID", "component":"hidden"},
      {"name":"USERNAME", "label":"学生姓名", "component":"input", "operator":"like"},
      {"name":"AGE", "label":"年龄", "component":"input"},
      {"name":"STATUS", "label":"状态", "component":"select"}
    ],
    [
      {"name":"CREATE_TIME", "label":"创建时间", "component":"date", "operator":"between"}
    ]
  ],
  "column": [
    {"name":"STUDENT_ID", "label":"ID", "component":"tdText"},
    {"name":"USERNAME", "label":"学生姓名", "component":"tdText"},
    {"name":"AGE", "label":"年龄", "component":"tdText"}
  ]
}
```

**场景1：修改"学生姓名"搜索字段的 label**
```json
{
  "patch": [
    {
      "op": "replace",
      "path": "/search/0/1/label",
      "value": "姓名"
    }
  ],
  "summary": "将搜索区域的'学生姓名'字段的label修改为'姓名'"
}
```
**路径分析：**
- `/search/0` - 第1行搜索字段
- `/1` - 第2个字段（索引0是hidden的STUDENT_ID，索引1才是USERNAME）
- `/label` - label属性

**场景2：修改列配置中的"学生姓名"列宽**
```json
{
  "patch": [
    {
      "op": "add",
      "path": "/column/1/props",
      "value": {"width": 150}
    }
  ],
  "summary": "设置'学生姓名'列的宽度为150"
}
```
**路径分析：**
- `/column/1` - 第2列（索引0=STUDENT_ID, 索引1=USERNAME）
- `/props` - 添加props属性

**场景3：在第一行搜索字段末尾添加新字段**
```json
{
  "patch": [
    {
      "op": "add",
      "path": "/search/0/-",
      "value": {
        "name": "PHONE",
        "label": "联系电话",
        "component": "input",
        "operator": "like"
      }
    }
  ],
  "summary": "在第一行搜索区域末尾添加'联系电话'字段"
}
```

**场景4：删除"年龄"搜索字段（第一行第三个字段）**
```json
{
  "patch": [
    {
      "op": "remove",
      "path": "/search/0/2"
    }
  ],
  "summary": "删除搜索区域第一行的'年龄'字段"
}
```
**路径分析：**
- `/search/0/2` - 第1行的第3个字段（索引0=STUDENT_ID, 索引1=USERNAME, 索引2=AGE）

**场景5：修改列配置中的第三列**
```json
{
  "patch": [
    {
      "op": "replace",
      "path": "/column/2/label",
      "value": "年龄（岁）"
    }
  ],
  "summary": "将'年龄'列的label修改为'年龄（岁）'"
}
```
**路径分析：**
- `/column/2` - column数组的第3个元素（索引0=STUDENT_ID, 索引1=USERNAME, 索引2=AGE）

### 索引计算核心规则（务必遵守）

1. **包含所有字段**
   - JSON数组中的每个元素都占用一个索引位置
   - **hidden字段也占用索引位置**
   - **不可见字段也占用索引位置**
   - 索引从0开始：第1个元素是索引0，第2个元素是索引1，以此类推

2. **路径构建步骤（以修改search为例）**
   ```
   目标：修改第1行的第2个搜索字段（USERNAME）

   步骤1：找到search - /search
   步骤2：找到第1行（索引0） - /search/0
   步骤3：从索引0开始数，包含hidden字段：
          - 索引0: STUDENT_ID (hidden) ✓
          - 索引1: USERNAME ← 这是目标！
   步骤4：完整路径 - /search/0/1
   ```

3. **路径构建步骤（以修改column为例）**
   ```
   目标：修改第2列（USERNAME）

   步骤1：找到column - /column
   步骤2：从索引0开始数：
          - 索引0: STUDENT_ID ✓
          - 索引1: USERNAME ← 这是目标！
   步骤3：完整路径 - /column/1
   ```

4. **检查清单**
   在生成patch前，请检查：
   - ✅ 是否从0开始计数？
   - ✅ 是否包含了所有元素（包括hidden）？
   - ✅ 路径中的每个索引是否对应实际的数组位置？
   - ✅ 是否遵循了JSON Patch RFC6902标准？

### 多操作示例

如果需要同时修改多个地方，patch 数组可以包含多个操作：

```json
{
  "patch": [
    {
      "op": "replace",
      "path": "/search/0/1/label",
      "value": "学生姓名"
    },
    {
      "op": "replace",
      "path": "/column/1/label",
      "value": "学生姓名"
    },
    {
      "op": "add",
      "path": "/column/1/props",
      "value": {"width": 150}
    }
  ],
  "summary": "统一修改'学生姓名'字段的label，并设置列宽为150"
}
```

## 五、完整示例

### 最小列表配置
```json
{
  "id": "user-list",
  "name": "用户列表",
  "dataSource": "dataSource",

  "body": {
    "showIndex": true,
    "selectable": true
  },

  "search": [
    [
      {
        "name": "NAME",
        "label": "姓名",
        "component": "input",
        "operator": "like"
      },
      {
        "name": "HOBBYS_IDS",
        "label": "爱好",
        "component": "select-multiple",
        "operator": "in",
        "initData": {
          "dataSource": "dataSource",
          "dataSql": "select hobbys_id value,name label from hobbys"
        }
      }
    ]
  ],

  "column": [{
    "name": "ID",
    "label": "ID"
  }, {
    "name": "NAME",
    "label": "姓名"
  }, {
    "name": "STATUS",
    "label": "状态",
    "component": "tdSelect",
    "initData": {
      "dataJson": [
        {"label": "启用", "value": "1"},
        {"label": "禁用", "value": "0"}
      ]
    }
  }],

  "rowButton": [{
    "name": "EDIT",
    "label": "编辑",
    "component": "button-normal",
    "props": {
      "opentype": "MODAL",
      "url": "/topezadmin/form/dsl-user-edit?id={{=d.id}}"
    }
  }],

  "express": {
    "main": "return search('SELECT HOBBYS_IDS,id,name,status FROM users WHERE 1=1')",
    "orderBy": "order by id",
    "groupBy": "group by id"
  }
}
```