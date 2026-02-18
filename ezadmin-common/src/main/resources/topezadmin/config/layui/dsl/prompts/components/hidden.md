# hidden 组件

## 组件说明

隐藏输入框，用于在表单中传递不需要用户看到但需要提交的数据，如ID、状态标识等。

## 使用场景

- 主键ID（编辑时传递）
- 父级ID（新增子记录时）
- 状态标识（默认值）
- 其他不需要用户看到的辅助字段

## 基本配置

```json
{
  "item_name": "id",
  "label": "ID",
  "component": "hidden",
  "col": 0
}
```

## 配置项说明

### 基础属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| item_name | String | 是 | 字段名，对应数据库字段 |
| label | String | 否 | 显示标签（隐藏字段通常不显示） |
| component | String | 是 | 固定值：`hidden` |
| col | Number | 否 | 建议设置为 `0` |

### props 属性

| 属性 | 类型 | 说明 | 示例 |
|------|------|------|------|
| value | String/Number | 默认值 | `"0"`, `1` |

## 完整示例

### 表单ID字段

```json
{
  "id": "user-form",
  "name": "用户表单",
  "dataSource": "dataSource",
  "cardList": [{
    "type": "card",
    "label": "基本信息",
    "col": 12,
    "fieldList": [{
      "item_name": "id",
      "label": "ID",
      "component": "hidden",
      "col": 0
    }, {
      "item_name": "username",
      "label": "用户名",
      "component": "input",
      "col": 6,
      "props": {
        "placeholder": "请输入用户名",
        "lay-verify": "required"
      }
    }, {
      "item_name": "status",
      "label": "状态",
      "component": "select",
      "col": 6,
      "initData": {
        "dataJson": [
          {"label": "启用", "value": "1"},
          {"label": "禁用", "value": "0"}
        ]
      }
    }]
  }],
  "initExpress": ["return selectOne('SELECT * FROM users WHERE id=${ID}')"],
  "submitExpress": [
    "if(!isNotBlank('id')) {",
    "  return insert('INSERT INTO users(username,status) VALUES(#{username},#{status})')",
    "} else {",
    "  return update('UPDATE users SET username=#{username},status=#{status} WHERE id=#{id}')",
    "}"
  ]
}
```

### 父级ID传递

```json
{
  "id": "comment-form",
  "name": "评论表单",
  "dataSource": "dataSource",
  "cardList": [{
    "type": "card",
    "label": "评论信息",
    "col": 12,
    "fieldList": [{
      "item_name": "article_id",
      "label": "文章ID",
      "component": "hidden",
      "col": 0
    }, {
      "item_name": "parent_id",
      "label": "父评论ID",
      "component": "hidden",
      "col": 0,
      "props": {
        "value": "0"
      }
    }, {
      "item_name": "content",
      "label": "评论内容",
      "component": "textarea",
      "col": 12,
      "props": {
        "placeholder": "请输入评论内容",
        "lay-verify": "required",
        "rows": 5
      }
    }]
  }],
  "submitExpress": [
    "return insert('INSERT INTO comments(article_id,parent_id,content,create_time) VALUES(#{article_id},#{parent_id},#{content},NOW())')"
  ]
}
```

### 默认状态值

```json
{
  "fieldList": [{
    "item_name": "delete_flag",
    "label": "删除标识",
    "component": "hidden",
    "col": 0,
    "props": {
      "value": "0"
    }
  }, {
    "item_name": "create_user",
    "label": "创建人",
    "component": "hidden",
    "col": 0
  }]
}
```

### 多个隐藏字段

```json
{
  "cardList": [{
    "type": "card",
    "label": "订单信息",
    "col": 12,
    "fieldList": [{
      "item_name": "order_id",
      "label": "订单ID",
      "component": "hidden",
      "col": 0
    }, {
      "item_name": "user_id",
      "label": "用户ID",
      "component": "hidden",
      "col": 0
    }, {
      "item_name": "order_type",
      "label": "订单类型",
      "component": "hidden",
      "col": 0,
      "props": {
        "value": "normal"
      }
    }, {
      "item_name": "order_no",
      "label": "订单号",
      "component": "input",
      "col": 6,
      "props": {
        "placeholder": "请输入订单号",
        "lay-verify": "required"
      }
    }]
  }]
}
```

## 使用技巧

### 从URL获取值

通过表单初始化时从URL参数获取值：

```javascript
// 访问URL: /topezadmin/form/page-order-form?user_id=123&article_id=456

// 表单配置
{
  "initExpress": [
    "map = new HashMap();",
    "map.put('user_id', $('user_id'));",
    "map.put('article_id', $('article_id'));",
    "return map;"
  ]
}
```

### 动态设置值

通过JavaScript动态设置隐藏字段的值：

```javascript
// 在页面脚本中
$('input[name="user_id"]').val('123');
```

### 从Session获取

在表单初始化时从Session获取用户信息：

```javascript
{
  "initExpress": [
    "map = new HashMap();",
    "map.put('create_user', $$('userId'));",
    "map.put('create_time', 'NOW()');",
    "return map;"
  ]
}
```

## 注意事项

1. **列宽度**：建议设置 `col: 0`，虽然字段已隐藏，但这样更规范
2. **字段映射**：`item_name` 必须与数据库字段名一致
3. **默认值**：可通过 `props.value` 设置默认值，也可在 `initExpress` 中动态设置
4. **提交数据**：隐藏字段的值会正常提交到后端，在表达式中可以通过 `#{item_name}` 获取
5. **安全性**：隐藏字段在前端是可见的（通过查看源代码），不要用于传递敏感信息
6. **必填验证**：隐藏字段通常不需要验证，但如果设置了 `lay-verify`，表单提交时仍会验证

## 常见用法

### 新增/编辑判断

```json
{
  "fieldList": [{
    "item_name": "id",
    "component": "hidden",
    "col": 0
  }],
  "submitExpress": [
    "if(!isNotBlank('id')) {",
    "  // 新增逻辑",
    "  return insert('INSERT INTO ...')",
    "} else {",
    "  // 编辑逻辑",
    "  return update('UPDATE ... WHERE id=#{id}')",
    "}"
  ]
}
```

### 级联关联

```json
{
  "fieldList": [{
    "item_name": "province_id",
    "label": "省份",
    "component": "select",
    "col": 4
  }, {
    "item_name": "city_id",
    "label": "城市",
    "component": "select",
    "col": 4
  }, {
    "item_name": "region_id",
    "label": "区域",
    "component": "hidden",
    "col": 0
  }]
}
```

## 相关组件

- [input](./input.md) - 单行文本输入
- [select](./select.md) - 下拉选择
