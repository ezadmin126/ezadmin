# select-span 组件

## 组件说明

下拉值只读展示组件，用于在表单中以只读文本形式展示下拉选项的值。与 tdSelect 类似，但用于表单而非列表。

## 使用场景

- 表单详情页面（只读模式）
- 审批流程中的信息展示
- 需要展示下拉选项但不允许修改的场景
- 表单内嵌列表的只读展示

## 基本配置

```json
{
  "item_name": "status",
  "label": "状态",
  "component": "select-span",
  "col": 6,
  "initData": {
    "dataJson": [
      {"label": "启用", "value": "1"},
      {"label": "禁用", "value": "0"}
    ]
  }
}
```

## 配置项说明

### 基础属性

| 属性          | 类型     | 必填 | 说明                              |
|-------------|--------|----|---------------------------------|
| item_name   | String | 是  | 字段名，对应数据库字段                     |
| label       | String | 是  | 显示标签                            |
| component   | String | 是  | 固定值：`select-span`               |
| classAppend | String | 否  | layui-col-md8 主要使用layui布局设置占用宽度 |
| initData    | Object | 是  | 数据源配置                           |

### initData 配置

与 select 组件完全相同，支持 dataJson 和 dataSql：

**静态数据（dataJson）：**

```json
{
  "initData": {
    "dataJson": [
      {"label": "选项1", "value": "1"},
      {"label": "选项2", "value": "2"}
    ]
  }
}
```

**动态数据（dataSql）：**

```json
{
  "initData": {
    "dataSource": "dataSource",
    "dataSql": "SELECT id value, name label FROM table_name"
  }
}
```

### props 属性

| 属性          | 类型     | 说明     | 示例           |
|-------------|--------|--------|--------------|
| description | String | 字段说明文本 | `"当前状态不可修改"` |

## 完整示例

### 详情页面

```json
{
  "id": "user-detail",
  "name": "用户详情",
  "dataSource": "dataSource",
  "cardList": [{
    "type": "card",
    "label": "基本信息",
    "classAppend":"layui-col-md8",
    "fieldList": [{
      "item_name": "username",
      "label": "用户名",
      "component": "input",
      "classAppend":"layui-col-md8",
      "props": {
        "readonly": true
      }
    }, {
      "item_name": "gender",
      "label": "性别",
      "component": "select-span",
      "classAppend":"layui-col-md8",
      "initData": {
        "dataJson": [
          {"label": "男", "value": "1"},
          {"label": "女", "value": "2"}
        ]
      }
    }, {
      "item_name": "status",
      "label": "状态",
      "component": "select-span",
      "classAppend":"layui-col-md8"   ,
      "initData": {
        "dataJson": [
          {"label": "启用", "value": "1"},
          {"label": "禁用", "value": "0"}
        ]
      }
    }, {
      "item_name": "level",
      "label": "会员等级",
      "component": "select-span",
      "classAppend":"layui-col-md8",
      "initData": {
        "dataSource": "dataSource",
        "dataSql": "SELECT id value, level_name label FROM member_levels"
      }
    }]
  }],
  "initExpress": ["return selectOne('SELECT * FROM users WHERE id=${ID}')"]
}
```

### 审批表单

```json
{
  "id": "approval-form",
  "name": "审批单",
  "dataSource": "dataSource",
  "cardList": [{
    "type": "card",
    "label": "申请信息",
    "classAppend":"layui-col-md8",
    "fieldList": [{
      "item_name": "apply_user",
      "label": "申请人",
      "component": "input",
      "classAppend":"layui-col-md8",
      "props": {
        "readonly": true
      }
    }, {
      "item_name": "apply_type",
      "label": "申请类型",
      "component": "select-span",
      "classAppend":"layui-col-md8",
      "initData": {
        "dataJson": [
          {"label": "请假", "value": "leave"},
          {"label": "报销", "value": "expense"},
          {"label": "采购", "value": "purchase"}
        ]
      }
    }, {
      "item_name": "reason",
      "label": "申请原因",
      "component": "textarea",
      "classAppend":"layui-col-md8",
      "props": {
        "readonly": true,
        "rows": 4
      }
    }]
  }, {
    "type": "card",
    "label": "审批操作",
    "classAppend":"layui-col-md8",
    "fieldList": [{
      "item_name": "approval_result",
      "label": "审批结果",
      "component": "radio",
      "classAppend":"layui-col-md8",
      "initData": {
        "dataJson": [
          {"label": "通过", "value": "approved"},
          {"label": "驳回", "value": "rejected"}
        ]
      }
    }, {
      "item_name": "approval_comment",
      "label": "审批意见",
      "component": "textarea",
      "classAppend":"layui-col-md8",
      "props": {
        "rows": 4
      }
    }]
  }],
  "initExpress": ["return selectOne('SELECT * FROM approvals WHERE id=${ID}')"],
  "submitExpress": [
    "return update('UPDATE approvals SET approval_result=#{approval_result},approval_comment=#{approval_comment},approval_time=NOW() WHERE id=#{id}')"
  ]
}
```

### 混合使用（部分可编辑）

```json
{
  "id": "order-form",
  "name": "订单表单",
  "dataSource": "dataSource",
  "cardList": [{
    "type": "card",
    "label": "订单信息",
    "classAppend":"layui-col-md8",
    "fieldList": [{
      "item_name": "order_no",
      "label": "订单号",
      "component": "input",
      "classAppend":"layui-col-md8",
      "props": {
        "readonly": true
      }
    }, {
      "item_name": "order_type",
      "label": "订单类型",
      "component": "select-span",
      "classAppend":"layui-col-md8",
      "initData": {
        "dataJson": [
          {"label": "普通订单", "value": "normal"},
          {"label": "预售订单", "value": "presale"}
        ]
      },
      "props": {
        "description": "订单类型不可修改"
      }
    }, {
      "item_name": "order_status",
      "label": "订单状态",
      "component": "select",
      "classAppend":"layui-col-md8",
      "initData": {
        "dataJson": [
          {"label": "待付款", "value": "pending"},
          {"label": "已付款", "value": "paid"},
          {"label": "已发货", "value": "shipped"},
          {"label": "已完成", "value": "completed"}
        ]
      }
    }, {
      "item_name": "remark",
      "label": "备注",
      "component": "textarea",
      "classAppend":"layui-col-md8",
      "props": {
        "rows": 3
      }
    }]
  }],
  "initExpress": ["return selectOne('SELECT * FROM orders WHERE id=${ID}')"],
  "submitExpress": [
    "return update('UPDATE orders SET order_status=#{order_status},remark=#{remark},update_time=NOW() WHERE id=#{id}')"
  ]
}
```

## 数据处理

### 值转换

组件会自动将字段的 value 值转换为对应的 label 文本显示：

```
字段值: "1"
显示文本: "启用"
```

### 空值处理

如果字段值为空或未匹配到对应的选项，显示空字符串。

### 隐藏字段

组件会自动创建一个隐藏的 input 字段用于存储原始值，确保表单提交时能正确传递数据。

## 与其他组件的区别

| 组件          | 使用位置 | 是否可编辑 | 说明      |
|-------------|------|-------|---------|
| select      | 表单   | 是     | 下拉选择框   |
| select-span | 表单   | 否     | 只读文本展示  |
| tdSelect    | 列表   | 否     | 列表中的值展示 |

## 使用场景对比

### 何时使用 select-span？

- 表单详情查看页面
- 审批流程中的申请信息展示
- 某些字段创建后不允许修改
- 需要在表单中展示下拉值但不可编辑

### 何时使用 select？

- 需要用户选择的场景
- 可编辑的表单字段

### 何时使用 tdSelect？

- 列表的 column 配置中
- 纯展示场景

## 注意事项

1. **数据源一致性**：initData 配置应与对应的 select 组件保持一致

2. **只读展示**：组件只负责展示，不参与数据修改

3. **隐藏字段**：组件内部包含隐藏的 input，确保表单提交时能传递原始值

4. **样式显示**：以普通文本形式显示，无边框、无背景色

5. **数据格式**：支持的数据格式与 select 组件完全相同

6. **SQL查询**：dataSql 必须使用 `value` 和 `label` 别名

## 常见问题

### 显示为空？

检查：

1. 字段值是否正确
2. initData 中是否有对应的 value
3. SQL查询是否返回数据

### 如何实现条件只读？

根据条件使用不同组件：

```json
// 通过前端判断使用哪个组件
// 或在后端动态生成表单配置
```

### 值匹配不上？

检查：

1. 数据类型是否一致（字符串 vs 数字）
2. value 是否完全匹配

## 相关组件

- [select](./select.md) - 下拉选择（可编辑）
- [tdSelect](./table-display.md#tdselect) - 列表中的下拉值展示
- [radio](./radio.md) - 单选框（可编辑）
- [input](./input.md) - 文本输入（设置readonly实现只读）
