# EzAdmin 低代码配置使用说明

ezadmin低代码是一个使用layui与qlexpress，将dsl配置转换成页面的工具，能够快速生成crud页面。
新增入口: /topezadmin/edit/create-
 

### 表单配置结构
```json
{
  "id": "表单ID",
  "name": "表单名称",
  "dataSource": "数据源Bean名称",
  "successUrl": "表单保存成功后的跳转链接",
  "cardList": [{
    "type": "card",
    "label": "卡片标题",
    "fieldList": [{"row":[]},{"row":[]}],  // 字段列表：对象数组格式，每个对象包含row属性（数组），row代表该行的字段
    "buttonList": []           // 按钮列表
  }],

  "buttonList": [],            // 底部按钮

  "initExpress": [],           // 初始化数据
  "submitExpress": [],         // 提交处理
  "deleteExpress": [],         // 删除处理
  "statusExpress": [],         // 状态变更
  "appendHead": [],            // 列表头部追加html内容
  "appendFoot": []             // 列表尾部追加html内容
}
```

#### fieldList 对象数组格式说明 - 实现复杂表单布局的核心机制

**格式规则（重要）：**
- fieldList 是**对象数组**：`[{row:[field1,field2]}, {row:[field3]}]`
- 每个对象包含 **row** 属性，row 是一个数组，代表表单中的**一行**的所有字段
- 每个 row 可以包含**任意数量**的字段（1个、2个、3个、4个甚至更多）最大12列
- 字段的实际宽度通过 **classAppend** 属性控制
- **核心原理**：Layui 栅格系统每行总宽度为 12，字段的 classAppend 宽度值相加应等于或接近 12

**classAppend 宽度与布局说明（详细）：**

1. **Layui 栅格系统基础**
   - 语法：`layui-col-{屏幕尺寸}{宽度}`
   - 屏幕尺寸：xs(手机<768px), sm(平板≥768px), md(桌面≥992px), lg(大屏≥1200px), xl(超大屏≥1920px)
   - 宽度值：1-12，**每行总宽度为 12**
   - 如果一行内字段宽度总和超过12，多余字段会自动换行

2. **常用宽度组合**
   - **单字段占满一行**：`layui-col-md12`（占100%）
   - **两字段均分**：每个设置 `layui-col-md6`（各占50%）
   - **三字段均分**：每个设置 `layui-col-md4`（各占33.3%）
   - **四字段均分**：每个设置 `layui-col-md3`（各占25%）
   - **二八分**：一个 `layui-col-md2`，一个 `layui-col-md10`
   - **三七分**：一个 `layui-col-md4`，一个 `layui-col-md8`
   - **四六分**：一个 `layui-col-md5`，一个 `layui-col-md7`

3. **响应式布局**（不同屏幕显示不同宽度）
   - 语法：多个 class 用空格分隔
   - 示例：`layui-col-xs12 layui-col-sm6 layui-col-md4 layui-col-lg3`
   - 含义：手机全宽，平板占半，桌面占1/3，大屏占1/4

4. **与 fieldList 行配合使用的原则**
   - **简单布局**：一行放多个字段，用 classAppend 控制宽度
   - **复杂布局**：增加行数，在不同行组织字段
   - **混合布局**：有的行放1个字段，有的行放2-3个字段

**基础示例（对象数组结构）：**
```json
"fieldList": [
  {
    "row": [
      {"item_name": "NAME", "label": "姓名", "component": "input", "classAppend": "layui-col-md6"},
      {"item_name": "AGE", "label": "年龄", "component": "input", "classAppend": "layui-col-md6"}
    ]
  },
  {
    "row": [
      {"item_name": "EMAIL", "label": "邮箱", "component": "input", "classAppend": "layui-col-md12"}
    ]
  },
  {
    "row": [
      {"item_name": "PHONE", "label": "电话", "component": "input", "classAppend": "layui-col-md4"},
      {"item_name": "ADDRESS", "label": "地址", "component": "input", "classAppend": "layui-col-md8"}
    ]
  }
]
```
**布局效果说明：**
```
┌─────────────────────────────────────────────────────┐
│ 第一行（两个字段各占50%）                            │
│ ┌──────────────────────┬──────────────────────┐    │
│ │ 姓名 (6/12=50%)      │ 年龄 (6/12=50%)      │    │
│ └──────────────────────┴──────────────────────┘    │
│                                                      │
│ 第二行（一个字段占100%）                             │
│ ┌───────────────────────────────────────────┐      │
│ │ 邮箱 (12/12=100%)                          │      │
│ └───────────────────────────────────────────┘      │
│                                                      │
│ 第三行（两个字段，一个33.3%，一个66.7%）             │
│ ┌──────────────┬─────────────────────────────┐     │
│ │ 电话 (4/12)  │ 地址 (8/12)                  │     │
│ └──────────────┴─────────────────────────────┘     │
└─────────────────────────────────────────────────────┘
```

**复杂布局示例（多种组合）：**
```json
"fieldList": [
  {
    "row": [
      {"item_name": "TITLE", "label": "标题", "component": "input", "classAppend": "layui-col-md12"}
    ]
  },
  {
    "row": [
      {"item_name": "CATEGORY", "label": "分类", "component": "select", "classAppend": "layui-col-md4"},
      {"item_name": "TAG", "label": "标签", "component": "select", "classAppend": "layui-col-md4"},
      {"item_name": "STATUS", "label": "状态", "component": "select", "classAppend": "layui-col-md4"}
    ]
  },
  {
    "row": [
      {"item_name": "AUTHOR", "label": "作者", "component": "input", "classAppend": "layui-col-md3"},
      {"item_name": "PUBLISH_DATE", "label": "发布日期", "component": "date", "classAppend": "layui-col-md3"},
      {"item_name": "VIEW_COUNT", "label": "浏览量", "component": "input", "classAppend": "layui-col-md3"},
      {"item_name": "LIKE_COUNT", "label": "点赞数", "component": "input", "classAppend": "layui-col-md3"}
    ]
  },
  {
    "row": [
      {"item_name": "SUMMARY", "label": "摘要", "component": "textarea", "classAppend": "layui-col-md12"}
    ]
  },
  {
    "row": [
      {"item_name": "CONTENT", "label": "内容", "component": "tinymce", "classAppend": "layui-col-md12"}
    ]
  }
]
```
**布局效果说明：**
```
┌─────────────────────────────────────────────────────┐
│ 第一行：标题字段占满整行                              │
│ 第二行：三个下拉框均分（各占1/3）                     │
│ 第三行：四个字段均分（各占1/4）                       │
│ 第四行：摘要文本域占满整行                            │
│ 第五行：富文本编辑器占满整行                          │
└─────────────────────────────────────────────────────┘
```

**响应式布局示例：**
```json
"fieldList": [
  {
    "row": [
      {
        "item_name": "NAME",
        "label": "姓名",
        "component": "input",
        "classAppend": "layui-col-xs12 layui-col-sm6 layui-col-md4"
      },
      {
        "item_name": "AGE",
        "label": "年龄",
        "component": "input",
        "classAppend": "layui-col-xs12 layui-col-sm6 layui-col-md4"
      },
      {
        "item_name": "GENDER",
        "label": "性别",
        "component": "select",
        "classAppend": "layui-col-xs12 layui-col-sm12 layui-col-md4"
      }
    ]
  }
]
```
**响应式效果说明：**
- **手机屏幕（xs）**：每个字段占12/12（100%），三个字段垂直排列
- **平板屏幕（sm）**：姓名和年龄各占6/12（50%）在第一行，性别占12/12（100%）在第二行
- **桌面屏幕（md及以上）**：三个字段各占4/12（33.3%），在同一行水平排列

**常见布局模式：**

1. **信息分组展示**（多个 card，每个 card 内用 fieldList 布局）
```json
{
  "cardList": [
    {
      "type": "card",
      "label": "基本信息",
      "fieldList": [
        {
          "row": [
            {"item_name": "NAME", "classAppend": "layui-col-md6"},
            {"item_name": "ID_CARD", "classAppend": "layui-col-md6"}
          ]
        }
      ]
    },
    {
      "type": "card",
      "label": "联系方式",
      "fieldList": [
        {
          "row": [
            {"item_name": "PHONE", "classAppend": "layui-col-md6"},
            {"item_name": "EMAIL", "classAppend": "layui-col-md6"}
          ]
        }
      ]
    }
  ]
}
```

2. **主从表单**（主字段在上，明细字段在下）
```json
"fieldList": [
  {
    "row": [
      {"item_name": "ORDER_NO", "label": "订单号", "classAppend": "layui-col-md6"},
      {"item_name": "ORDER_DATE", "label": "订单日期", "classAppend": "layui-col-md6"}
    ]
  },
  {
    "row": [
      {"item_name": "CUSTOMER_NAME", "label": "客户名称", "classAppend": "layui-col-md12"}
    ]
  },
  {
    "row": [
      {"item_name": "ITEMS", "label": "订单明细", "component": "table", "classAppend": "layui-col-md12"}
    ]
  }
]
```

**最佳实践建议：**
1. **一般表单**：每行2-3个字段，使用 `layui-col-md6` 或 `layui-col-md4`
2. **重要字段**：独占一行，使用 `layui-col-md12`
3. **文本域和富文本**：建议独占一行 `layui-col-md12`
4. **移动端优先**：如果需要移动端支持，务必添加 `layui-col-xs12` 确保在小屏幕上每个字段占满一行
5. **宽度总和**：确保同一行内所有字段的宽度数字相加等于12，避免布局错乱

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

  "props": {
    "props": {// Layui原生属性 或自定义属性
      "placeholder": "0-9999",
      "type": "number",
      "step": "1",
      "min": "0",
      "lay-affix": "number",
      "maxlength": "4",
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
}
```
 

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

**查询：**
```javascript
StringBuilder sql = new StringBuilder();
sql.append("SELECT NAME,ID,USERSEX FROM users WHERE delete_flag=0");
return search(sql);
```

**表单保存：**
```javascript
if(!isNotBlank("ID")) {
    // 新增
    param = new InsertParam();
    param.table("users");
    param.add("#{NAME}");
    param.add("#{CREATE_TIME,value=NOW()}");
    return insertSimple(param);
} else {
    // 更新
    param = new UpdateParam();
    param.table("users");
    param.add("#{NAME}");
    param.where("id=#{ID}");
    return updateSimple(param);
}
```

## 五、JSON Patch 修改规则（RFC6902）

### 重要：数组索引计算规则

在生成 JSON Patch 操作时，**必须严格按照数组的实际索引**计算路径，**不能忽略任何元素**，包括：
- `component: "hidden"` 的字段
- 不可见的字段
- 任何其他字段

### 索引计算示例

**错误示例（忽略hidden字段）：**
```json
"fieldList": [
  [
    {"item_name":"students_id", "component":"hidden"},  // 这是索引 0，不能忽略！
    {"item_name":"username", "component":"input"}        // 这是索引 1
  ]
]
```

如果要修改 `username` 字段，**错误的路径**是：`/cardList/0/fieldList/0/0` （忽略了hidden字段）
**正确的路径**应该是：`/cardList/0/fieldList/0/1` （包含hidden字段的索引）

### JSON Patch 操作类型

1. **replace（替换）**
```json
{
  "op": "replace",
  "path": "/cardList/0/fieldList/0/1/label",
  "value": "新的标签名称"
}
```

2. **add（添加）**
```json
{
  "op": "add",
  "path": "/cardList/0/fieldList/0/-",
  "value": {"item_name":"NEW_FIELD", "label":"新字段", "component":"input"}
}
```

3. **remove（删除）**
```json
{
  "op": "remove",
  "path": "/cardList/0/fieldList/0/2"
}
```

### 完整的修改示例

**原始DSL：**
```json
{
  "cardList": [{
    "type": "card",
    "label": "学生信息",
    "fieldList": [
      {
        "row": [
          {"item_name":"STUDENT_ID", "label":"ID", "component":"hidden", "classAppend":"layui-col-md0"},
          {"item_name":"USERNAME", "label":"学生姓名", "component":"input", "classAppend":"layui-col-md6"},
          {"item_name":"AGE", "label":"年龄", "component":"input", "classAppend":"layui-col-md6"}
        ]
      },
      {
        "row": [
          {"item_name":"EMAIL", "label":"邮箱", "component":"input", "classAppend":"layui-col-md12"}
        ]
      }
    ]
  }]
}
```

**场景1：修改"学生姓名"的 label**
```json
{
  "patch": [
    {
      "op": "replace",
      "path": "/cardList/0/fieldList/0/row/1/label",
      "value": "姓名（真实）"
    }
  ],
  "summary": "将'学生姓名'字段的label修改为'姓名（真实）'"
}
```
**路径分析：**
- `/cardList/0` - 第1个card
- `/fieldList/0` - 第1个行对象
- `/row/1` - row数组中的第2个字段（索引0是hidden的STUDENT_ID，索引1才是USERNAME）
- `/label` - label属性

**场景2：修改"年龄"字段的宽度**
```json
{
  "patch": [
    {
      "op": "replace",
      "path": "/cardList/0/fieldList/0/row/2/classAppend",
      "value": "layui-col-md4"
    }
  ],
  "summary": "将'年龄'字段的宽度修改为1/3"
}
```
**路径分析：**
- `/cardList/0/fieldList/0/row/2` - 第1行对象的row数组中的第3个字段（索引0=STUDENT_ID, 索引1=USERNAME, 索引2=AGE）

**场景3：在第一行row数组末尾添加新字段**
```json
{
  "patch": [
    {
      "op": "add",
      "path": "/cardList/0/fieldList/0/row/-",
      "value": {
        "item_name": "PHONE",
        "label": "联系电话",
        "component": "input",
        "classAppend": "layui-col-md6"
      }
    }
  ],
  "summary": "在第一行末尾添加'联系电话'字段"
}
```

**场景4：删除"邮箱"字段（第二行的第一个字段）**
```json
{
  "patch": [
    {
      "op": "remove",
      "path": "/cardList/0/fieldList/1/row/0"
    }
  ],
  "summary": "删除第二行的'邮箱'字段"
}
```

### 索引计算核心规则（务必遵守）

1. **包含所有字段**
   - JSON数组中的每个元素都占用一个索引位置
   - **hidden字段也占用索引位置**
   - **不可见字段也占用索引位置**
   - 索引从0开始：第1个元素是索引0，第2个元素是索引1，以此类推

2. **路径构建步骤**
   ```
   目标：修改第1个card的第1行对象的row数组中的第2个字段（USERNAME）

   步骤1：找到cardList - /cardList
   步骤2：找到第1个card（索引0） - /cardList/0
   步骤3：找到fieldList - /cardList/0/fieldList
   步骤4：找到第1个行对象（索引0） - /cardList/0/fieldList/0
   步骤5：找到row数组 - /cardList/0/fieldList/0/row
   步骤6：从索引0开始数row数组中的元素，包含hidden字段：
          - 索引0: STUDENT_ID (hidden) ✓
          - 索引1: USERNAME ← 这是目标！
   步骤7：完整路径 - /cardList/0/fieldList/0/row/1
   ```

3. **检查清单**
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
      "path": "/cardList/0/fieldList/0/1/label",
      "value": "学生姓名（必填）"
    },
    {
      "op": "replace",
      "path": "/cardList/0/fieldList/0/1/props/lay-verify",
      "value": "required"
    },
    {
      "op": "replace",
      "path": "/cardList/0/fieldList/0/2/classAppend",
      "value": "layui-col-md4"
    }
  ],
  "summary": "修改学生姓名为必填，并调整年龄字段宽度为1/3"
}
```

## 六、完整示例

 

### 最小表单配置
```json
{
  "id": "user-form",
  "name": "用户表单",
  "dataSource": "dataSource",

  "cardList": [{
    "type": "card",
    "label": "基本信息",
    "fieldList": [
      {
        "row": [
          {
            "item_name": "ID",
            "label": "ID",
            "component": "hidden"
          }
        ]
      },
      {
        "row": [
          {
            "item_name": "NAME",
            "label": "姓名",
            "component": "input",
            "classAppend": "layui-col-md6",
            "props": {
              "placeholder": "请输入姓名",
              "lay-verify": "required"
            }
          },
          {
            "item_name": "STATUS",
            "label": "状态",
            "component": "select",
            "classAppend": "layui-col-md6",
            "initData": {
              "dataJson": [
                {"label": "启用", "value": "1"},
                {"label": "禁用", "value": "0"}
              ]
            }
          }
        ]
      }
    ]
  }],

  "initExpress": ["return selectOne('SELECT * FROM users WHERE id=${ID}')"],

  "submitExpress": [
    "if(!isNotBlank('id')) {",
    "  return insert('INSERT INTO users(name,status) VALUES(#{NAME},#{STATUS})')",
    "} else {",
    "  return update('UPDATE users SET name=#{NAME},status=#{STATUS} WHERE id=#{ID}')",
    "}"
  ],
  "appendHead": "",
  "appendFoot": ""
}
```