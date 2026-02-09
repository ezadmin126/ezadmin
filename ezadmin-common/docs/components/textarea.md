# textarea 组件

## 组件说明

多行文本输入框，用于接收用户输入的多行文本内容，支持字数统计功能。

## 使用场景

- 备注、描述等多行文本输入
- 地址信息输入
- 需要输入较长文本的场景
- 需要字数限制的文本输入

## 基本配置

```json
{
  "item_name": "remark",
  "label": "备注",
  "component": "textarea",
  "col": 12,
  "props": {
    "placeholder": "请输入备注信息",
    "rows": 4
  }
}
```

## 配置项说明

### 基础属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| item_name | String | 是 | 字段名，对应数据库字段 |
| label | String | 是 | 显示标签 |
| component | String | 是 | 固定值：`textarea` |
| col | Number | 否 | 列宽度(0-12)，默认12 |
| operator | String | 否 | 查询操作符(搜索项使用)，如 `LIKE` |

### props 属性

| 属性 | 类型 | 说明 | 示例 |
|------|------|------|------|
| placeholder | String | 输入提示文本 | `"请输入备注"` |
| rows | Number | 显示行数 | `4` |
| maxlength | Number | 最大字数限制(会显示字数统计) | `500` |
| lay-verify | String | Layui表单验证规则 | `"required"` |
| disabled | Boolean | 是否禁用 | `true` |
| readonly | Boolean | 是否只读 | `true` |
| required | Boolean | 是否必填(显示红色*) | `true` |
| description | String | 字段说明文本 | `"请详细描述问题"` |

## 完整示例

### 基础使用

```json
{
  "item_name": "description",
  "label": "产品描述",
  "component": "textarea",
  "col": 12,
  "props": {
    "placeholder": "请输入产品描述",
    "rows": 5,
    "lay-verify": "required"
  }
}
```

### 带字数限制

```json
{
  "item_name": "comment",
  "label": "评论内容",
  "component": "textarea",
  "col": 12,
  "props": {
    "placeholder": "请输入评论内容",
    "rows": 6,
    "maxlength": 500,
    "description": "评论内容不超过500字"
  }
}
```

### 只读展示

```json
{
  "item_name": "system_log",
  "label": "系统日志",
  "component": "textarea",
  "col": 12,
  "props": {
    "readonly": true,
    "rows": 10
  }
}
```

### 完整表单示例

```json
{
  "id": "product-form",
  "name": "产品表单",
  "dataSource": "dataSource",
  "cardList": [{
    "type": "card",
    "label": "产品信息",
    "col": 12,
    "fieldList": [{
      "item_name": "product_name",
      "label": "产品名称",
      "component": "input",
      "col": 6,
      "props": {
        "placeholder": "请输入产品名称",
        "lay-verify": "required"
      }
    }, {
      "item_name": "address",
      "label": "地址",
      "component": "textarea",
      "col": 12,
      "props": {
        "placeholder": "请输入详细地址",
        "rows": 3,
        "lay-verify": "required",
        "maxlength": 200
      }
    }, {
      "item_name": "description",
      "label": "产品描述",
      "component": "textarea",
      "col": 12,
      "props": {
        "placeholder": "请输入产品描述",
        "rows": 5,
        "maxlength": 1000,
        "description": "详细描述产品的特点和优势"
      }
    }, {
      "item_name": "remark",
      "label": "备注",
      "component": "textarea",
      "col": 12,
      "props": {
        "placeholder": "请输入备注信息",
        "rows": 4
      }
    }]
  }],
  "initExpress": ["return selectOne('SELECT * FROM products WHERE id=${ID}')"],
  "submitExpress": [
    "if(!isNotBlank('id')) {",
    "  return insert('INSERT INTO products(product_name,address,description,remark) VALUES(#{product_name},#{address},#{description},#{remark})')",
    "} else {",
    "  return update('UPDATE products SET product_name=#{product_name},address=#{address},description=#{description},remark=#{remark} WHERE id=#{id}')",
    "}"
  ]
}
```

## 特性说明

### 字数统计

当配置 `maxlength` 属性时，输入框右下角会自动显示字数统计徽章：
- 显示格式：`当前字数/最大字数`
- 样式：圆角徽章，位于右下角
- 实时更新：输入时自动更新统计

示例效果：
```
┌─────────────────────────────────┐
│ 这是输入的内容...               │
│                                 │
│                        [15/500] │
└─────────────────────────────────┘
```

## 注意事项

1. **行数配置**：通过 `rows` 属性设置初始显示行数，用户可以拖动调整高度
2. **字数限制**：设置 `maxlength` 后会启用字数统计功能，并限制输入字数
3. **字段映射**：`item_name` 必须与数据库字段名一致
4. **必填标识**：设置 `props.required: true` 会在标签前显示红色星号
5. **列宽度**：建议设置 `col: 12` 以获得更好的输入体验
6. **换行符**：文本域内容会保留换行符，存储到数据库时需要注意字段类型（建议使用TEXT类型）

## 常见问题

### 如何设置自动高度？

Layui的textarea会根据 `rows` 属性设置初始高度，用户可以手动拖动调整。如需自动扩展高度，可以通过自定义CSS实现。

### 如何限制只能输入特定内容？

可以配合正则验证：
```json
{
  "props": {
    "lay-verify": "required",
    "lay-verType": "tips"
  }
}
```

### 字数统计支持中英文吗？

字数统计按字符数计算，中英文均算一个字符。

## 相关组件

- [input](./input.md) - 单行文本输入
- [tinymce](./tinymce.md) - 富文本编辑器（支持格式化文本）
- [hidden](./hidden.md) - 隐藏字段
