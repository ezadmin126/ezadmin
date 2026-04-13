# 按钮组件说明

EzAdmin 提供了多种按钮组件，用于不同的场景。所有按钮组件都支持统一的配置方式和交互行为。

## 组件类型

| 组件 | 使用场景 | 位置 |
|------|---------|------|
| [button-normal](#button-normal) | 行操作按钮 | 列表行操作列 |
| [button-toolbar](#button-toolbar) | 工具栏按钮 | 列表表头工具栏 |
| [button-dropdown](#button-dropdown) | 下拉菜单项 | 下拉菜单中 |
| [button-bread](#button-bread) | 面包屑链接 | 面包屑导航 |
| [button-span](#button-span) | 文本按钮 | 任意位置 |

## 通用配置

所有按钮组件都支持以下基础配置：

### 基础属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| item_name | String | 是 | 按钮标识 |
| label | String | 是 | 按钮文本 |
| component | String | 是 | 按钮组件类型 |

### props 属性

| 属性 | 类型 | 说明 | 示例                            |
|------|------|------|-------------------------------|
| opentype | String | 打开方式 | `MODAL`、`FORM`、`AJAX`等        |
| url | String | 目标URL | `/path/to/page`               |
| windowname | String | 窗口标题 | `"编辑用户"`                      |
| area | String | 窗口大小(百分比) | 语法与layui的layer一样              |
| display | String | 显示条件(laytpl表达式) | `"{{d.status==1?'':'none'}}"` |

### opentype 取值

| 值 | 说明 | 适用场景 |
|---|------|---------|
| MODAL | 模态框 | 表单编辑、详情查看 |
| FORM | 全屏表单 | 复杂表单编辑 |
| FULL | 全屏窗口 | 需要更大空间的页面 |
| _BLANK | 新窗口 | 打开外部链接 |
| LOCATION | 当前页跳转 | 页面跳转 |
| PARENT | 父窗口 | 关闭当前窗口并刷新父窗口 |
| AJAX | Ajax请求 | 后端操作（不打开页面） |
| CONFIRM_AJAX | 确认后Ajax | 需要确认的删除等操作 |
| CONFIRM_MODEL | 确认后打开模态框 | 需要确认的编辑操作 |

## button-normal

### 说明
行操作按钮，显示在列表每行的操作列中。

### 配置位置
```json
{
  "rowButton": []
}
```

### 基本配置

```json
{
  "item_name": "edit",
  "label": "编辑",
  "component": "button-normal",
  "props": {
    "opentype": "MODAL",
    "windowname": "编辑",
    "url": "/topezadmin/form/page-user-form?id={{=d.id}}",
    "area": "60,80"
  }
}
```

### 完整示例

```json
{
  "rowButton": [{
    "item_name": "EDIT",
    "label": "编辑",
    "component": "button-normal",
    "props": {
      "opentype": "MODAL",
      "windowname": "编辑用户",
      "url": "/topezadmin/form/page-user-form?id={{=d.id}}",
      "area": "60,80"
    }
  }, {
    "item_name": "DELETE",
    "label": "删除",
    "component": "button-normal",
    "props": {
      "opentype": "CONFIRM_AJAX",
      "url": "/api/user/delete?id={{=d.id}}",
      "windowname": "确认删除?"
    }
  }, {
    "item_name": "DETAIL",
    "label": "详情",
    "component": "button-normal",
    "props": {
      "opentype": "MODAL",
      "windowname": "用户详情",
      "url": "/topezadmin/form/page-user-detail?id={{=d.id}}",
      "area": "70,85"
    }
  }, {
    "item_name": "ENABLE",
    "label": "启用",
    "component": "button-normal",
    "props": {
      "opentype": "AJAX",
      "url": "/api/user/enable?id={{=d.id}}",
      "display": "{{d.status==0?'':'none'}}"
    }
  }]
}
```

## button-toolbar

### 说明
工具栏按钮，显示在列表表头。

### 配置位置
```json
{
  "tableButton": []
}
```

### 基本配置

```json
{
  "item_name": "ADD",
  "label": "新增",
  "component": "button-toolbar",
  "props": {
    "opentype": "MODAL",
    "windowname": "新增",
    "url": "/topezadmin/form/page-user-form",
    "area": "60,80"
  }
}
```

### 完整示例

```json
{
  "tableButton": [{
    "item_name": "ADD",
    "label": "新增",
    "component": "button-toolbar",
    "props": {
      "opentype": "MODAL",
      "windowname": "新增用户",
      "url": "/topezadmin/form/page-user-form",
      "area": "60,80"
    }
  }, {
    "item_name": "BATCH_DELETE",
    "label": "批量删除",
    "component": "button-toolbar",
    "props": {
      "opentype": "CONFIRM_AJAX",
      "url": "/api/user/batchDelete",
      "windowname": "确认删除选中的记录?"
    }
  }, {
    "item_name": "EXPORT",
    "label": "导出",
    "component": "button-toolbar",
    "props": {
      "opentype": "_BLANK",
      "url": "/api/user/export"
    }
  }, {
    "item_name": "IMPORT",
    "label": "导入",
    "component": "button-toolbar",
    "props": {
      "opentype": "MODAL",
      "windowname": "导入用户",
      "url": "/page/import",
      "area": "50,60"
    }
  }]
}
```

## button-dropdown

### 说明
下拉菜单项，配合下拉组件使用。

### 基本配置

```json
{
  "item_name": "EXPORT_EXCEL",
  "label": "导出Excel",
  "component": "button-dropdown",
  "props": {
    "opentype": "_BLANK",
    "url": "/api/export?type=excel"
  }
}
```

## button-bread

### 说明
面包屑按钮，用于面包屑导航。

### 基本配置

```json
{
  "item_name": "HOME",
  "label": "首页",
  "component": "button-bread",
  "props": {
    "opentype": "LOCATION",
    "url": "/home"
  }
}
```

## button-span

### 说明
文本样式的按钮，显示为纯文本。

### 基本配置

```json
{
  "item_name": "TIP",
  "label": "提示信息",
  "component": "button-span",
  "props": {}
}
```

## URL参数说明

### 使用 laytpl 语法

在 URL 中可以使用 laytpl 模板语法获取当前行数据：

```
/path/to/page?id={{=d.id}}&name={{=d.name}}&status={{=d.status}}
```

- `{{=d.字段名}}`: 获取当前行的字段值
- `{{d.字段名}}`: 同上（简写）

### 常见URL模式

**编辑：**
```
/topezadmin/form/page-{form-id}?id={{=d.id}}
```

**详情：**
```
/topezadmin/form/page-{form-id}?id={{=d.id}}&readonly=true
```

**删除：**
```
/topezadmin/form/delete-{form-id}?id={{=d.id}}
```

**状态变更：**
```
/topezadmin/form/status-{form-id}?id={{=d.id}}&status={{=d.status==1?0:1}}
```

## 显示条件

使用 `display` 属性控制按钮的显示/隐藏：

### 根据状态显示

```json
{
  "props": {
    "display": "{{d.status==1?'':'none'}}"
  }
}
```

### 根据角色显示

```json
{
  "props": {
    "display": "{{d.user_type=='admin'?'':'none'}}"
  }
}
```

### 多条件组合

```json
{
  "props": {
    "display": "{{d.status==1 && d.is_delete==0?'':'none'}}"
  }
}
```

## 注意事项

1. **URL编码**: URL中的特殊字符需要编码
2. **模板语法**: 使用 `{{=d.字段名}}` 获取行数据
3. **窗口大小**: area 格式为 "宽度百分比,高度百分比"
4. **显示条件**: display 使用 laytpl 语法，返回空字符串显示，返回'none'隐藏
5. **Ajax请求**: AJAX 和 CONFIRM_AJAX 会自动刷新列表
6. **批量操作**: 批量操作按钮需要配合列表的 selectable 配置

## 常见问题

### 如何实现按钮权限控制？

通过 display 属性：
```json
{
  "props": {
    "display": "{{d.has_edit_permission?'':'none'}}"
  }
}
```

### 如何自定义窗口大小？

通过 area 属性：
```json
{
  "props": {
    "area": "80,90"  // 宽80%，高90%
  }
}
```

### Ajax请求如何传递选中的行？

系统会自动将选中行的ID传递给后端。

### 如何实现确认提示？

使用 CONFIRM_AJAX 或 CONFIRM_MODEL：
```json
{
  "props": {
    "opentype": "CONFIRM_AJAX",
    "windowname": "确认要删除吗?"
  }
}
```
