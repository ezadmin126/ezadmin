# tinymce 组件

## 组件说明

富文本编辑器组件，基于 TinyMCE 实现，支持图片上传、文件上传、文本格式化等功能，适用于需要编辑富文本内容的场景。

## 使用场景

- 文章内容编辑
- 产品详情编辑
- 公告、新闻内容编辑
- 需要富文本格式的任何内容编辑

## 基本配置

```json
{
  "item_name": "content",
  "label": "文章内容",
  "component": "tinymce",
  "col": 12
}
```

## 配置项说明

### 基础属性

| 属性          | 类型     | 必填     | 说明                              |
|-------------|--------|--------|---------------------------------|
| item_name   | String | 是      | 字段名，对应数据库字段                     |
| label       | String | 否      | 显示标签（tinymce通常不显示标签）            |
| component   | String | 是      | 固定值：`tinymce`                   |
| classAppend | String | 否      | layui-col-md8 主要使用layui布局设置占用宽度 |
| description | String | 字段说明文本 | `"可选择多个"`                       |
| props       | Object | 否      | 组件属性   主要可以配置 验证信息              |

### props 属性

| 属性          | 类型     | 说明     | 示例            |
|-------------|--------|--------|---------------|
| description | String | 字段说明文本 | `"请输入文章正文内容"` |

## 内置功能

### 工具栏功能

TinyMCE 内置了丰富的编辑功能：

| 功能                               | 说明           |
|----------------------------------|--------------|
| undo/redo                        | 撤销/重做        |
| styleselect                      | 样式选择（标题、段落等） |
| bold                             | 粗体           |
| italic                           | 斜体           |
| bullist                          | 无序列表         |
| numlist                          | 有序列表         |
| outdent/indent                   | 减少/增加缩进      |
| alignleft/aligncenter/alignright | 左对齐/居中/右对齐   |
| table                            | 表格插入         |
| hr                               | 水平线          |
| link                             | 超链接          |
| image                            | 图片插入         |
| batchUploadImage                 | 批量上传图片       |
| fullscreen                       | 全屏编辑         |

### 插件支持

内置以下插件：

- `link` - 超链接
- `image` - 图片
- `media` - 音视频
- `lists` - 列表
- `advlist` - 高级列表
- `table` - 表格
- `hr` - 水平线
- `fullscreen` - 全屏
- `insertdatetime` - 插入日期时间
- `searchreplace` - 查找替换
- `batchUploadImage` - 批量图片上传

### 文件上传

支持以下文件类型：

**图片文件**：

- `.bmp`
- `.jpg`
- `.jpeg`
- `.png`
- `.gif`
- `.pdf`

**音视频文件**：

- `.mp3`
- `.mp4`

**其他文件**：

- `.pdf`
- `.txt`
- `.zip`, `.rar`, `.7z`
- `.doc`, `.docx`
- `.xls`, `.xlsx`
- `.ppt`, `.pptx`

## 完整示例

### 文章编辑

```json
{
  "id": "article-form",
  "name": "文章编辑",
  "dataSource": "dataSource",
  "cardList": [{
    "type": "card",
    "label": "文章信息",
    "classAppend":"layui-col-md8",
    "fieldList": [{
      "item_name": "id",
      "label": "ID",
      "component": "hidden",
      "classAppend":"layui-col-md0" 
    }, {
      "item_name": "title",
      "label": "文章标题",
      "component": "input",
      "classAppend":"layui-col-md12" 
      "props": {
        "placeholder": "请输入文章标题",
        "lay-verify": "required"
      }
    }, {
      "item_name": "category_id",
      "label": "分类",
      "component": "select",
      "classAppend":"layui-col-md6" 
      "initData": {
        "dataSource": "dataSource",
        "dataSql": "SELECT id value, name label FROM article_category"
      },
      "props": {
        "lay-verify": "required"
      }
    }, {
      "item_name": "author",
      "label": "作者",
      "component": "input",
      "classAppend":"layui-col-md6" 
      "props": {
        "placeholder": "请输入作者"
      }
    }, {
      "item_name": "summary",
      "label": "摘要",
      "component": "textarea",
      "classAppend":"layui-col-md12" 
      "props": {
        "placeholder": "请输入文章摘要",
        "rows": 3,
        "maxlength": 200
      }
    }, {
      "item_name": "content",
      "label": "文章内容",
      "component": "tinymce",
      "classAppend":"layui-col-md12" 
      "props": {
        "description": "请输入文章正文内容，支持图片、表格等富文本格式"
      }
    }]
  }],
  "initExpress": ["return selectOne('SELECT * FROM articles WHERE id=${ID}')"],
  "submitExpress": [
    "if(!isNotBlank('id')) {",
    "  return insert('INSERT INTO articles(title,category_id,author,summary,content,create_time) VALUES(#{title},#{category_id},#{author},#{summary},#{content},NOW())')",
    "} else {",
    "  return update('UPDATE articles SET title=#{title},category_id=#{category_id},author=#{author},summary=#{summary},content=#{content},update_time=NOW() WHERE id=#{id}')",
    "}"
  ]
}
```

### 产品详情编辑

```json
{
  "id": "product-form",
  "name": "产品表单",
  "dataSource": "dataSource",
  "cardList": [{
    "type": "card",
    "label": "基本信息",
    "classAppend":"layui-col-md12",
    "fieldList": [{
      "item_name": "product_name",
      "label": "产品名称",
      "component": "input",
      "classAppend":"layui-col-md6" 
      "props": {
        "placeholder": "请输入产品名称",
        "lay-verify": "required"
      }
    }, {
      "item_name": "price",
      "label": "价格",
      "component": "input",
      "classAppend":"layui-col-md6" 
      "props": {
        "placeholder": "请输入价格",
        "lay-verify": "number"
      }
    }]
  }, {
    "type": "card",
    "label": "详细信息",
    "classAppend":"layui-col-md12",
    "fieldList": [{
      "item_name": "detail",
      "component": "tinymce",
      "classAppend":"layui-col-md12"
    }]
  }],
  "initExpress": ["return selectOne('SELECT * FROM products WHERE id=${ID}')"],
  "submitExpress": [
    "if(!isNotBlank('id')) {",
    "  return insert('INSERT INTO products(product_name,price,detail) VALUES(#{product_name},#{price},#{detail})')",
    "} else {",
    "  return update('UPDATE products SET product_name=#{product_name},price=#{price},detail=#{detail} WHERE id=#{id}')",
    "}"
  ]
}
```

### 公告编辑

```json
{
  "fieldList": [{
    "item_name": "notice_title",
    "label": "公告标题",
    "component": "input",
    "classAppend":"layui-col-md12",
    "props": {
      "placeholder": "请输入公告标题",
      "lay-verify": "required"
    }
  }, {
    "item_name": "notice_content",
    "component": "tinymce",
    "classAppend":"layui-col-md12",
    "props": {
      "description": "请输入公告内容"
    }
  }, {
    "item_name": "publish_time",
    "label": "发布时间",
    "component": "date",
    "classAppend":"layui-col-md6",
    "props": {
      "type": "datetime"
    }
  }]
}
```

## 编辑器配置

### 默认配置

系统默认配置如下：

- 语言：中文（zh_CN）
- 高度：600px
- 宽度：100%
- 工具栏浮动：启用
- 菜单栏：隐藏
- 品牌标识：隐藏
- 相对URL：关闭
- URL转换：关闭

### 图片上传

图片上传会自动调用系统配置的上传接口，上传成功后：

1. 图片URL会自动插入到编辑器中
2. 支持粘贴图片自动上传
3. 支持拖拽图片上传
4. 支持批量图片上传

## 注意事项

1. **数据库字段类型**：content字段建议使用 `TEXT` 或 `LONGTEXT` 类型，以存储大量HTML内容
2. **上传接口**：确保系统已配置文件上传接口，TinyMCE 会使用 `uploadUrl` 参数
3. **内容安全**：富文本内容包含HTML标签，展示时需要注意XSS安全问题
4. **列宽度**：强烈建议设置 `col: 12`，让编辑器占满整行，提供更好的编辑体验
5. **标签显示**：TinyMCE 组件默认不显示label标签，编辑器会占用全部宽度
6. **内容保存**：编辑器内容变更时会自动保存到对应的 textarea，表单提交时正常获取

## 常见问题

### 图片上传失败？

检查以下几点：

1. 确认上传接口是否正确配置
2. 检查上传文件大小限制
3. 查看浏览器控制台错误信息
4. 确认后端返回格式：`{data: {src: "图片URL"}}` 或 `{data: {url: "图片URL"}}`

### 如何设置编辑器高度？

目前高度固定为600px，如需自定义，需要修改模板配置。

### 内容中的图片路径问题？

系统配置了 `relative_urls: false` 和 `convert_urls: false`，确保图片路径不会被转换。

### 如何清空编辑器内容？

```javascript
tinymce.get('DESC_ITEM_ID_content').setContent('');
```

## 相关组件

- [textarea](./textarea.md) - 纯文本多行输入
- [upload](./upload.md) - 文件上传
- [input](./input.md) - 单行文本输入
