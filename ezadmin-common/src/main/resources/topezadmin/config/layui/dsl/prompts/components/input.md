# input 组件

## 组件说明

单行文本输入框，用于接收用户输入的单行文本内容。

## 使用场景

- 表单中的姓名、账号、标题等单行文本输入
- 搜索条件中的关键词输入
- 需要用户输入简短文本的任何场景

## 基本配置

```json
{
  "item_name": "name",
  "label": "姓名",
  "component": "input",
  "classAppend":"layui-col-md8",
  "props": {
    "placeholder": "请输入姓名",
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

## 配置项说明

### 基础属性

| 属性          | 类型     | 必填     | 说明                              |
|-------------|--------|--------|---------------------------------|
| item_name   | String | 是      | 字段名，对应数据库字段                     |
| label       | String | 是      | 显示标签                            |
| component   | String | 是      | 固定值：`input`                     |
| operator    | String | 否      | 查询操作符(搜索项使用)，如 `EQ`、`LIKE`      |
| description | String | 字段说明文本 | `"可选择多个"`                       |
| props       | Object | 否      | 组件属性,支持所有layui input 属性，html5属性 |
| classAppend | String | 否      | layui-col-md8 主要使用layui布局设置占用宽度 |

### props 属性

| 属性          | 类型      | 说明          | 示例                                 |
|-------------|---------|-------------|------------------------------------|
| placeholder | String  | 输入提示文本      | `"请输入姓名"`                          |
| lay-verify  | String  | Layui表单验证规则 | `"required"`, `"phone"`, `"email"` |
| maxlength   | Number  | 最大输入长度      | `50`                               |
| disabled    | Boolean | 是否禁用        | `true`                             |
| readonly    | Boolean | 是否只读        | `true`                             |
| required    | Boolean | 是否必填(显示红色*) | `true`                             |
| description | String  | 字段说明文本      | `"请输入真实姓名"`                        |

## 完整示例

### 表单中使用

```json
{
  "id": "user-form",
  "name": "用户表单",
  "dataSource": "dataSource",
  "cardList": [{
    "type": "card",
    "label": "基本信息",
    "classAppend":"layui-col-md12",
    "fieldList": [{
      "item_name": "username",
      "label": "用户名",
      "component": "input",
      "classAppend":"layui-col-md8",
      "props": {
        "placeholder": "请输入用户名",
        "lay-verify": "required",
        "maxlength": 20
      }
    }, {
      "item_name": "email",
      "label": "邮箱",
      "component": "input",
      "classAppend":"layui-col-md8",
      "props": {
        "placeholder": "请输入邮箱",
        "lay-verify": "email",
        "description": "用于接收系统通知"
      }
    }, {
      "item_name": "phone",
      "label": "手机号",
      "component": "input",
      "classAppend":"layui-col-md8",
      "props": {
        "placeholder": "请输入手机号",
        "lay-verify": "phone|required",
        "maxlength": 11,
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
    }]
  }],
  "initExpress": ["return selectOne('SELECT * FROM users WHERE id=${ID}')"],
  "submitExpress": [
    "if(!isNotBlank('id')) {",
    "  return insert('INSERT INTO users(username,email,phone) VALUES(#{username},#{email},#{phone})')",
    "} else {",
    "  return update('UPDATE users SET username=#{username},email=#{email},phone=#{phone} WHERE id=#{id}')",
    "}"
  ]
}
```

### 搜索项中使用

```json
{
  "item_name": "username",
  "label": "用户名",
  "component": "input",
  "operator": "like",
  "props": {
    "placeholder": "请输入用户名"
  }
}
```

### 精确查询

```json
{
  "item_name": "order_no",
  "label": "订单号",
  "component": "input",
  "operator": "eq",
  "props": {
    "placeholder": "请输入订单号"
  }
}
```

## 验证规则

Layui 内置验证规则：

| 规则       | 说明    |
|----------|-------|
| required | 必填项   |
| phone    | 手机号   |
| email    | 邮箱    |
| url      | URL地址 |
| number   | 数字    |
| date     | 日期    |
| identity | 身份证号  |

自定义验证规则可组合使用，用 `|` 分隔：

```json
{
  "lay-verify": "required|number"
}
```

## 注意事项

1. **字段映射**：`item_name` 必须与数据库字段名或查询别名一致
2. **验证规则**：使用 `lay-verify` 属性配置验证规则，支持Layui所有内置规则
3. **必填标识**：设置 `props.required: true` 会在标签前显示红色星号
4. **字段说明**：使用 `props.description` 可以在输入框下方显示辅助说明文本

## 相关组件

- [textarea](./textarea.md) - 多行文本输入
- [hidden](./hidden.md) - 隐藏字段
- [tinymce](./tinymce.md) - 富文本编辑器
