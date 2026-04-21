# radio 组件

## 组件说明

单选框，用于从多个选项中选择一个值，所有选项平铺展示。

## 使用场景

- 性别选择
- 是/否选择
- 状态选择（选项较少时）
- 任何单选场景（选项数量3-5个为宜）

## 基本配置

```json
{
  "item_name": "gender",
  "label": "性别",
  "component": "radio",
  "col": 6,
  "initData": {
    "dataJson": [
      {"label": "男", "value": "1"},
      {"label": "女", "value": "2"}
    ]
  }
}
```

## 配置项说明

### 基础属性

| 属性          | 类型     | 必填     | 说明                              |
|-------------|--------|--------|---------------------------------|
| item_name   | String | 是      | 字段名，对应数据库字段                     |
| label       | String | 是      | 显示标签                            |
| component   | String | 是      | 固定值：`radio`                     |
| classAppend | String | 否      | layui-col-md8 主要使用layui布局设置占用宽度 |
| operator    | String | 否      | 查询操作符，通常使用 `eq`                 |
| initData    | Object | 是      | 数据源配置                           |
| description | String | 字段说明文本 | `"可选择多个"`                       |
| props       | Object | 否      | 组件属性,支持所有layui radio属性，html5属性  |

### initData 配置

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

| 属性          | 类型      | 说明          | 示例           |
|-------------|---------|-------------|--------------|
| lay-verify  | String  | Layui表单验证规则 | `"required"` |
| disabled    | Boolean | 是否禁用        | `true`       |
| required    | Boolean | 是否必填(显示红色*) | `true`       |
| description | String  | 字段说明文本      | `"请选择性别"`    |

## 完整示例

### 性别选择

```json
{
  "item_name": "gender",
  "label": "性别",
  "component": "radio",
  "classAppend":"layui-col-md8",
  "initData": {
    "dataJson": [
      {"label": "男", "value": "1"},
      {"label": "女", "value": "2"}
    ]
  },
  "props": {
    "lay-verify": "required",
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

### 是否选择

```json
{
  "item_name": "is_public",
  "label": "是否公开",
  "component": "radio",
  "classAppend":"layui-col-md8",
  "initData": {
    "dataJson": [
      {"label": "是", "value": "1"},
      {"label": "否", "value": "0"}
    ]
  },
  "props": {
    "lay-verify": "required"
  }
}
```

### 状态选择

```json
{
  "item_name": "status",
  "label": "状态",
  "component": "radio",
  "classAppend":"layui-col-md8",
  "initData": {
    "dataJson": [
      {"label": "启用", "value": "1"},
      {"label": "禁用", "value": "0"},
      {"label": "待审核", "value": "2"}
    ]
  }
}
```

### 搜索项中使用

```json
{
  "search": [{
    "item_name": "gender",
    "label": "性别",
    "component": "radio",
    "operator": "eq",
    "initData": {
      "dataJson": [
        {"label": "全部", "value": ""},
        {"label": "男", "value": "1"},
        {"label": "女", "value": "2"}
      ]
    }
  }]
}
```

### 动态数据选择

```json
{
  "item_name": "level",
  "label": "会员等级",
  "component": "radio",
  "classAppend":"layui-col-md8",
  "initData": {
    "dataSource": "dataSource",
    "dataSql": "SELECT id value, level_name label FROM member_levels WHERE status=1 ORDER BY sort"
  },
  "props": {
    "lay-verify": "required"
  }
}
```

### 完整表单示例

```json
{
  "id": "user-form",
  "name": "用户表单",
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
        "placeholder": "请输入用户名",
        "lay-verify": "required"
      }
    }, {
      "item_name": "gender",
      "label": "性别",
      "component": "radio",
      "classAppend":"layui-col-md8",
      "initData": {
        "dataJson": [
          {"label": "男", "value": "1"},
          {"label": "女", "value": "2"}
        ]
      },
      "props": {
        "lay-verify": "required"
      }
    }, {
      "item_name": "email",
      "label": "邮箱",
      "component": "input",
      "classAppend":"layui-col-md8",
      "props": {
        "placeholder": "请输入邮箱",
        "lay-verify": "email"
      }
    }, {
      "item_name": "is_subscribe",
      "label": "订阅通知",
      "component": "radio",
      "classAppend":"layui-col-md8",
      "initData": {
        "dataJson": [
          {"label": "是", "value": "1"},
          {"label": "否", "value": "0"}
        ]
      }
    }, {
      "item_name": "status",
      "label": "状态",
      "component": "radio",
      "classAppend":"layui-col-md8",
      "initData": {
        "dataJson": [
          {"label": "启用", "value": "1"},
          {"label": "禁用", "value": "0"}
        ]
      },
      "props": {
        "lay-verify": "required",
        "description": "启用后用户可以正常登录"
      }
    }]
  }],
  "initExpress": [
    "map = selectOne('SELECT * FROM users WHERE id=${ID}');",
    "if(!map) {",
    "  map = new HashMap();",
    "  map.put('gender', '1');",
    "  map.put('is_subscribe', '1');",
    "  map.put('status', '1');",
    "}",
    "return map;"
  ],
  "submitExpress": [
    "if(!isNotBlank('id')) {",
    "  return insert('INSERT INTO users(username,gender,email,is_subscribe,status,create_time) VALUES(#{username},#{gender},#{email},#{is_subscribe},#{status},NOW())')",
    "} else {",
    "  return update('UPDATE users SET username=#{username},gender=#{gender},email=#{email},is_subscribe=#{is_subscribe},status=#{status},update_time=NOW() WHERE id=#{id}')",
    "}"
  ]
}
```

## 监听选择事件

可以通过 Layui 的表单监听实现联动：

```javascript
// 监听单选框变化
form.on('radio(lay-filter-gender)', function(data){
  console.log('选中的值：', data.value);
  console.log('选中项的文本：', data.elem.title);

  // 根据选择显示/隐藏其他字段
  if(data.value == '1') {
    // 男性
  } else {
    // 女性
  }
});
```

## 注意事项

1. **选项数量**：单选框适合3-5个选项，选项过多建议使用 select 组件
2. **数据格式**：选项数据必须包含 `label` 和 `value` 字段
3. **默认值**：可在 initExpress 中设置默认选中项
4. **必填验证**：使用 `lay-verify: "required"` 进行必填验证
5. **字段映射**：`item_name` 存储的是 `value` 值
6. **SQL查询**：dataSql中必须使用别名 `value` 和 `label`

## 与 select 组件的选择

| 场景     | 推荐组件   | 原因               |
|--------|--------|------------------|
| 2-5个选项 | radio  | 所有选项一目了然，不需要点击展开 |
| 5个以上选项 | select | 节省页面空间，支持搜索      |
| 是/否选择  | radio  | 更直观              |
| 性别选择   | radio  | 更符合用户习惯          |
| 状态选择   | 都可以    | 根据选项数量决定         |

## 常见问题

### 如何设置默认选中？

在 initExpress 中设置字段值：

```javascript
{
  "initExpress": [
    "map = new HashMap();",
    "map.put('gender', '1');",  // 默认选中男
    "return map;"
  ]
}
```

### 选项不显示？

检查：

1. initData 配置是否正确
2. 数据格式是否包含 label 和 value
3. SQL查询是否有数据返回

### 如何实现联动？

使用 Layui 的 form.on 监听：

```javascript
form.on('radio(lay-filter-item_name)', function(data){
  // 处理联动逻辑
});
```

### 单选框无法选中？

检查：

1. 是否有多个单选框使用了相同的 item_name
2. Layui 表单是否正确渲染：`form.render('radio')`

## 相关组件

- [checkbox](./checkbox.md) - 复选框（多选）
- [select](./select.md) - 下拉选择（选项较多时使用）
