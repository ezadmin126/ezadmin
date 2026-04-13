# 配置文件热加载功能说明

## 功能概述

当 `EzBootstrap.config().sqlCache` 设置为 `false` 时，系统将直接从项目源文件路径读取配置文件和模板文件，而不是从编译后的 classpath 读取。这允许开发人员在不重新编译项目的情况下，实时修改和测试配置文件及模板的变更。

## 实现原理

### 1. 配置开关
- `sqlCache = true`（默认）：从 classpath 读取配置文件和模板（生产环境）
- `sqlCache = false`：从项目源文件路径读取配置文件和模板（开发环境）

### 2. 文件读取优先级
当 `sqlCache = false` 时：
1. 首先尝试从项目路径读取：`{项目根目录}/src/main/resources/{资源路径}`
2. 如果项目路径不存在，则回退到 classpath 读取

### 3. 支持的文件类型
- **配置文件**：
  - 列表配置：`topezadmin/config/layui/dsl/list/*.json`
  - 表单配置：`topezadmin/config/layui/dsl/form/*.json`
- **模板文件**：
  - 组件模板：`topezadmin/config/layui/dsl/component/*.html`
  - 页面模板：`topezadmin/config/layui/*.html`
  - 插件模板：`topezadmin/config/layui/plugins/**/*.html`

## 使用方法

### 1. 启用热加载

在应用启动时设置配置：

```java
// 在 Spring Boot 应用中
@Configuration
public class EzAdminConfig {

    @Bean
    public EzBootstrapConfig ezBootstrapConfig() {
        EzBootstrapConfig config = new EzBootstrapConfig();

        // 开发环境启用热加载
        if ("dev".equals(System.getProperty("spring.profiles.active"))) {
            config.setSqlCache(false);  // 关闭缓存，启用热加载
        } else {
            config.setSqlCache(true);   // 生产环境使用缓存
        }

        // 其他配置...
        return config;
    }
}
```

### 2. 通过配置文件设置

在 `application.yml` 或 `application.properties` 中：

```yaml
# application-dev.yml
ezadmin:
  sqlCache: false  # 开发环境关闭缓存

# application-prod.yml
ezadmin:
  sqlCache: true   # 生产环境开启缓存
```

### 3. 通过系统属性设置项目根路径（可选）

如果系统无法自动检测项目根路径，可以手动指定：

```java
// 在应用启动时设置
System.setProperty("ezadmin.project.root", "/path/to/your/project");
```

或在启动参数中：

```bash
java -Dezadmin.project.root=/path/to/your/project -jar your-app.jar
```

## 工作流程示例

### 开发时修改配置

1. **设置开发环境**
   ```java
   EzBootstrap.config().setSqlCache(false);
   ```

2. **修改配置文件**
   直接编辑源文件：
   ```
   src/main/resources/topezadmin/config/layui/dsl/list/user-list.json
   ```

3. **刷新页面**
   无需重启应用，直接刷新浏览器页面即可看到配置变更

### 示例：修改列表配置

原始配置（`user-list.json`）：
```json
{
  "id": "user-list",
  "title": "用户列表",
  "columns": [
    {"field": "id", "title": "ID", "width": 80},
    {"field": "name", "title": "姓名", "width": 150}
  ]
}
```

修改后（添加新列）：
```json
{
  "id": "user-list",
  "title": "用户列表",
  "columns": [
    {"field": "id", "title": "ID", "width": 80},
    {"field": "name", "title": "姓名", "width": 150},
    {"field": "email", "title": "邮箱", "width": 200}  // 新增列
  ]
}
```

### 示例：修改组件模板

原始模板（`select.html`）：
```html
<select th:name="${field}" class="form-control">
    <option value="">请选择</option>
    <!-- options -->
</select>
```

修改后（添加样式）：
```html
<select th:name="${field}" class="form-control custom-select">
    <option value="">-- 请选择 --</option>
    <!-- options with custom styling -->
</select>
```

保存文件后，刷新页面即可看到组件样式的变更。

## 注意事项

1. **性能影响**：热加载模式会在每次请求时读取文件，可能影响性能，建议仅在开发环境使用。

2. **文件路径**：确保项目结构符合标准 Maven/Gradle 项目结构：
   ```
   project-root/
   ├── src/
   │   └── main/
   │       └── resources/
   │           └── topezadmin/
   │               └── config/
   │                   └── layui/
   │                       └── dsl/
   │                           ├── list/
   │                           └── form/
   └── pom.xml 或 build.gradle
   ```

3. **权限问题**：确保应用有权限读取项目源文件路径。

4. **生产环境**：生产环境务必设置 `sqlCache = true`，使用编译后的资源文件。

5. **IDE 集成**：在 IDE 中运行时，工作目录通常已经是项目根目录，热加载功能可以正常工作。

## 调试信息

启用调试日志查看文件加载详情：

```xml
<!-- logback.xml -->
<logger name="top.ezadmin.common.utils.ConfigFileLoader" level="DEBUG"/>
```

日志示例：
```
DEBUG ConfigFileLoader - Loaded config from project path (hot reload enabled): topezadmin/config/layui/dsl/list/user-list.json
```

## 常见问题

### Q: 修改文件后没有生效？
A: 检查以下几点：
1. 确认 `sqlCache` 设置为 `false`
2. 确认文件路径正确
3. 查看日志确认是否从项目路径加载

### Q: 如何确认当前是否启用了热加载？
A: 可以通过以下代码检查：
```java
boolean hotReloadEnabled = !EzBootstrap.config().isSqlCache();
System.out.println("热加载状态: " + (hotReloadEnabled ? "启用" : "禁用"));
```

### Q: 部署后能否使用热加载？
A: 不建议。部署后通常没有源文件，应该使用编译后的资源文件（`sqlCache = true`）。

## 相关类

- `ConfigFileLoader`: 核心文件加载工具类，处理 JSON 配置文件的热加载
- `ThymeleafEzTemplate`: 模板引擎实现类，支持 HTML 模板的热加载
- `EzBootstrapConfig`: 配置类，包含 `sqlCache` 属性
- `ListController`: 使用配置文件的列表控制器
- `FormController`: 使用配置文件的表单控制器
- `TemplateUtils`: 模板工具类，用于渲染组件模板