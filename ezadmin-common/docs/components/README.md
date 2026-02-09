# EzAdmin 组件文档索引

本文档提供了 EzAdmin DSL 配置中所有可用组件的快速索引和说明。

## 组件分类

### 输入类组件（表单输入）

用于表单的 fieldList 配置中，接收用户输入。

| 组件 | 说明 | 适用场景 | 文档 |
|------|------|---------|------|
| input | 单行文本输入 | 姓名、账号、标题等 | [详细文档](./input.md) |
| textarea | 多行文本输入 | 备注、描述、地址等 | [详细文档](./textarea.md) |
| hidden | 隐藏字段 | ID、状态标识等 | [详细文档](./hidden.md) |
| tinymce | 富文本编辑器 | 文章内容、产品详情 | [详细文档](./tinymce.md) |

### 选择类组件（表单选择）

用于表单的 fieldList 配置中，从预定义选项中选择。

| 组件 | 说明 | 适用场景 | 文档 |
|------|------|---------|------|
| select | 下拉单选 | 状态、分类选择 | [详细文档](./select.md) |
| select-multiple | 下拉多选 | 标签、权限选择 | [详细文档](./select-multiple.md) |
| cascader | 级联选择 | 省市区、部门选择 | [详细文档](./cascader.md) |
| radio | 单选框 | 性别、是否选择 | [详细文档](./radio.md) |
| checkbox | 复选框 | 兴趣爱好、权限勾选 | [详细文档](./checkbox.md) |

### 日期和文件组件

| 组件 | 说明 | 适用场景 | 文档 |
|------|------|---------|------|
| date | 日期时间选择 | 出生日期、创建时间 | [详细文档](./date.md) |
| upload | 文件上传 | 头像、附件上传 | [详细文档](./upload.md) |

### 按钮组件

用于列表的 rowButton、tableButton 等配置中。

| 组件 | 说明 | 适用场景 | 文档 |
|------|------|---------|------|
| button-normal | 行操作按钮 | 编辑、删除等行操作 | [详细文档](./button.md#button-normal) |
| button-toolbar | 工具栏按钮 | 新增、导出等工具栏操作 | [详细文档](./button.md#button-toolbar) |
| button-dropdown | 下拉菜单项 | 下拉菜单中的操作 | [详细文档](./button.md#button-dropdown) |
| button-bread | 面包屑按钮 | 面包屑导航 | [详细文档](./button.md#button-bread) |
| button-span | 文本按钮 | 文本样式的按钮 | [详细文档](./button.md#button-span) |

### 表格展示组件

用于列表的 column 配置中，格式化展示数据。

| 组件 | 说明 | 适用场景 | 文档 |
|------|------|---------|------|
| tdText | 文本展示 | 普通文本字段 | [详细文档](./table-display.md#tdtext) |
| tdPic | 图片展示 | 图片字段 | [详细文档](./table-display.md#tdpic) |
| tdLink | 链接展示 | 可点击的链接 | [详细文档](./table-display.md#tdlink) |
| tdSelect | 下拉值展示 | 将value转为label | [详细文档](./table-display.md#tdselect) |
| tdSelectMultiple | 多选值展示 | 将多个value转为label | [详细文档](./table-display.md#tdselectmultiple) |
| tdCascader | 级联值展示 | 将级联path转为label | [详细文档](./table-display.md#tdcascader) |

### 特殊组件

| 组件 | 说明 | 适用场景 | 文档 |
|------|------|---------|------|
| select-span | 下拉值只读展示 | 表单详情页的只读展示 | [详细文档](./select-span.md) |

## 快速查找

### 按使用位置分类

#### 表单输入（fieldList）

**文本输入：**
- [input](./input.md) - 单行文本
- [textarea](./textarea.md) - 多行文本
- [tinymce](./tinymce.md) - 富文本

**选择输入：**
- [select](./select.md) - 单选下拉
- [select-multiple](./select-multiple.md) - 多选下拉
- [cascader](./cascader.md) - 级联选择
- [radio](./radio.md) - 单选框
- [checkbox](./checkbox.md) - 复选框

**其他输入：**
- [date](./date.md) - 日期时间
- [upload](./upload.md) - 文件上传
- [hidden](./hidden.md) - 隐藏字段

**只读展示：**
- [select-span](./select-span.md) - 下拉值只读

#### 列表展示（column）

- [tdText](./table-display.md#tdtext) - 文本
- [tdPic](./table-display.md#tdpic) - 图片
- [tdLink](./table-display.md#tdlink) - 链接
- [tdSelect](./table-display.md#tdselect) - 下拉值
- [tdSelectMultiple](./table-display.md#tdselectmultiple) - 多选值
- [tdCascader](./table-display.md#tdcascader) - 级联值

#### 列表搜索（search）

所有表单输入组件都可以用在搜索项中：
- [input](./input.md)
- [select](./select.md)
- [select-multiple](./select-multiple.md)
- [cascader](./cascader.md)
- [radio](./radio.md)
- [checkbox](./checkbox.md)
- [date](./date.md)

#### 按钮操作

- [button-normal](./button.md#button-normal) - 行操作按钮（rowButton）
- [button-toolbar](./button.md#button-toolbar) - 工具栏按钮（tableButton）
- [button-dropdown](./button.md#button-dropdown) - 下拉菜单项
- [button-bread](./button.md#button-bread) - 面包屑
- [button-span](./button.md#button-span) - 文本按钮

### 按数据类型分类

#### 文本类型
- [input](./input.md) - 单行
- [textarea](./textarea.md) - 多行
- [tinymce](./tinymce.md) - 富文本
- [tdText](./table-display.md#tdtext) - 展示

#### 选项类型
- [select](./select.md) / [tdSelect](./table-display.md#tdselect) - 单选
- [select-multiple](./select-multiple.md) / [tdSelectMultiple](./table-display.md#tdselectmultiple) - 多选
- [radio](./radio.md) - 单选框
- [checkbox](./checkbox.md) - 复选框
- [select-span](./select-span.md) - 只读

#### 层级数据
- [cascader](./cascader.md) / [tdCascader](./table-display.md#tdcascader) - 级联选择

#### 日期时间
- [date](./date.md) - 日期时间选择

#### 文件类型
- [upload](./upload.md) - 文件上传
- [tdPic](./table-display.md#tdpic) - 图片展示
- [tdLink](./table-display.md#tdlink) - 文件链接

## 组件对应关系

表单输入组件与列表展示组件的对应关系：

| 表单输入 | 列表展示 | 说明 |
|---------|---------|------|
| input / textarea | tdText | 文本字段 |
| select | tdSelect | 单选下拉 |
| select-multiple / checkbox | tdSelectMultiple | 多选 |
| cascader | tdCascader | 级联 |
| upload（图片） | tdPic | 图片 |
| upload（文件） | tdLink | 文件链接 |
| date | tdText | 日期时间 |
| hidden | - | 隐藏字段 |
| tinymce | tdText | 富文本内容 |

## 组件选择指南

### 文本输入

| 场景 | 推荐组件 |
|------|---------|
| 简短文本（姓名、标题） | [input](./input.md) |
| 多行文本（备注、地址） | [textarea](./textarea.md) |
| 富文本内容（文章、详情） | [tinymce](./tinymce.md) |
| 隐藏传递（ID、标识） | [hidden](./hidden.md) |

### 选择输入

| 场景 | 推荐组件 |
|------|---------|
| 单选，选项较多（>5个） | [select](./select.md) |
| 单选，选项较少（2-5个） | [radio](./radio.md) |
| 多选，选项较多（>8个） | [select-multiple](./select-multiple.md) |
| 多选，选项较少（3-8个） | [checkbox](./checkbox.md) |
| 层级数据选择 | [cascader](./cascader.md) |

### 日期选择

| 场景 | 推荐配置 |
|------|---------|
| 日期 | [date](./date.md) + `type: "date"` |
| 日期时间 | [date](./date.md) + `type: "datetime"` |
| 年份 | [date](./date.md) + `type: "year"` |
| 月份 | [date](./date.md) + `type: "month"` |
| 时间 | [date](./date.md) + `type: "time"` |
| 日期范围 | [date](./date.md) + `range: true` |

### 文件上传

| 场景 | 推荐配置 |
|------|---------|
| 单图片 | [upload](./upload.md) + `accept: "images"`, `multiple: false` |
| 多图片 | [upload](./upload.md) + `accept: "images"`, `multiple: true` |
| 单文件 | [upload](./upload.md) + `accept: "file"`, `multiple: false` |
| 多文件 | [upload](./upload.md) + `accept: "file"`, `multiple: true` |

## 常见问题

### 如何选择合适的组件？

1. 确定使用位置（表单 / 列表 / 搜索）
2. 确定数据类型（文本 / 选项 / 日期 / 文件）
3. 根据上述分类选择合适的组件

### 表单和列表组件如何配对？

参考[组件对应关系](#组件对应关系)表格。

### 组件不生效怎么办？

1. 检查组件名称是否正确
2. 检查配置位置是否正确（fieldList / column / search）
3. 查看对应组件的详细文档
4. 检查必填配置项是否完整

## 相关文档

- [使用说明.md](../使用说明.md) - 完整的配置说明
- [表达式系统](../使用说明.md#四表达式系统) - 表达式语法
- [路由规则](../使用说明.md#六路由规则) - URL路由

## 更新记录

- 2024-01-01: 创建组件文档索引
