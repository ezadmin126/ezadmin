# EZ Inspector

> 类似 F12 开发者工具的页面元素检查插件（支持 item_name 属性过滤）

一个轻量级、易集成的前端插件，让您可以像使用浏览器开发者工具一样检查页面元素，获取源代码，并支持与后端或 AI 交互。

## 🎉 V2.0 重大更新（2026-02-08）

**核心改进：**
1. **✅ 使用 Layui Layer 弹框** - 替换原有的自定义弹框，风格统一，与 Layui 项目完美集成
2. **✅ Layui 组件支持** - 弹框内容使用 Layui 表单组件和按钮样式
3. **✅ 简化选中样式** - 选中元素时使用简单的 2px 绿色边框，不影响页面布局
4. **✅ 自动激活** - 引入 JS 文件后自动初始化并激活，无需手动调用任何方法
5. **✅ 依赖说明** - 需要页面引入 Layui 和 jQuery（项目通常已有）

**破坏性变更：**
- 需要页面预先引入 Layui 框架（CSS + JS）
- 需要引入 jQuery（Layui 通常已包含）
- 移除了原有的自定义面板样式类
- **自动激活** - 引入JS后会自动弹出检查面板

**🆕 特别功能：**
- 只选中带有 `item_name` 属性的元素
- 弹框始终显示，即使未选中元素也可输入并提交
- 完全兼容 Layui 项目的样式系统
- **零配置** - 只需引入JS，无需任何初始化代码

## ✨ 特性

- 🎯 **智能元素选择** - 只选中带有 `item_name` 属性的元素，避免误选
- 💬 **始终可用面板** - 激活后面板始终显示，无需选中元素即可输入问题
- 📝 **代码获取** - 自动获取 HTML 源代码、XPath、CSS Selector、item_name 属性
- 💬 **备注功能** - 支持添加文字说明和问题描述
- 📋 **一键复制** - 快速复制代码到剪贴板
- 🎨 **样式隔离** - 完全独立的样式系统，不受主页面 CSS 影响
- 🎨 **美观界面** - 现代化 UI，支持拖拽和最小化
- ⌨️ **快捷键** - 支持自定义快捷键快速激活
- 📱 **响应式** - 适配各种屏幕尺寸
- 🚀 **零依赖** - 纯原生 JavaScript 实现
- 🔌 **易集成** - 只需引入一个 JS 和 CSS 文件

## 🚀 快速开始

### 前置依赖

**V2.0 版本需要先引入以下依赖：**

```html
<!-- 1. 引入 jQuery -->
<script src="https://cdn.jsdelivr.net/npm/jquery@3.6.0/dist/jquery.min.js"></script>

<!-- 2. 引入 Layui -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/layui@2.8.18/dist/css/layui.css">
<script src="https://cdn.jsdelivr.net/npm/layui@2.8.18/dist/layui.js"></script>
```

> 如果您的项目已经引入了 Layui，可以跳过此步骤。

### 1. 引入 EZ Inspector 文件（自动激活）

**最简单的使用方式 - 引入即用！**

```html
<!-- 引入样式 -->
<link rel="stylesheet" href="path/to/ez-inspector.css">

<!-- 引入脚本 - 会自动激活并弹出检查面板 -->
<script src="path/to/ez-inspector.js"></script>
```

就这么简单！引入JS后：
1. 会自动等待 Layui 加载完成
2. 自动初始化 Inspector
3. 自动激活检查模式
4. 自动弹出检查面板

### 2. 开始使用

引入JS后，页面会立即：
- ✅ 鼠标悬停在**带有 `item_name` 属性的元素**上会显示蓝色高亮框
- ✅ 点击元素会显示绿色边框并更新面板信息
- ✅ 可以直接在面板中输入备注并提交
- ✅ 按快捷键 `Ctrl + Shift + I` 可以切换激活/停用状态

### 3. 监听提交事件（可选）

如果需要处理提交的数据：

```html
<script>
  // 监听提交事件
  window.addEventListener('ezinspector:submit', (event) => {
    console.log('提交的数据:', event.detail);

    // 发送到后端
    fetch('/api/inspector/submit', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(event.detail)
    });
  });
</script>
```

### 📌 item_name 属性说明

插件**只会响应带有 `item_name` 属性的元素**，这样可以：
- 避免误选无关元素
- 精确定位需要检查的业务组件
- 配合表单或页面配置系统使用

```html
<!-- 可选中的元素 -->
<div item_name="username_input">
  <input type="text" placeholder="用户名" />
</div>

<!-- 不可选中的普通元素 -->
<div>
  <p>这个不会被高亮</p>
</div>

<!-- 嵌套元素会向上查找 -->
<div item_name="form_container">
  <button>点击这个按钮会选中父元素</button>
</div>
```

## 📖 API 文档

### 初始化配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `apiEndpoint` | String | `/api/inspector/submit` | 数据提交的后端接口地址 |
| `hotkey` | String | `ctrl+shift+i` | 激活快捷键 |
| `theme` | String | `light` | 主题：`light` / `dark` |
| `position` | String | `bottom-right` | 面板位置：`top-left` / `top-right` / `bottom-left` / `bottom-right` / `center` |
| `autoStart` | Boolean | `false` | 是否自动激活检查模式 |
| `zIndex` | Number | `999999` | 面板和高亮层的 z-index |

### 方法

```javascript
// 初始化（必须先调用）
EzInspector.init(options);

// 激活检查模式
EzInspector.activate();

// 停用检查模式
EzInspector.deactivate();

// 切换检查模式
EzInspector.toggle();

// 获取当前实例
const instance = EzInspector.getInstance();

// 销毁实例
EzInspector.destroy();
```

### 事件监听

```javascript
// 监听数据提交事件
window.addEventListener('ezinspector:submit', (event) => {
  const data = event.detail;

  console.log('URL:', data.url);
  console.log('元素信息:', data.element);
  console.log('用户备注:', data.userNote);
  console.log('页面信息:', data.pageInfo);

  // 自定义处理逻辑
  // 例如：发送给后端或 AI
  fetch('/api/analyze', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  });
});
```

### 数据格式

提交时会收集以下数据：

```javascript
{
  "url": "https://example.com/page",
  "timestamp": "2026-02-08T10:30:00Z",
  "element": {
    "html": "<div class='container'>...</div>",
    "tagName": "DIV",
    "className": "container",
    "id": "main-content",
    "itemName": "form_username",  // item_name 属性值
    "xpath": "/html/body/div[1]/div[2]",
    "cssSelector": "body > div.container",
    "textContent": "元素的文本内容（限制200字符）"
  },
  "userNote": "用户输入的备注说明",
  "pageInfo": {
    "title": "页面标题",
    "viewport": "1920x1080",
    "userAgent": "浏览器信息"
  }
}

// 如果未选中元素，element 为 null
{
  "url": "https://example.com/page",
  "timestamp": "2026-02-08T10:30:00Z",
  "element": null,
  "userNote": "用户直接输入的问题",
  "pageInfo": { ... }
}
```

## 🎯 使用场景

### 1. 开发调试

快速定位和记录页面问题元素，配合开发工具使用。

```javascript
EzInspector.init({
  autoStart: true,
  position: 'top-right'
});
```

### 2. 用户反馈

让用户可以直接标注问题位置并提交反馈。

```javascript
EzInspector.init({
  apiEndpoint: '/api/feedback/submit'
});

window.addEventListener('ezinspector:submit', async (e) => {
  // 添加用户信息
  const data = {
    ...e.detail,
    userId: getCurrentUserId(),
    reportType: 'bug'
  };

  // 提交到服务器
  await fetch('/api/feedback/submit', {
    method: 'POST',
    body: JSON.stringify(data)
  });

  alert('反馈已提交，感谢您的支持！');
});
```

### 3. 与 AI 交互

将选中的元素信息发送给 AI 进行分析或生成建议。

```javascript
EzInspector.init({
  position: 'bottom-right'
});

window.addEventListener('ezinspector:submit', async (e) => {
  const { element, userNote } = e.detail;

  // 构建提示词
  const prompt = `
    用户问题: ${userNote}
    页面元素: ${element.html}
    元素类型: ${element.tagName}
    CSS 选择器: ${element.cssSelector}

    请分析这个元素并提供优化建议。
  `;

  // 发送给 AI API
  const response = await fetch('/api/ai/analyze', {
    method: 'POST',
    body: JSON.stringify({ prompt })
  });

  const result = await response.json();
  console.log('AI 分析结果:', result);
});
```

### 4. UI 测试记录

测试人员可以标注 UI 问题并自动记录相关信息。

```javascript
EzInspector.init({
  apiEndpoint: '/api/testing/report'
});
```

## 🎨 自定义样式

如果需要自定义样式，可以覆盖 CSS 变量或类：

```css
/* 修改高亮颜色 */
.ez-inspector-highlighter {
  border-color: #ff4d4f !important;
  background-color: rgba(255, 77, 79, 0.1) !important;
}

/* 修改面板宽度 */
.ez-inspector-panel {
  width: 600px !important;
}

/* 修改主题色 */
.ez-inspector-header {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%) !important;
}
```

## 🔌 与 EzAdmin 集成

如果您在使用 EzAdmin 项目，可以这样集成：

### 方式 1：全局引入

在 `resources/META-INF/resources/webjars/topezadmin/` 目录下放置文件，然后在模板中引入：

```html
<!-- 在 Thymeleaf 模板中 -->
<link rel="stylesheet" th:href="@{/webjars/topezadmin/ez-inspector/ez-inspector.css}">
<script th:src="@{/webjars/topezadmin/ez-inspector/ez-inspector.js}"></script>

<script th:inline="javascript">
  EzInspector.init({
    apiEndpoint: /*[[@{/api/inspector/submit}]]*/ '/api/inspector/submit'
  });
</script>
```

### 方式 2：仅开发环境使用

```html
<!-- 仅在开发环境加载 -->
<th:block th:if="${#strings.equals(env, 'dev')}">
  <link rel="stylesheet" th:href="@{/webjars/topezadmin/ez-inspector/ez-inspector.css}">
  <script th:src="@{/webjars/topezadmin/ez-inspector/ez-inspector.js}"></script>
  <script>
    EzInspector.init({ autoStart: true });
  </script>
</th:block>
```

## 🌐 浏览器兼容性

| 浏览器 | 版本 |
|--------|------|
| Chrome | 60+ |
| Firefox | 60+ |
| Safari | 12+ |
| Edge | 79+ |

## 📝 开发计划

- [ ] 支持屏幕截图功能
- [ ] 历史记录功能
- [ ] 导出为 JSON/Markdown
- [ ] 暗色主题支持
- [ ] 代码语法高亮
- [ ] 支持 Shadow DOM
- [ ] 多语言支持
- [ ] 性能优化

## 📄 License

MIT License

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📧 联系方式

如有问题或建议，请联系：[您的联系方式]

---

**Enjoy! 🎉**
