# DSL代码编辑器使用指南

## 📝 功能说明

为DslSourceEditorController添加了在线代码编辑功能，使用ACE Editor提供专业的JSON编辑体验。

## 🚀 新增功能

### 1. 代码编辑器页面
- **模板文件**: `src/main/resources/topezadmin/config/layui/dsl/codeEditor.html`
- **编辑器**: ACE Editor with JSON mode
- **功能特性**:
  - JSON语法高亮
  - 自动补全和代码提示
  - 实时语法验证
  - 代码格式化（Format按钮）
  - 快捷键保存（Ctrl+S / Cmd+S）
  - 实时显示光标位置（行号、列号）
  - 状态指示器（已保存/已修改/错误）

### 2. 控制器方法
在 `DslSourceEditorController.java` 中新增:

```java
public EzResult codeEditor(RequestContext requestContext, String dslType, String dslId)
```

**参数说明**:
- `dslType`: DSL类型，"form" 或 "list"
- `dslId`: DSL配置ID

### 3. 路由配置
在 `EzBootstrap.java` 中添加了两个路由case:

#### Form DSL代码编辑
```
URL: /topezadmin/dsl/form/code-{formId}
示例: /topezadmin/dsl/form/code-test-save-form
```

#### List DSL代码编辑
```
URL: /topezadmin/dsl/list/code-{listId}
示例: /topezadmin/dsl/list/code-test-file-list
```

## 📋 可用测试文件

### Form DSL测试文件
- `test-save-form` - 简单表单配置
- `test-file-form` - 文件版表单配置
- `test-form` - 测试表单
- `test-form-express` - 带表达式的表单
- `test` - 完整表单配置

**测试URL**:
- http://localhost:8080/topezadmin/dsl/form/code-test-save-form
- http://localhost:8080/topezadmin/dsl/form/code-test-file-form

### List DSL测试文件
- `test-file-list` - 文件版列表配置
- `test-list` - 测试列表
- `test-array-express` - 数组表达式列表
- `test-express-ref` - 表达式引用列表
- `test` - 完整列表配置

**测试URL**:
- http://localhost:8080/topezadmin/dsl/list/code-test-file-list
- http://localhost:8080/topezadmin/dsl/list/code-test-list

## 🎯 使用流程

### 1. 访问代码编辑器
访问对应的URL，例如:
```
/topezadmin/dsl/form/code-test-save-form
```

### 2. 编辑DSL配置
- 页面加载后自动显示DSL JSON配置
- 使用ACE编辑器进行编辑
- 实时语法检查会标记错误

### 3. 格式化代码
点击 **格式化** 按钮，自动美化JSON格式（缩进、换行等）

### 4. 验证JSON
点击 **验证JSON** 按钮，检查JSON格式是否正确

### 5. 保存配置
- 点击 **保存** 按钮
- 或使用快捷键 `Ctrl+S` (Windows/Linux) 或 `Cmd+S` (Mac)
- 保存前会自动验证JSON格式
- 保存成功后状态指示器变为绿色"已保存"

### 6. 状态指示
- 🟢 **已保存**: 配置已成功保存
- 🟡 **已修改**: 配置有未保存的修改
- 🔴 **错误**: JSON格式错误或保存失败

## 🔧 技术实现

### 前端技术栈
- **ACE Editor 1.35.5**: 代码编辑器
- **Layui 2.9.20**: UI框架
- **jQuery**: AJAX请求

### 编辑器配置
```javascript
editor.setTheme("ace/theme/tomorrow");
editor.session.setMode("ace/mode/json");
editor.setOptions({
    fontSize: 14,
    showPrintMargin: false,
    enableBasicAutocompletion: true,
    enableLiveAutocompletion: true,
    enableSnippets: true,
    tabSize: 2,
    useSoftTabs: true
});
```

### AJAX端点
- **加载配置**: `GET /topezadmin/dsl/{type}/get-{id}`
- **保存配置**: `POST /topezadmin/dsl/{type}/save-{id}`

### 路由解析流程
```
URL: /topezadmin/dsl/form/code-test-save-form
↓
正则匹配: contro=dsl, method=form, id=code-test-save-form
↓
解析action: action=code, formId=test-save-form
↓
调用: controller.codeEditor(requestContext, "form", "test-save-form")
↓
渲染: layui/dsl/codeEditor.html
```

## ✅ 编译测试结果

```bash
mvn clean compile -DskipTests
```

**结果**: ✅ BUILD SUCCESS

- 编译成功，无语法错误
- 模板文件正确放置
- 路由配置正确

## 🔄 与现有功能的关系

### DslSourceEditorController功能对比

| 功能 | AI编辑模式 | 代码编辑模式 |
|------|-----------|------------|
| 入口 | `/topezadmin/dsl/form/editor-{id}` | `/topezadmin/dsl/form/code-{id}` |
| 编辑方式 | AI对话式修改 | 直接编辑JSON代码 |
| 适用场景 | 快速修改、自然语言描述 | 精确控制、批量修改 |
| 技术要求 | 需要AI模型支持 | 纯前端，无需AI |
| 学习曲线 | 低（自然语言） | 中（需了解DSL结构） |

## 📖 后续扩展

可以考虑的增强功能：
1. 侧边栏显示所有Form/List配置列表（已有UI结构，需连接数据）
2. 代码对比功能（显示修改前后差异）
3. 多标签编辑（同时打开多个配置文件）
4. 代码片段库（常用DSL模板）
5. 撤销/重做历史记录
6. 实时协作编辑

## 🎉 总结

代码编辑器功能已完整实现：
- ✅ 前端ACE编辑器界面
- ✅ 后端控制器方法
- ✅ Form DSL路由
- ✅ List DSL路由
- ✅ 编译通过
- ✅ 与现有AI编辑功能互补

开发者现在可以选择：
- 使用AI编辑模式进行智能修改
- 使用代码编辑模式进行精确控制
