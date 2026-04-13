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

| 属性 | 类型 | 必填 | 说明                        |
|------|------|------|---------------------------|
| item_name | String | 是 | 字段名，对应数据库字段               |
| label | String | 是 | 显示标签                      |
| component | String | 是 | 固定值：`upload`              |
| classAppend | String | 否 | layui-col-md8 主要使用layui布局设置占用宽度                       |
| description | String | 字段说明文本 | `"可选择多个"`                 |
| props | Object | 否 | 组件属性  支持layui upload组件的属性 |
### props 属性

| 属性 | 类型 | 说明           | 示例 | 默认值 |
|------|------|--------------|------|-------|
| accept | String | 允许上传的文件类型    | `images`、`file`、`video`、`audio` | `file` |
| acceptMime | String | 自定义允许的MIME类型 | `image/jpg,image/png` | - |
| multiple | Boolean | 是否允许多文件上传    | `true`、`false` | `false` |
| number | Number | 同时允许上传数量     | `5` | 无限制 |
| size | Number | 单个文件大小限制(KB) | `10240` (10MB) | - |
| lay-verify | String | 表单验证规则       | `"required"` | - |
| required | Boolean | 是否必填(显示红色*)  | `true` | `false` |
| description | String | 字段说明文本       | `"支持jpg、png格式"` | - |

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
  "classAppend":"layui-col-md8",
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
  "classAppend":"layui-col-md8",
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
  "classAppend":"layui-col-md8",
  "props": {
    "accept": "file",
    "multiple": true,
    "number": 10,
    "size": 20480,
    "description": "支持上传各类文件，单个文件不超过20MB"
  }
}
```
 

### 自定义文件类型

```json
{
  "item_name": "document",
  "label": "文档",
  "component": "upload",
  "classAppend":"layui-col-md8",
  "props": {
    "accept": "file",
    "acceptMime": "application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "multiple": false,
    "description": "仅支持PDF、Word格式"
  }
}
```

### 最大上传数量与最小上传数量

通过内置的 jquery validate扩展方法实现 ，
| uploadMax | Number | 最大上传数量限制     | `10` | 无限制 |
| uploadMin | Number | 最小上传数量限制     | `1` | 无限制 |
```json
{
   "item_name": "upload_test",
   "props": {
      "validate": {
         "rule": {
           "uploadMax": 10,
           "uploadMin": 1
         },
         "message": {
           "uploadMax": "最多上传10个文件",
           "uploadMin": "至少上传1个文件"
         }
      }
   }
}

```
