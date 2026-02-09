# 表格展示组件说明

表格展示组件（td开头）专门用于列表的 column 配置中，用于格式化展示数据。这些组件只负责数据展示，不涉及数据输入。

## 组件类型

| 组件 | 说明 | 适用场景 |
|------|------|---------|
| [tdText](#tdtext) | 文本展示 | 普通文本字段 |
| [tdPic](#tdpic) | 图片展示 | 图片字段 |
| [tdLink](#tdlink) | 链接展示 | 可点击的链接 |
| [tdSelect](#tdselect) | 下拉值展示 | 将value转为label显示 |
| [tdSelectMultiple](#tdselectmultiple) | 多选值展示 | 将多个value转为label显示 |
| [tdCascader](#tdcascader) | 级联值展示 | 将级联path转为label显示 |

## 配置位置

所有表格展示组件都配置在列表的 column 中：

```json
{
  "column": [
    {
      "item_name": "字段名",
      "label": "列标题",
      "component": "td组件类型",
      "initData": {},
      "props": {}
    }
  ]
}
```

## tdText

### 说明
最基本的文本展示组件，直接显示字段值。

### 基本配置

```json
{
  "item_name": "name",
  "label": "姓名",
  "component": "tdText"
}
```

### props 属性

| 属性 | 类型 | 说明 | 示例 |
|------|------|------|------|
| emptyShow | String | 空值显示文本 | `"-"` |

### 完整示例

```json
{
  "column": [{
    "item_name": "id",
    "label": "ID",
    "component": "tdText"
  }, {
    "item_name": "username",
    "label": "用户名",
    "component": "tdText"
  }, {
    "item_name": "email",
    "label": "邮箱",
    "component": "tdText",
    "props": {
      "emptyShow": "未填写"
    }
  }]
}
```

## tdPic

### 说明
图片展示组件，显示图片缩略图，支持点击查看大图。

### 基本配置

```json
{
  "item_name": "avatar",
  "label": "头像",
  "component": "tdPic"
}
```

### props 属性

| 属性 | 类型 | 说明 | 示例 |
|------|------|------|------|
| emptyShow | String | 空值显示文本 | `"-"` |

### 特性

- 自动添加下载URL前缀
- 支持点击查看大图
- 图片加载失败显示默认错误图标
- 自动显示缩略图

### 完整示例

```json
{
  "column": [{
    "item_name": "avatar",
    "label": "头像",
    "component": "tdPic"
  }, {
    "item_name": "cover_image",
    "label": "封面图",
    "component": "tdPic",
    "props": {
      "emptyShow": "无图片"
    }
  }]
}
```

## tdLink

### 说明
链接展示组件，将字段值显示为可点击的链接。

### 基本配置

```json
{
  "item_name": "file_name",
  "label": "附件",
  "component": "tdLink",
  "props": {
    "url": "{{=downloadUrl}}{{=d.file_path}}"
  }
}
```

### props 属性

| 属性 | 类型 | 必填 | 说明 | 示例 |
|------|------|------|------|------|
| url | String | 是 | 链接地址(支持laytpl) | `"{{=d.url}}"` |
| emptyShow | String | 否 | 空值显示文本 | `"-"` |

### 完整示例

```json
{
  "column": [{
    "item_name": "attachment_name",
    "label": "附件",
    "component": "tdLink",
    "props": {
      "url": "{{=downloadUrl}}{{=d.attachment_path}}"
    }
  }, {
    "item_name": "document_name",
    "label": "文档",
    "component": "tdLink",
    "props": {
      "url": "/download?id={{=d.id}}",
      "emptyShow": "无文档"
    }
  }]
}
```

## tdSelect

### 说明
下拉值展示组件，将存储的 value 值转换为对应的 label 文本显示。

### 基本配置

```json
{
  "item_name": "status",
  "label": "状态",
  "component": "tdSelect",
  "initData": {
    "dataJson": [
      {"label": "启用", "value": "1"},
      {"label": "禁用", "value": "0"}
    ]
  }
}
```

### initData 配置

与 select 组件相同，支持 dataJson 和 dataSql：

**静态数据：**
```json
{
  "initData": {
    "dataJson": [
      {"label": "选项1", "value": "1"}
    ]
  }
}
```

**动态数据：**
```json
{
  "initData": {
    "dataSource": "dataSource",
    "dataSql": "SELECT id value, name label FROM table"
  }
}
```

### props 属性

| 属性 | 类型 | 说明 | 示例 |
|------|------|------|------|
| emptyShow | String | 空值显示文本 | `"-"` |

### 完整示例

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
  }, {
    "item_name": "gender",
    "label": "性别",
    "component": "tdSelect",
    "initData": {
      "dataJson": [
        {"label": "男", "value": "1"},
        {"label": "女", "value": "2"}
      ]
    },
    "props": {
      "emptyShow": "未填写"
    }
  }, {
    "item_name": "category_id",
    "label": "分类",
    "component": "tdSelect",
    "initData": {
      "dataSource": "dataSource",
      "dataSql": "SELECT id value, name label FROM categories"
    }
  }]
}
```

## tdSelectMultiple

### 说明
多选值展示组件，将存储的多个 value 值转换为对应的 label 文本并用逗号分隔显示。

### 基本配置

```json
{
  "item_name": "tags",
  "label": "标签",
  "component": "tdSelectMultiple",
  "initData": {
    "dataJson": [
      {"label": "热门", "value": "1"},
      {"label": "推荐", "value": "2"}
    ]
  }
}
```

### 数据格式

字段值格式为逗号分隔的字符串：
```
"1,2,3"
```

显示结果：
```
"热门,推荐,新品"
```

### 完整示例

```json
{
  "column": [{
    "item_name": "hobbies",
    "label": "兴趣",
    "component": "tdSelectMultiple",
    "initData": {
      "dataJson": [
        {"label": "读书", "value": "1"},
        {"label": "运动", "value": "2"},
        {"label": "旅游", "value": "3"}
      ]
    }
  }, {
    "item_name": "permission_ids",
    "label": "权限",
    "component": "tdSelectMultiple",
    "initData": {
      "dataSource": "dataSource",
      "dataSql": "SELECT id value, name label FROM permissions"
    },
    "props": {
      "emptyShow": "无权限"
    }
  }]
}
```

## tdCascader

### 说明
级联值展示组件，将存储的级联路径转换为对应的 label 文本并用逗号分隔显示。

### 基本配置

```json
{
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
}
```

### 数据格式

字段值格式为逗号分隔的路径：
```
"110000,110100,110101"
```

显示结果：
```
"北京市,市辖区,东城区"
```

### 完整示例

```json
{
  "column": [{
    "item_name": "region_ids",
    "label": "所在地区",
    "component": "tdCascader",
    "initData": {
      "dataSource": "dataSource",
      "dataSql": "SELECT id value, name label, parent_id FROM regions"
    }
  }, {
    "item_name": "dept_id",
    "label": "部门",
    "component": "tdCascader",
    "initData": {
      "dataJson": [
        {
          "label": "技术部",
          "value": "1",
          "children": [
            {"label": "前端组", "value": "11"},
            {"label": "后端组", "value": "12"}
          ]
        }
      ]
    },
    "props": {
      "emptyShow": "未分配"
    }
  }]
}
```

## 注意事项

1. **只用于展示**: 这些组件只能用在列表的 column 配置中，不能用于表单的 fieldList

2. **数据源配置**: tdSelect、tdSelectMultiple、tdCascader 需要配置 initData，且数据源必须与对应的输入组件一致

3. **空值处理**: 所有组件都支持 `props.emptyShow` 配置空值显示文本

4. **value匹配**:
   - tdSelect: 将单个value转为label
   - tdSelectMultiple: 将多个value（逗号分隔）转为label
   - tdCascader: 将路径value（逗号分隔）转为label

5. **图片路径**: tdPic 会自动添加 downloadUrl 前缀

6. **链接地址**: tdLink 的 url 属性支持 laytpl 模板语法

## 完整列表示例

```json
{
  "id": "user-list",
  "name": "用户列表",
  "dataSource": "dataSource",
  "column": [{
    "item_name": "id",
    "label": "ID",
    "component": "tdText"
  }, {
    "item_name": "avatar",
    "label": "头像",
    "component": "tdPic"
  }, {
    "item_name": "username",
    "label": "用户名",
    "component": "tdText"
  }, {
    "item_name": "gender",
    "label": "性别",
    "component": "tdSelect",
    "initData": {
      "dataJson": [
        {"label": "男", "value": "1"},
        {"label": "女", "value": "2"}
      ]
    }
  }, {
    "item_name": "hobbies",
    "label": "兴趣",
    "component": "tdSelectMultiple",
    "initData": {
      "dataJson": [
        {"label": "读书", "value": "1"},
        {"label": "运动", "value": "2"}
      ]
    }
  }, {
    "item_name": "region_ids",
    "label": "地区",
    "component": "tdCascader",
    "initData": {
      "dataSource": "dataSource",
      "dataSql": "SELECT id value, name label, parent_id FROM regions"
    }
  }, {
    "item_name": "status",
    "label": "状态",
    "component": "tdSelect",
    "initData": {
      "dataJson": [
        {"label": "启用", "value": "1"},
        {"label": "禁用", "value": "0"}
      ]
    }
  }],
  "express": {
    "main": "return search('SELECT * FROM users WHERE 1=1')"
  }
}
```

## 相关组件

表单输入组件对应关系：

| 表格展示组件 | 表单输入组件 | 说明 |
|-------------|-------------|------|
| tdText | input / textarea | 文本输入 |
| tdPic | upload | 图片上传 |
| tdLink | upload | 文件上传 |
| tdSelect | select | 单选下拉 |
| tdSelectMultiple | select-multiple / checkbox | 多选 |
| tdCascader | cascader | 级联选择 |
