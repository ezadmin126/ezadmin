# upload 组件

## 组件说明

文件上传组件,基于 Layui 上传组件实现,支持单文件、多文件上传,支持拖拽排序,可用于图片、文档等各类文件上传。

## 使用场景

- 头像上传
- 身份证/营业执照上传
- 附件上传
- 产品图片上传
- 轮播图上传

## 基本配置

```json
{
  "item_name": "avatar",
  "label": "头像",
  "component": "upload",
  "col": 6,
  "props": {
    "accept": "images",
    "multiple": false
  }
}
```

## 配置项说明

### 基础属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| item_name | String | 是 | 字段名，对应数据库字段 |
| label | String | 是 | 显示标签 |
| component | String | 是 | 固定值：`upload` |
| col | Number | 否 | 列宽度(0-12)，默认12 |

### props 属性

| 属性 | 类型 | 说明 | 示例 | 默认值 |
|------|------|------|------|-------|
| accept | String | 允许上传的文件类型 | `images`、`file`、`video`、`audio` | `file` |
| acceptMime | String | 自定义允许的MIME类型 | `image/jpg,image/png` | - |
| multiple | Boolean | 是否允许多文件上传 | `true`、`false` | `false` |
| number | Number | 最大上传数量 | `5` | 无限制 |
| size | Number | 单个文件大小限制(KB) | `10240` (10MB) | - |
| lay-verify | String | 表单验证规则 | `"required"` | - |
| required | Boolean | 是否必填(显示红色*) | `true` | `false` |
| description | String | 字段说明文本 | `"支持jpg、png格式"` | - |

### accept 类型说明

| 类型 | 说明 | 文件格式 |
|------|------|---------|
| images | 图片 | jpg、png、gif、bmp等 |
| file | 普通文件 | 所有文件 |
| video | 视频 | mp4、avi、mov等 |
| audio | 音频 | mp3、wav等 |

## 完整示例

### 单图片上传

```json
{
  "item_name": "avatar",
  "label": "头像",
  "component": "upload",
  "col": 6,
  "props": {
    "accept": "images",
    "multiple": false,
    "size": 2048,
    "description": "支持jpg、png格式，大小不超过2MB"
  }
}
```

### 多图片上传

```json
{
  "item_name": "product_images",
  "label": "产品图片",
  "component": "upload",
  "col": 12,
  "props": {
    "accept": "images",
    "multiple": true,
    "number": 5,
    "size": 5120,
    "lay-verify": "required",
    "description": "最多上传5张图片，每张不超过5MB"
  }
}
```

### 附件上传

```json
{
  "item_name": "attachments",
  "label": "附件",
  "component": "upload",
  "col": 12,
  "props": {
    "accept": "file",
    "multiple": true,
    "number": 10,
    "size": 20480,
    "description": "支持上传各类文件，单个文件不超过20MB"
  }
}
```

### 身份证上传

```json
{
  "item_name": "id_card_images",
  "label": "身份证",
  "component": "upload",
  "col": 12,
  "props": {
    "accept": "images",
    "multiple": true,
    "number": 2,
    "size": 5120,
    "lay-verify": "required",
    "description": "请上传身份证正反面，每张不超过5MB"
  }
}
```

### 自定义文件类型

```json
{
  "item_name": "document",
  "label": "文档",
  "component": "upload",
  "col": 6,
  "props": {
    "accept": "file",
    "acceptMime": "application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "multiple": false,
    "description": "仅支持PDF、Word格式"
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
    "label": "基本信息",
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
        "dataSql": "SELECT id value, name label FROM categories"
      }
    }, {
      "item_name": "cover_image",
      "label": "封面图",
      "component": "upload",
      "col": 6,
      "props": {
        "accept": "images",
        "multiple": false,
        "size": 2048,
        "lay-verify": "required",
        "description": "产品封面图，尺寸建议800x800"
      }
    }, {
      "item_name": "detail_images",
      "label": "详情图",
      "component": "upload",
      "col": 12,
      "props": {
        "accept": "images",
        "multiple": true,
        "number": 10,
        "size": 5120,
        "description": "产品详情图，最多10张"
      }
    }, {
      "item_name": "specification",
      "label": "规格说明书",
      "component": "upload",
      "col": 6,
      "props": {
        "accept": "file",
        "acceptMime": "application/pdf",
        "multiple": false,
        "size": 10240,
        "description": "仅支持PDF格式"
      }
    }]
  }],
  "initExpress": ["return selectOne('SELECT * FROM products WHERE id=${ID}')"],
  "submitExpress": [
    "if(!isNotBlank('id')) {",
    "  return insert('INSERT INTO products(product_name,category_id,cover_image,detail_images,specification,create_time) VALUES(#{product_name},#{category_id},#{cover_image},#{detail_images},#{specification},NOW())')",
    "} else {",
    "  return update('UPDATE products SET product_name=#{product_name},category_id=#{category_id},cover_image=#{cover_image},detail_images=#{detail_images},specification=#{specification},update_time=NOW() WHERE id=#{id}')",
    "}"
  ]
}
```

## 数据格式

### 单文件上传

存储格式为文件路径字符串：
```
"/upload/2024/01/01/abc123.jpg"
```

### 多文件上传

存储格式为逗号分隔的文件路径字符串：
```
"/upload/2024/01/01/abc123.jpg,/upload/2024/01/01/def456.jpg,/upload/2024/01/01/ghi789.jpg"
```

## 文件展示

### 图片预览

上传的图片会自动显示缩略图预览，支持：
- 点击查看大图
- 拖拽排序（多图上传时）
- 删除图片

### 文件列表

非图片文件显示为文件名列表，支持：
- 点击下载
- 删除文件

## 上传流程

1. 用户点击"上传"按钮
2. 选择文件
3. 自动上传到服务器
4. 服务器返回文件路径
5. 文件路径存储到隐藏输入框
6. 显示文件预览

## 注意事项

1. **上传接口**：组件会自动调用系统配置的上传接口

2. **文件大小**：props.size 单位为 KB，1MB = 1024KB

3. **数据库字段**：
   - 单文件：VARCHAR(255)
   - 多文件：VARCHAR(1000) 或 TEXT

4. **文件存储**：
   - 单文件：存储文件路径字符串
   - 多文件：存储逗号分隔的路径字符串

5. **拖拽排序**：多文件上传时支持拖拽调整顺序，顺序会影响存储的路径顺序

6. **必填验证**：设置 `lay-verify: "required"` 时，至少要上传一个文件

7. **接口返回格式**：上传接口需要返回以下格式：
   ```json
   {
     "code": 0,
     "data": {
       "src": "/upload/path/file.jpg",
       "url": "/upload/path/file.jpg"
     }
   }
   ```

## 常见问题

### 上传失败？

检查：
1. 上传接口是否配置正确
2. 文件大小是否超过限制
3. 文件类型是否允许
4. 服务器磁盘空间是否充足
5. 浏览器控制台是否有错误信息

### 如何限制图片尺寸？

目前组件不支持尺寸限制，可以：
1. 在description中提示建议尺寸
2. 在后端上传接口中进行尺寸检查

### 如何实现图片裁剪？

需要集成第三方图片裁剪插件，如cropper.js。

### 上传的文件如何展示？

在列表中展示：
- 图片：使用 `tdPic` 组件
- 链接：使用 `tdLink` 组件

### 如何获取上传文件的顺序？

多文件上传时，文件路径按上传顺序（或拖拽排序后的顺序）以逗号分隔存储。

### 如何设置默认图片？

在 initExpress 中返回文件路径：
```javascript
{
  "initExpress": [
    "map = selectOne('SELECT * FROM table WHERE id=${ID}');",
    "// cover_image 字段格式：'/upload/path/file.jpg'",
    "// detail_images 字段格式：'/upload/path/file1.jpg,/upload/path/file2.jpg'",
    "return map;"
  ]
}
```

## 文件下载

### 配置下载URL

系统会自动使用配置的 downloadUrl 前缀：

```html
<img src="downloadUrl + filePath">
```

### 文件链接

使用 `tdLink` 组件展示可下载的链接：

```json
{
  "column": [{
    "item_name": "attachment",
    "label": "附件",
    "component": "tdLink",
    "props": {
      "url": "{{=downloadUrl}}{{=d.attachment}}"
    }
  }]
}
```

## 相关组件

- [tdPic](./tdPic.md) - 图片展示（列表中使用）
- [tdLink](./tdLink.md) - 链接展示（列表中使用）
- [tinymce](./tinymce.md) - 富文本编辑器（也支持图片上传）
