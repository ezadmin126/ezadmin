# select-multiple 组件

## 组件说明

多选下拉框，基于 xm-select 插件实现，用于从预定义选项中选择多个值。

## 使用场景

- 标签选择
- 权限分配
- 兴趣爱好选择
- 需要多选的任何场景

## 基本配置

```json
{
  "item_name": "tags",
  "label": "标签",
  "component": "select-multiple",
  "col": 6,
  "initData": {
    "dataJson": [
      {"label": "标签1", "value": "1"},
      {"label": "标签2", "value": "2"},
      {"label": "标签3", "value": "3"}
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
| component | String | 是 | 固定值：`select-multiple` |
| col | Number | 否 | 列宽度(0-12)，默认12 |
| operator | String | 否 | 查询操作符，建议使用 `in` |
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
| placeholder | String | 提示文本 | `"请选择标签"` |
| lay-verify | String | Layui表单验证规则 | `"required"` |
| required | Boolean | 是否必填(显示红色*) | `true` |
| description | String | 字段说明文本 | `"可选择多个"` |

## 完整示例

### 静态选项 - 标签选择

```json
{
  "item_name": "tags",
  "label": "标签",
  "component": "select-multiple",
  "col": 6,
  "initData": {
    "dataJson": [
      {"label": "热门", "value": "hot"},
      {"label": "推荐", "value": "recommend"},
      {"label": "新品", "value": "new"},
      {"label": "促销", "value": "promotion"}
    ]
  },
  "props": {
    "placeholder": "请选择标签"
  }
}
```

### 动态选项 - 权限分配

```json
{
  "item_name": "permission_ids",
  "label": "权限",
  "component": "select-multiple",
  "col": 12,
  "initData": {
    "dataSource": "dataSource",
    "dataSql": "SELECT id value, name label FROM permissions WHERE status=1 ORDER BY sort"
  },
  "props": {
    "placeholder": "请选择权限",
    "lay-verify": "required"
  }
}
```

### 搜索项中使用

```json
{
  "search": [{
    "item_name": "tag_ids",
    "label": "标签",
    "component": "select-multiple",
    "operator": "in",
    "initData": {
      "dataJson": [
        {"label": "热门", "value": "hot"},
        {"label": "推荐", "value": "recommend"},
        {"label": "新品", "value": "new"}
      ]
    }
  }]
}
```

### 列表展示中使用

在列表的 column 配置中，使用 `tdSelectMultiple` 组件展示多选值：

```json
{
  "column": [{
    "item_name": "tags",
    "label": "标签",
    "component": "tdSelectMultiple",
    "initData": {
      "dataJson": [
        {"label": "热门", "value": "hot"},
        {"label": "推荐", "value": "recommend"},
        {"label": "新品", "value": "new"}
      ]
    }
  }]
}
```

### 完整表单示例

```json
{
  "id": "article-form",
  "name": "文章表单",
  "dataSource": "dataSource",
  "cardList": [{
    "type": "card",
    "label": "文章信息",
    "col": 12,
    "fieldList": [{
      "item_name": "title",
      "label": "标题",
      "component": "input",
      "col": 12,
      "props": {
        "placeholder": "请输入标题",
        "lay-verify": "required"
      }
    }, {
      "item_name": "category_id",
      "label": "分类",
      "component": "select",
      "col": 6,
      "initData": {
        "dataSource": "dataSource",
        "dataSql": "SELECT id value, name label FROM categories"
      }
    }, {
      "item_name": "tag_ids",
      "label": "标签",
      "component": "select-multiple",
      "col": 6,
      "initData": {
        "dataSource": "dataSource",
        "dataSql": "SELECT id value, name label FROM tags WHERE status=1"
      },
      "props": {
        "placeholder": "请选择标签"
      }
    }, {
      "item_name": "content",
      "label": "内容",
      "component": "textarea",
      "col": 12,
      "props": {
        "rows": 5
      }
    }]
  }],
  "initExpress": ["return selectOne('SELECT * FROM articles WHERE id=${ID}')"],
  "submitExpress": [
    "if(!isNotBlank('id')) {",
    "  return insert('INSERT INTO articles(title,category_id,tag_ids,content,create_time) VALUES(#{title},#{category_id},#{tag_ids},#{content},NOW())')",
    "} else {",
    "  return update('UPDATE articles SET title=#{title},category_id=#{category_id},tag_ids=#{tag_ids},content=#{content},update_time=NOW() WHERE id=#{id}')",
    "}"
  ]
}
```

### 用户角色分配

```json
{
  "id": "user-role-form",
  "name": "用户角色",
  "dataSource": "dataSource",
  "cardList": [{
    "type": "card",
    "label": "角色分配",
    "col": 12,
    "fieldList": [{
      "item_name": "username",
      "label": "用户名",
      "component": "input",
      "col": 6,
      "props": {
        "readonly": true
      }
    }, {
      "item_name": "role_ids",
      "label": "角色",
      "component": "select-multiple",
      "col": 12,
      "initData": {
        "dataSource": "dataSource",
        "dataSql": "SELECT id value, role_name label FROM roles WHERE status=1"
      },
      "props": {
        "placeholder": "请选择角色",
        "lay-verify": "required",
        "description": "可选择多个角色"
      }
    }]
  }],
  "initExpress": [
    "map = selectOne('SELECT * FROM users WHERE id=${ID}');",
    "// 查询用户已有的角色",
    "roles = select('SELECT role_id FROM user_roles WHERE user_id=${ID}');",
    "roleIds = [];",
    "for(role : roles) {",
    "  roleIds.add(role.get('role_id'));",
    "}",
    "map.put('role_ids', String.join(',', roleIds));",
    "return map;"
  ],
  "submitExpress": [
    "// 先删除原有角色",
    "update('DELETE FROM user_roles WHERE user_id=#{id}');",
    "// 再插入新角色",
    "if(isNotBlank('role_ids')) {",
    "  roleIds = $('role_ids').split(',');",
    "  for(roleId : roleIds) {",
    "    insert('INSERT INTO user_roles(user_id,role_id) VALUES(#{id},' + roleId + ')');",
    "  }",
    "}",
    "return true;"
  ]
}
```

## 数据格式

### 存储格式

多选值以逗号分隔的字符串形式存储：
```
"1,2,3"
```

### 查询使用

在搜索条件中，通常使用 `IN` 操作符：

```json
{
  "operator": "in"
}
```

对应的SQL：
```sql
WHERE tag_ids IN (1,2,3)
```

或使用 FIND_IN_SET：
```sql
WHERE FIND_IN_SET('1', tag_ids) > 0
```

## 注意事项

1. **数据格式**：选项数据必须包含 `label` 和 `value` 字段
2. **存储格式**：选中的值以逗号分隔的字符串形式存储（如："1,2,3"）
3. **数据库字段**：建议使用 VARCHAR 类型，长度根据实际需要设置
4. **查询操作符**：搜索项中建议使用 `operator: "in"`
5. **SQL查询**：dataSql中必须使用别名 `value` 和 `label`
6. **初始化**：编辑时需要返回逗号分隔的字符串
7. **插件依赖**：组件依赖 xm-select 插件

## 常见问题

### 如何设置默认选中？

在 initExpress 中返回逗号分隔的字符串：
```javascript
{
  "initExpress": [
    "map = selectOne('SELECT * FROM table WHERE id=${ID}');",
    "// tag_ids 字段格式：'1,2,3'",
    "return map;"
  ]
}
```

### 如何在查询中使用？

使用 FIND_IN_SET 或 IN 操作符：
```sql
-- FIND_IN_SET 方式
WHERE FIND_IN_SET(#{tag_id}, tag_ids) > 0

-- IN 方式（需要拆分字符串）
WHERE tag_id IN (${tag_ids})
```

### 选项不显示？

检查：
1. SQL查询是否正确
2. 是否使用了 `value` 和 `label` 别名
3. xm-select 插件是否正确加载

### 如何实现全选/取消全选？

xm-select 插件默认支持全选功能。

## 相关组件

- [select](./select.md) - 单选下拉
- [checkbox](./checkbox.md) - 复选框
- [tdSelectMultiple](./tdSelectMultiple.md) - 多选值展示（列表中使用）
