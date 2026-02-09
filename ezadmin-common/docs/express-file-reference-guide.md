# 表达式文件引用功能说明

## 功能概述

在 JSON 配置文件中编写复杂的 SQL 表达式或业务逻辑脚本时，经常会遇到以下问题：
- JSON 格式对多行文本支持不友好
- 复杂的 SQL 语句在 JSON 中难以编辑和维护
- 无法使用 IDE 的语法高亮和代码提示功能

为解决这些问题，系统现在支持**表达式文件引用**功能。通过在 JSON 配置中使用 `$filename` 的方式，可以将表达式内容保存在独立的文本文件中，系统会自动加载文件内容作为表达式。

## 使用方法

### 1. 基本语法

在 JSON 配置文件中，任何以 `$` 开头的表达式值都会被识别为文件引用：

```json
{
  "express": {
    "main": "$select_express.txt",
    "count": "$count_express.txt"
  }
}
```

系统会自动：
1. 识别以 `$` 开头的值
2. 去掉 `$` 前缀，获取文件名
3. 在配置文件的同级目录查找对应文件
4. 读取文件内容替换原始值

### 2. 列表配置示例

**配置文件**：`topezadmin/config/layui/dsl/list/user-list.json`
```json
{
  "id": "user-list",
  "title": "用户列表",
  "express": {
    "main": "$user_select.sql",
    "count": "$user_count.sql",
    "orderBy": "create_time DESC",
    "groupBy": "$user_group.txt"
  }
}
```

**表达式文件**：`topezadmin/config/layui/dsl/list/user_select.sql`
```sql
SELECT
    u.id,
    u.username,
    u.email,
    u.status,
    CASE
        WHEN u.status = 1 THEN '启用'
        WHEN u.status = 0 THEN '禁用'
        ELSE '未知'
    END as status_text,
    u.create_time,
    u.last_login_time,
    r.role_name
FROM
    sys_user u
    LEFT JOIN sys_user_role ur ON u.id = ur.user_id
    LEFT JOIN sys_role r ON ur.role_id = r.id
WHERE
    u.delete_flag = 0
    {username?AND u.username LIKE '%{username}%'}
    {email?AND u.email LIKE '%{email}%'}
    {status?AND u.status = {status}}
    {role_id?AND ur.role_id = {role_id}}
    {create_time_start?AND u.create_time >= '{create_time_start}'}
    {create_time_end?AND u.create_time <= '{create_time_end}'}
```

### 3. 表单配置示例

**配置文件**：`topezadmin/config/layui/dsl/form/user-form.json`
```json
{
  "id": "user-form",
  "title": "用户表单",
  "express": {
    "init": "$user_init.js",
    "submit": "$user_submit.js",
    "delete": "$user_delete.js",
    "status": "$user_status.js"
  }
}
```

**初始化表达式**：`topezadmin/config/layui/dsl/form/user_init.js`
```javascript
// 用户表单初始化逻辑
if(StringUtils.isNotBlank(id)){
    // 加载用户数据
    Map<String,Object> user = selectOne("""
        SELECT
            u.*,
            GROUP_CONCAT(ur.role_id) as role_ids
        FROM
            sys_user u
            LEFT JOIN sys_user_role ur ON u.id = ur.user_id
        WHERE
            u.id = {id}
            AND u.delete_flag = 0
        GROUP BY u.id
    """);

    if(user != null){
        // 设置表单值
        setValue("username", user.get("username"));
        setValue("email", user.get("email"));
        setValue("phone", user.get("phone"));
        setValue("status", user.get("status"));

        // 处理角色多选
        String roleIds = user.get("role_ids");
        if(StringUtils.isNotBlank(roleIds)){
            setValue("role_ids", roleIds.split(","));
        }

        // 设置只读字段
        setReadonly("username", true);
        setValue("create_time", user.get("create_time"));
        setValue("update_time", user.get("update_time"));
    }
} else {
    // 新增用户，设置默认值
    setValue("status", "1");
    setValue("password", "");
}

// 加载角色列表
List<Map<String,Object>> roles = select("SELECT id, role_name FROM sys_role WHERE status = 1");
setOptions("role_ids", roles);
```

### 4. 数组中的表达式引用示例

系统支持在数组中使用表达式文件引用。数组中的任何以 `$` 开头的字符串元素都会被自动识别并替换为对应文件的内容。

**配置文件**：`topezadmin/config/layui/dsl/list/page-with-scripts.json`
```json
{
  "id": "page-with-scripts",
  "title": "带脚本的页面",
  "appendHead": [
    "<style>",
    "  .custom-header { background: #f0f0f0; }",
    "</style>"
  ],
  "appendFoot": [
    "<script>",
    "$infolist_foot.js",
    "</script>"
  ],
  "customScripts": [
    "$init_script.js",
    "console.log('inline script');",
    "$feature_script.js"
  ]
}
```

在上面的例子中：
- `appendFoot` 数组的第二个元素 `"$infolist_foot.js"` 会被替换为 `infolist_foot.js` 文件的内容
- `customScripts` 数组的第一个和第三个元素会被替换为对应文件的内容
- 其他不以 `$` 开头的元素保持不变

**表达式文件**：`topezadmin/config/layui/dsl/list/infolist_foot.js`
```javascript
// InfoList Footer Script
(function() {
    console.log('InfoList footer loaded');

    // 页面底部的初始化逻辑
    if (window.layui) {
        layui.use(['layer', 'jquery'], function() {
            var $ = layui.jquery;

            $(document).ready(function() {
                console.log('Page fully loaded');
                // 自定义业务逻辑
            });
        });
    }
})();
```

### 5. 嵌套结构中的表达式引用

系统支持在任意深度的嵌套结构中使用表达式文件引用，包括：
- 嵌套的对象（Map）中的字符串字段
- 嵌套的数组（List）中的字符串元素
- 数组中的对象的字符串字段

**示例配置**：
```json
{
  "nestedExample": {
    "items": [
      "normal string",
      "$nested_item.txt",
      {
        "type": "object",
        "content": "$nested_object_content.txt"
      }
    ],
    "config": {
      "template": "$template.html",
      "scripts": ["$script1.js", "inline code", "$script2.js"]
    }
  }
}
```

所有以 `$` 开头的值都会被递归处理并替换为对应文件的内容。

## 文件命名规范

### 建议的命名规范

1. **SQL 查询文件**：使用 `.sql` 扩展名
   - `user_select.sql` - 主查询
   - `user_count.sql` - 计数查询
   - `user_group.sql` - 分组查询

2. **JavaScript 表达式**：使用 `.js` 扩展名
   - `user_init.js` - 初始化逻辑
   - `user_submit.js` - 提交逻辑
   - `user_validate.js` - 验证逻辑

3. **简单文本**：使用 `.txt` 扩展名
   - `order_by.txt` - 排序表达式
   - `group_by.txt` - 分组表达式

### 文件组织结构

```
topezadmin/config/layui/dsl/
├── list/
│   ├── user-list.json          # 用户列表配置
│   ├── user_select.sql         # 用户列表主查询
│   ├── user_count.sql          # 用户列表计数查询
│   ├── product-list.json       # 产品列表配置
│   ├── product_select.sql      # 产品列表主查询
│   └── product_count.sql       # 产品列表计数查询
└── form/
    ├── user-form.json           # 用户表单配置
    ├── user_init.js            # 用户表单初始化
    ├── user_submit.js          # 用户表单提交
    ├── product-form.json       # 产品表单配置
    ├── product_init.js         # 产品表单初始化
    └── product_submit.js       # 产品表单提交
```

## 支持的字段

### 列表配置支持的表达式字段

在 `express` 节点下：
- `main` / `select` - 主查询表达式
- `count` - 计数查询表达式
- `orderBy` - 排序表达式
- `groupBy` - 分组表达式
- `where` - WHERE 条件表达式
- `having` - HAVING 条件表达式

兼容老版本的字段：
- `select_express`
- `count_express`
- `orderby_express`
- `groupby_express`

### 表单配置支持的表达式字段

在 `express` 节点下：
- `init` - 初始化表达式
- `submit` - 提交表达式
- `delete` - 删除表达式
- `status` - 状态变更表达式

兼容老版本的字段：
- `init_express`
- `submit_express`
- `delete_express`
- `status_express`
- `displayorder_express`

## 热加载支持

表达式文件同样支持热加载功能：

1. **开发模式**（`sqlCache = false`）
   - 每次请求都会重新读取表达式文件
   - 修改表达式文件后立即生效，无需重启应用
   - 适合开发和调试

2. **生产模式**（`sqlCache = true`）
   - 表达式文件会被缓存
   - 需要重启应用才能加载新的表达式内容
   - 性能更好，适合生产环境

## 优势

### 1. 更好的编辑体验
- 可以使用专业的 SQL 编辑器编辑 SQL 文件
- 支持语法高亮和代码提示
- 可以格式化和美化代码

### 2. 更易维护
- 表达式独立成文件，结构清晰
- 便于版本控制和代码审查
- 可以添加注释说明业务逻辑

### 3. 复用性
- 相同的表达式文件可以被多个配置引用
- 减少重复代码
- 统一维护，一处修改处处生效

### 4. 支持复杂逻辑
- 不受 JSON 格式限制
- 支持多行文本和复杂格式
- 可以编写完整的业务逻辑脚本

## 注意事项

1. **文件路径**
   - 表达式文件必须与配置文件在同一目录
   - 文件名区分大小写
   - 建议使用有意义的文件名

2. **字符编码**
   - 所有文件使用 UTF-8 编码
   - 避免使用特殊字符

3. **性能考虑**
   - 生产环境建议启用缓存（`sqlCache = true`）
   - 避免在表达式文件中引用过多外部资源

4. **安全性**
   - 表达式文件不应包含敏感信息
   - 注意 SQL 注入防护
   - 合理设置文件权限

## 示例项目

完整的示例文件已包含在项目中：

- 列表配置示例：`test-express-ref.json`
- 表单配置示例：`test-form-express.json`
- 相关表达式文件：`test_select_express.txt`、`form_init_express.txt` 等

可以参考这些示例文件了解具体用法。

## 相关类

- `ExpressFileLoader` - 表达式文件加载工具类
- `ConfigFileLoader` - 配置文件加载工具类
- `ListController` - 列表控制器（已支持表达式文件引用）
- `FormController` - 表单控制器（已支持表达式文件引用）