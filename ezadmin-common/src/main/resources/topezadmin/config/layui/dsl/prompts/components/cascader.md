# cascader 组件

## 组件说明

级联选择器，基于 lay-cascader 插件实现，用于选择具有层级关系的数据，如省市区、部门组织等。

## 使用场景

- 省市区选择
- 部门/组织选择
- 商品分类选择
- 任何具有层级关系的数据选择

## 基本配置

```json
{
  "item_name": "region_id",
  "label": "地区",
  "component": "cascader",
  "initData": {
    "dataJson": [
      {
        "label": "北京市",
        "value": "110000",
        "children": [
          {"label": "东城区", "value": "110101"},
          {"label": "西城区", "value": "110102"}
        ]
      }
    ]
  }
}
```

## 配置项说明

### 基础属性

| 属性        | 类型 | 必填 | 说明                      |
|-----------|------|----|-------------------------|
| item_name | String | 是  | 字段名，对应数据库字段             |
| label     | String | 是  | 显示标签                    |
| component | String | 是  | 固定值：`cascader`          |
| operator  | String | 否  | 查询操作符                   |
| initData  | Object | 是  | 数据源配置                   |
| props     | Object | 否  | 属性配置，支持lay-cascader所有属性 |
| description | String | 字段说明文本 | `"可选择多个"`                       |
| classAppend | String | 否 | layui-col-md8 主要使用layui布局设置占用宽度                       |

### props详情
| expandTrigger	| 次级菜单的展开方式	| String	| click / hover	| 默认值：'click'
| multiple	| 是否多选	| Boolean	| true / false	| false
| disabled	| 指定选项的禁用为选项对象的某个属性值	| String	| -	| 'disabled'





### initData 配置

**静态数据（dataJson）：**
```json
{
  "initData": {
    "dataJson": [
      {
        "label": "父级1",
        "value": "1",
        "children": [
          {
            "label": "子级1-1",
            "value": "11",
            "children": [
              {"label": "子级1-1-1", "value": "111"}
            ]
          }
        ]
      }
    ]
  }
}
```

**动态数据（dataSql）：**
```json
{
  "initData": {
    "dataSource": "dataSource",
    "dataSql": "SELECT id value, name label, parent_id FROM regions"
  }
}
```

### props 属性

| 属性 | 类型 | 说明 | 示例 |
|------|------|------|------|
| placeholder | String | 提示文本 | `"请选择地区"` |
| lay-verify | String | Layui表单验证规则 | `"required"` |
| required | Boolean | 是否必填(显示红色*) | `true` |
| description | String | 字段说明文本 | `"请选择完整路径"` |

## 完整示例

### 省市区选择

```json
{
  "item_name": "region_ids",
  "label": "所在地区",
  "component": "cascader",
  "classAppend":  "layui-col-md8",
  "initData": {
    "dataJson": [
      {
        "label": "北京市",
        "value": "110000",
        "children": [
          {
            "label": "市辖区",
            "value": "110100",
            "children": [
              {"label": "东城区", "value": "110101"},
              {"label": "西城区", "value": "110102"},
              {"label": "朝阳区", "value": "110105"}
            ]
          }
        ]
      },
      {
        "label": "广东省",
        "value": "440000",
        "children": [
          {
            "label": "广州市",
            "value": "440100",
            "children": [
              {"label": "天河区", "value": "440106"},
              {"label": "海珠区", "value": "440105"}
            ]
          },
          {
            "label": "深圳市",
            "value": "440300",
            "children": [
              {"label": "福田区", "value": "440304"},
              {"label": "南山区", "value": "440305"}
            ]
          }
        ]
      }
    ]
  },
  "props": {
    "placeholder": "请选择省市区",
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

### 部门组织选择

```json
{
  "item_name": "dept_id",
  "label": "部门",
  "component": "cascader",
  "classAppend":  "layui-col-md8",
  "initData": {
    "dataSource": "dataSource",
    "dataSql": "SELECT id value, name label, parent_id FROM departments WHERE status=1 ORDER BY sort"
  },
  "props": {
    "placeholder": "请选择部门",
    "lay-verify": "required"
  }
}
```

### 商品分类选择

```json
{
  "item_name": "category_id",
  "label": "商品分类",
  "component": "cascader",
  "classAppend":  "layui-col-md8",
  "initData": {
    "dataJson": [
      {
        "label": "电子产品",
        "value": "1",
        "children": [
          {
            "label": "手机",
            "value": "11",
            "children": [
              {"label": "智能手机", "value": "111"},
              {"label": "功能手机", "value": "112"}
            ]
          },
          {
            "label": "电脑",
            "value": "12",
            "children": [
              {"label": "笔记本", "value": "121"},
              {"label": "台式机", "value": "122"}
            ]
          }
        ]
      },
      {
        "label": "服装鞋帽",
        "value": "2",
        "children": [
          {
            "label": "男装",
            "value": "21",
            "children": [
              {"label": "衬衫", "value": "211"},
              {"label": "T恤", "value": "212"}
            ]
          }
        ]
      }
    ]
  },
  "props": {
    "placeholder": "请选择分类"
  }
}
```

### 列表展示中使用

在列表的 column 配置中，使用 `tdCascader` 组件展示级联路径：

```json
{
  "column": [{
    "item_name": "region_ids",
    "label": "地区",
    "component": "tdCascader",
    "initData": {
      "dataJson": [
        {
          "label": "北京市",
          "value": "110000",
          "children": [
            {"label": "东城区", "value": "110101"}
          ]
        }
      ]
    }
  }]
}
```

### 完整表单示例

```json
{
  "id": "store-form",
  "name": "门店表单",
  "dataSource": "dataSource",
  "cardList": [{
    "type": "card",
    "label": "门店信息",
    "classAppend":  "layui-col-md12",
    "fieldList": [{
      "item_name": "store_name",
      "label": "门店名称",
      "component": "input",
      "classAppend":  "layui-col-md6",
      "props": {
        "placeholder": "请输入门店名称",
        "lay-verify": "required"
      }
    }, {
      "item_name": "region_ids",
      "label": "所在地区",
      "component": "cascader",
      "classAppend":  "layui-col-md6",
      "initData": {
        "dataSource": "dataSource",
        "dataSql": "SELECT id value, name label, parent_id FROM regions"
      },
      "props": {
        "placeholder": "请选择省市区",
        "lay-verify": "required",
        "description": "请选择到区级"
      }
    }, {
      "item_name": "address",
      "label": "详细地址",
      "component": "input",
      "classAppend":  "layui-col-md12",
      "props": {
        "placeholder": "请输入详细地址",
        "lay-verify": "required"
      }
    }, {
      "item_name": "contact_person",
      "label": "联系人",
      "component": "input",
      "classAppend":  "layui-col-md6",
      "props": {
        "placeholder": "请输入联系人"
      }
    }, {
      "item_name": "contact_phone",
      "label": "联系电话",
      "component": "input",
      "classAppend":  "layui-col-md6",
      "props": {
        "placeholder": "请输入联系电话",
        "lay-verify": "phone"
      }
    }]
  }],
  "initExpress": ["return selectOne('SELECT * FROM stores WHERE id=${ID}')"],
  "submitExpress": [
    "if(!isNotBlank('id')) {",
    "  return insert('INSERT INTO stores(store_name,region_ids,address,contact_person,contact_phone,create_time) VALUES(#{store_name},#{region_ids},#{address},#{contact_person},#{contact_phone},NOW())')",
    "} else {",
    "  return update('UPDATE stores SET store_name=#{store_name},region_ids=#{region_ids},address=#{address},contact_person=#{contact_person},contact_phone=#{contact_phone},update_time=NOW() WHERE id=#{id}')",
    "}"
  ]
}
```

## 数据格式

### 树形结构

数据必须是树形结构，每个节点包含：
- `label`: 显示文本
- `value`: 节点值
- `children`: 子节点数组（可选）

```json
[
  {
    "label": "父节点",
    "value": "1",
    "children": [
      {
        "label": "子节点",
        "value": "11",
        "children": []
      }
    ]
  }
]
```

### 存储格式

选中的路径以逗号分隔的字符串形式存储：
```
"110000,110100,110101"
```
表示：北京市 > 市辖区 > 东城区

### SQL数据转换

如果使用 dataSql 从数据库查询，数据库结构应该包含 parent_id 字段：

```sql
CREATE TABLE regions (
  id INT PRIMARY KEY,
  name VARCHAR(50),
  parent_id INT,
  level INT
);
```

查询SQL：
```sql
SELECT id value, name label, parent_id FROM regions WHERE status=1 ORDER BY sort
```




系统会自动将扁平数据转换为树形结构。

## 注意事项

1. **数据格式**：必须是树形结构，包含 `label`、`value`、`children` 字段
2. **存储格式**：选中的完整路径以逗号分隔存储（如："1,11,111"）
3. **数据库字段**：建议使用 VARCHAR 类型
4. **层级限制**：建议不超过4级，层级过多影响用户体验
5. **插件依赖**：组件依赖 lay-cascader 插件
6. **SQL查询**：dataSql 必须包含 parent_id 字段用于构建树形结构
7. **初始化**：编辑时返回完整路径的逗号分隔字符串

## 常见问题

### 如何只选择最后一级？

lay-cascader 默认只能选择叶子节点（最后一级）。

### 如何获取完整路径文本？

在列表展示时使用 `tdCascader` 组件会自动显示完整路径文本（用逗号分隔）。

### 数据不显示树形结构？

检查：
1. 数据格式是否正确（必须包含 children 字段）
2. SQL查询是否包含 parent_id 字段
3. 数据中是否有循环引用

### 如何实现动态加载？

可以配合后端接口实现懒加载，需要自定义实现。

### 如何使用外部URL作为数据源？

```json
{
          "item_name": "CURRENT_ORG_ID",
          "label": "销售部门",
          "component": "cascader",
          "operator": "IN",
          "alias": "tsd",
          "initData": {
            "dataApi": "/ezadmin/api/org.html?level=10&parentId=2"
          },
          "props": {
            "placeholder": "请选择部门",
            "props": {
              "multiple": true,
              "value": "orgId",
              "label": "orgName",
              "children": "organizations"
            }
          }
        }
```

### 如果一条sql无法查出来所有数据怎么办
服务端将 递归5层 获取子数据 ，最终前端 树形展示
```json
{
 "initData": {
   "dataTreeSqlRoot": "SELECT * FROM menu WHERE parent_id IS NULL OR parent_id = ''",
   "dataTreeSql": "SELECT * FROM menu WHERE parent_id = ${parentId}",
   "dataSource": "datasource",
   "maxDepth": 5
 }
}
```

## 相关组件

- [select](./select.md) - 单选下拉
- [tdCascader](./tdCascader.md) - 级联值展示（列表中使用）
