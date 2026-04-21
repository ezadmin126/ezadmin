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
      {
        "label": "标签1",
        "value": "1"
      },
      {
        "label": "标签2",
        "value": "2"
      },
      {
        "label": "标签3",
        "value": "3"
      }
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
| component   | String | 是  | 固定值：`select-multiple`           |
| classAppend | String | 否  | layui-col-md8 主要使用layui布局设置占用宽度 |
| operator    | String | 否  | 查询操作符，建议使用 `in`                 |
| initData    | Object | 是  | 数据源配置                           |
| props       | Object | 否  | 组件属性                            |

### props 属性 配置

| 参数                | 说明                         | 类型                                                                                                                                              | 可选值              | 默认值   
|-------------------|----------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|------------------|-------
| placeholder       | String                     | 提示文本                                                                                                                                            | `"请选择标签"`        |
| lay-verify        | String                     | Layui表单验证规则                                                                                                                                     | `"required"`     |
| required          | Boolean                    | 是否必填(显示红色*)                                                                                                                                     | `true`           |
| description       | String                     | 字段说明文本                                                                                                                                          | `"可选择多个"`        |
| language          | 语言选择                       | string                                                                                                                                          | zn / en          | zn    
| data              | 显示的数据                      | array                                                                                                                                           | -                | [ ]   
| content           | 自定义下拉框html                 | string                                                                                                                                          | -                | -     
| initValue         | 初始化选中的数据, 需要在data中存在       | array                                                                                                                                           | -                | null  
| tips              | 默认提示, 类似于placeholder       | string                                                                                                                                          | -                | 请选择   
| empty             | 空数据提示                      | string                                                                                                                                          | -                | 暂无数据  
| filterable        | 是否开启搜索                     | boolean                                                                                                                                         | true / false     | false 
| searchTips        | 搜索提示                       | string                                                                                                                                          | -                | 请选择   
| delay             | 搜索延迟 ms                    | int                                                                                                                                             | -                | 500   
| filterMethod      | 搜索回调函数                     | function(val, item, index, prop) val: 当前搜索值, item: 每个option选项, index: 位置数据中的下标, prop: 定义key                                                     | -                | -     
| filterDone        | 搜索完成函数                     | function(val, list) val: 当前搜索值, list: 过滤后的数据                                                                                                    | -                | -     
| remoteSearch      | 是否开启自定义搜索 (远程搜索)           | boolean                                                                                                                                         | true / false     | false 
| remoteMethod      | 自定义搜索回调函数                  | function(val, cb, show, pageIndex) val: 当前搜索值, cb(arr, totalPage): 回调函数, 需要回调一个数组, 结构同data, 远程分页需要第二个参数: 总页码, show: 下拉框显示状态, pageIndex: 分页下当前页码 | -                | -     
| direction         | 下拉方向                       | string                                                                                                                                          | auto / up / down | auto  
| style             | 自定义样式                      | object                                                                                                                                          | -                | { }   
| height            | 默认最大高度                     | string                                                                                                                                          | -                | 200px 
| paging            | 是否开启自定义分页                  | boolean                                                                                                                                         | true / false     | false 
| pageSize          | 分页条数                       | int                                                                                                                                             | -                | 10    
| pageEmptyShow     | 分页无数据是否显示                  | boolean                                                                                                                                         | true / false     | true  
| pageRemote        | 是否开启远程分页                   | boolean                                                                                                                                         | true / false     | true  
| radio             | 是否开启单选模式                   | boolean                                                                                                                                         | true / false     | false 
| repeat            | 是否开启重复性模式                  | boolean                                                                                                                                         | true / false     | false 
| clickClose        | 是否点击选项后自动关闭下拉框             | boolean                                                                                                                                         | true / false     | false 
| prop              | 自定义属性名称, 具体看下表             | object                                                                                                                                          | -                | -     
| theme             | 主题配置, 具体看下表                | object                                                                                                                                          | -                | -     
| model             | 模型, 多选的展示方式, 具体见下表         | object                                                                                                                                          | -                | -     
| iconfont          | 自定义选中图标                    | object                                                                                                                                          | -                | -     
| show	             | 展开下拉的回调 	                  | function	-	-                                                                                                                                    
| hide	             | 隐藏下拉的回调 	                  | function	-	-                                                                                                                                    
| template	         | 自定义渲染选项 	                  | function({ item, sels, name, value })	-	-                                                                                                       
| on	               | 监听选中变化 	                   | function({ arr, change, isAdd })	-	-                                                                                                            
| max	              | 设置多选选中上限 	                 | int	-	0                                                                                                                                         
| maxMethod	        | 达到选中上限的回到 	                | function(sels, item), sels: 已选中数据, item: 当前选中的值	-	-                                                                                             
| name	             | 表单提交时的name 	               | string	-	select                                                                                                                                 
| layVerify	        | 表单验证, 同layui的lay-verify 	  | string	-	''                                                                                                                                     
| layVerType	       | 表单验证, 同layui的lay-verType 	 | string	-	''                                                                                                                                     
| layReqText	       | 表单验证, 同layui的lay-reqText 	 | string	-	''                                                                                                                                     
| toolbar	          | 工具条, 具体看下表 	               | object	-	-                                                                                                                                      
| showCount	        | 展示在下拉框中的最多选项数量 	           | int	-	0                                                                                                                                         
| enableKeyboard	   | 是否启用键盘操作选项 	               | boolean	true / false	true                                                                                                                       
| enableHoverFirst	 | 是否默认选中第一项 	                | boolean	true / false	true                                                                                                                       
| selectedKeyCode	  | 选中的键盘KeyCode 	             | int	全部KeyCode, 也可xmSelect.KeyCode.Enter,xmSelect.KeyCode.Space	13                                                                               
| autoRow	          | 是否开启自动换行(选项过多时) 	          | boolean	true / false	false                                                                                                                      
| size	             | 尺寸 	                       | string	large / medium / small / mini	medium                                                                                                     
| disabled	         | 是否禁用多选	                    | boolean	true / false	false                                                                                                                      
| create	           | 创建条目	                      | function(val, data), val: 搜索的数据, data: 当前下拉数据	-	-                                                                                               

### initData 配置

**静态数据（dataJson）：**

```json
{
  "initData": {
    "dataJson": [
      {
        "label": "选项1",
        "value": "1"
      },
      {
        "label": "选项2",
        "value": "2"
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
    "dataSql": "SELECT id value, name label FROM table_name"
  }
}
```

## 完整示例

### 静态选项 - 标签选择

```json
{
  "item_name": "tags",
  "label": "标签",
  "component": "select-multiple",
  "classAppend": "layui-col-md8",
  "initData": {
    "dataJson": [
      {
        "label": "热门",
        "value": "hot"
      },
      {
        "label": "推荐",
        "value": "recommend"
      },
      {
        "label": "新品",
        "value": "new"
      },
      {
        "label": "促销",
        "value": "promotion"
      }
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
  "classAppend": "layui-col-md8",
  "initData": {
    "dataSource": "dataSource",
    "dataSql": "SELECT id value, name label FROM permissions WHERE status=1 ORDER BY sort"
  },
  "props": {
    "placeholder": "请选择权限",
    "lay-verify": "required",
    "validate": {
      //jquery validate 使用 validate
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

### 搜索项中使用

```json
{
  "search": [
    {
      "item_name": "tag_ids",
      "label": "标签",
      "component": "select-multiple",
      "operator": "in",
      "initData": {
        "dataJson": [
          {
            "label": "热门",
            "value": "hot"
          },
          {
            "label": "推荐",
            "value": "recommend"
          },
          {
            "label": "新品",
            "value": "new"
          }
        ]
      }
    }
  ]
}
```

### 列表展示中使用

在列表的 column 配置中，使用 `tdSelectMultiple` 组件展示多选值：

```json
{
  "column": [
    {
      "item_name": "tags",
      "label": "标签",
      "component": "tdSelectMultiple",
      "initData": {
        "dataJson": [
          {
            "label": "热门",
            "value": "hot"
          },
          {
            "label": "推荐",
            "value": "recommend"
          },
          {
            "label": "新品",
            "value": "new"
          }
        ]
      }
    }
  ]
}
```

### 完整表单示例

```json
{
  "id": "article-form",
  "name": "文章表单",
  "dataSource": "dataSource",
  "cardList": [
    {
      "type": "card",
      "label": "文章信息",
      "classAppend": "layui-col-md8",
      "fieldList": [
        {
          "item_name": "title",
          "label": "标题",
          "component": "input",
          "classAppend": "layui-col-md8",
          "props": {
            "placeholder": "请输入标题",
            "lay-verify": "required"
          }
        },
        {
          "item_name": "category_id",
          "label": "分类",
          "component": "select",
          "classAppend": "layui-col-md8",
          "initData": {
            "dataSource": "dataSource",
            "dataSql": "SELECT id value, name label FROM categories"
          }
        },
        {
          "item_name": "tag_ids",
          "label": "标签",
          "component": "select-multiple",
          "classAppend": "layui-col-md8",
          "initData": {
            "dataSource": "dataSource",
            "dataSql": "SELECT id value, name label FROM tags WHERE status=1"
          },
          "props": {
            "placeholder": "请选择标签"
          }
        },
        {
          "item_name": "content",
          "label": "内容",
          "component": "textarea",
          "classAppend": "layui-col-md8",
          "props": {
            "rows": 5
          }
        }
      ]
    }
  ],
  "initExpress": [
    "return selectOne('SELECT * FROM articles WHERE id=${ID}')"
  ],
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
  "cardList": [
    {
      "type": "card",
      "label": "角色分配",
      "classAppend": "layui-col-md8",
      "fieldList": [
        {
          "item_name": "username",
          "label": "用户名",
          "component": "input",
          "classAppend": "layui-col-md8",
          "props": {
            "readonly": true
          }
        },
        {
          "item_name": "role_ids",
          "label": "角色",
          "component": "select-multiple",
          "classAppend": "layui-col-md8",
          "initData": {
            "dataSource": "dataSource",
            "dataSql": "SELECT id value, role_name label FROM roles WHERE status=1"
          },
          "props": {
            "placeholder": "请选择角色",
            "lay-verify": "required",
            "description": "可选择多个角色"
          }
        }
      ]
    }
  ],
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
    "initExpress"
:
    [
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
WHERE FIND_IN_SET(
#{tag_id},
tag_ids
)
>
0

-- IN 方式（需要拆分字符串）
WHERE
tag_id
IN
(
${tag_ids}
)
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
