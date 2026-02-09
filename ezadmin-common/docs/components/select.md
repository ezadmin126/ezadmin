# select 组件

## 组件说明

下拉选择框，用于从预定义的选项中选择一个值，支持搜索功能。

## 使用场景

- 状态选择（启用/禁用）
- 分类选择
- 地区选择
- 任何单选场景

## 基本配置

```json
{
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
}
```

## 配置项说明

### 基础属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| item_name | String | 是 | 字段名，对应数据库字段 |
| label | String | 是 | 显示标签 |
| component | String | 是 | 固定值：`select` |
| col | Number | 否 | 列宽度(0-12)，默认12 |
| operator | String | 否 | 查询操作符(搜索项使用)，如 `EQ`、`IN` |
| initData | Object | 是 | 数据源配置 |

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

| 属性 | 类型 | 说明 | 示例 |
|------|------|------|------|
| placeholder | String | 提示文本 | `"请选择状态"` |
| lay-verify | String | Layui表单验证规则 | `"required"` |
| disabled | Boolean | 是否禁用 | `true` |
| required | Boolean | 是否必填(显示红色*) | `true` |
| description | String | 字段说明文本 | `"选择后不可修改"` |
| lay-search | String | 是否启用搜索（默认已启用） | `""` |

## 完整示例

### 静态选项 - 状态选择

```json
{
  "item_name": "status",
  "label": "状态",
  "component": "select",
  "col": 6,
  "initData": {
    "dataJson": [
      {"label": "启用", "value": "1"},
      {"label": "禁用", "value": "0"}
    ]
  },
  "props": {
    "lay-verify": "required",
    "placeholder": "请选择状态"
  }
}
```

### 动态选项 - 分类选择

```json
{
  "item_name": "category_id",
  "label": "分类",
  "component": "select",
  "col": 6,
  "initData": {
    "dataSource": "dataSource",
    "dataSql": "SELECT id value, name label FROM categories WHERE status=1 ORDER BY sort"
  },
  "props": {
    "lay-verify": "required",
    "placeholder": "请选择分类"
  }
}
```

### 搜索项中使用

```json
{
  "search": [{
    "item_name": "status",
    "label": "状态",
    "component": "select",
    "operator": "eq",
    "initData": {
      "dataJson": [
        {"label": "全部", "value": ""},
        {"label": "启用", "value": "1"},
        {"label": "禁用", "value": "0"}
      ]
    }
  }, {
    "item_name": "category_id",
    "label": "分类",
    "component": "select",
    "operator": "eq",
    "initData": {
      "dataSource": "dataSource",
      "dataSql": "SELECT id value, name label FROM categories"
    }
  }]
}
```

### 列表展示中使用

在列表的 column 配置中，使用 `tdSelect` 组件展示下拉值的文本：

```json
{
  "column": [{
    "item_name": "status",
    "label": "状态",
    "component": "tdSelect",
    "initData": {
      "dataJson": [
        {"label": "启用", "value": "1"},
        {"label": "禁用", "value": "0"}
      ]
    }
  }]
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
      "item_name": "category_id",
      "label": "分类",
      "component": "select",
      "col": 6,
      "initData": {
        "dataSource": "dataSource",
        "dataSql": "SELECT id value, name label FROM product_categories WHERE status=1"
      },
      "props": {
        "lay-verify": "required",
        "placeholder": "请选择分类"
      }
    }, {
      "item_name": "brand_id",
      "label": "品牌",
      "component": "select",
      "col": 6,
      "initData": {
        "dataSource": "dataSource",
        "dataSql": "SELECT id value, name label FROM brands WHERE status=1"
      },
      "props": {
        "placeholder": "请选择品牌"
      }
    }, {
      "item_name": "status",
      "label": "状态",
      "component": "select",
      "col": 6,
      "initData": {
        "dataJson": [
          {"label": "上架", "value": "1"},
          {"label": "下架", "value": "0"}
        ]
      },
      "props": {
        "lay-verify": "required"
      }
    }]
  }],
  "initExpress": ["return selectOne('SELECT * FROM products WHERE id=${ID}')"],
  "submitExpress": [
    "if(!isNotBlank('id')) {",
    "  return insert('INSERT INTO products(product_name,category_id,brand_id,status) VALUES(#{product_name},#{category_id},#{brand_id},#{status})')",
    "} else {",
    "  return update('UPDATE products SET product_name=#{product_name},category_id=#{category_id},brand_id=#{brand_id},status=#{status} WHERE id=#{id}')",
    "}"
  ]
}
```

## 高级用法

### 级联选择

通过监听选择事件实现级联：

```javascript
// 在表单页面中添加自定义脚本
form.on('select(lay-filter-province_id)', function(data){
  var provinceId = data.value;
  // 重新加载城市数据
  $.get('/api/cities?province_id=' + provinceId, function(result){
    // 更新城市下拉框
  });
});
```

### 动态加载

根据其他字段的值动态加载选项：

```json
{
  "initData": {
    "dataSource": "dataSource",
    "dataSql": "SELECT id value, name label FROM cities WHERE province_id=${province_id}"
  }
}
```

### 带默认值

在 initExpress 中设置默认值：

```javascript
{
  "initExpress": [
    "map = new HashMap();",
    "map.put('status', '1');",
    "return map;"
  ]
}
```

## 注意事项

1. **数据格式**：选项数据必须包含 `label` 和 `value` 字段
2. **SQL查询**：dataSql中必须使用别名 `value` 和 `label`
3. **搜索功能**：默认启用搜索功能（lay-search属性）
4. **空选项**：系统会自动添加"请选择"空选项
5. **字段映射**：`item_name` 存储的是 `value` 值
6. **必填验证**：使用 `lay-verify: "required"` 进行必填验证
7. **数据源**：dataSql必须指定 `dataSource`

## 常见问题

### 如何设置默认选中？

通过 initExpress 设置字段的值：
```javascript
{
  "initExpress": [
    "return selectOne('SELECT * FROM users WHERE id=${ID}')"
  ]
}
```

### 选项不显示？

检查：
1. SQL查询是否正确，是否有数据返回
2. 是否使用了 `value` 和 `label` 别名
3. dataSource 是否配置正确

### 如何实现联动？

使用 Layui 的表单监听：
```javascript
form.on('select(lay-filter-item_name)', function(data){
  // 处理联动逻辑
});
```

### 搜索不生效？

select 组件默认已启用搜索功能，如果不生效，检查 Layui 版本是否支持。

## 相关组件

- [select-multiple](./select-multiple.md) - 多选下拉
- [cascader](./cascader.md) - 级联选择
- [radio](./radio.md) - 单选框
- [tdSelect](./tdSelect.md) - 下拉值展示（列表中使用）
- [select-span](./select-span.md) - 下拉值只读展示（表单中使用）
