# DSL 双数据源功能测试指南

## 📋 测试准备

### 1. 数据库准备

执行测试SQL脚本，插入测试数据：

```bash
# 找到SQL脚本
cat src/test/resources/test_dsl_dual_source.sql

# 在数据库中执行该脚本
# 或通过数据库客户端（Navicat、DBeaver等）执行
```

### 2. 测试数据说明

#### 文件版配置（已创建）
- **表单**: `test-file-form.json` - 存储在 `src/main/resources/topezadmin/config/layui/dsl/form/`
- **列表**: `test-file-list.json` - 存储在 `src/main/resources/topezadmin/config/layui/dsl/list/`

#### 数据库版配置（需执行SQL）
- **表单**: `test-db-form` - 存储在 `T_EZADMIN_FORM` 表
- **列表**: `test-db-list` - 存储在 `T_EZADMIN_LIST` 表

## 🧪 测试用例

### 测试1：从文件加载Form DSL ✅
- **测试目标**: 验证文件加载功能
- **预期结果**: 成功加载，source='file'
- **无需前提**: 文件已存在

### 测试2：从文件加载List DSL ✅
- **测试目标**: 验证文件加载功能
- **预期结果**: 成功加载，source='file'
- **无需前提**: 文件已存在

### 测试3：从数据库加载Form DSL ⚠️
- **测试目标**: 验证数据库降级功能
- **预期结果**: 成功加载，source='database'
- **前提条件**: 需要执行test_dsl_dual_source.sql

### 测试4：从数据库加载List DSL ⚠️
- **测试目标**: 验证数据库降级功能
- **预期结果**: 成功加载，source='database'
- **前提条件**: 需要执行test_dsl_dual_source.sql

### 测试5：文件优先策略 ✅
- **测试目标**: 验证文件优先于数据库
- **预期结果**: 优先加载文件，source='file'
- **无需前提**: 使用test-file-form

### 测试6：不存在的DSL ✅
- **测试目标**: 验证不存在时返回null
- **预期结果**: 返回null
- **无需前提**: 使用不存在的ID

### 测试7：双写保存DSL ⚠️
- **测试目标**: 验证同时保存到文件和数据库
- **预期结果**: 文件和数据库都保存成功
- **前提条件**: 需要写权限和数据库连接

## 🚀 执行测试

### 方式1：Maven命令（推荐）

```bash
# 编译项目
mvn clean compile -DskipTests

# 执行测试类
mvn test -Dtest=DslDualSourceTest

# 或执行单个测试方法
mvn test -Dtest=DslDualSourceTest#testLoadFormFromFile
```

### 方式2：直接运行main方法

```bash
# 进入项目目录
cd ezadmin-common

# 编译
mvn compile

# 运行测试
mvn exec:java -Dexec.mainClass="top.ezadmin.service.DslDualSourceTest"
```

### 方式3：IDE运行
1. 打开 `DslDualSourceTest.java`
2. 右键 → Run 'DslDualSourceTest.main()'

## 📊 测试结果解读

### 成功示例
```
========== 测试1：从文件加载Form DSL ==========
✅ 测试通过：成功从文件加载Form DSL
   - ID: test-file-form
   - 标题: 测试表单(文件版)
   - 数据源: file
```

### 警告示例（需要执行SQL）
```
========== 测试3：从数据库加载Form DSL ==========
⚠️  前提：需要先执行test_dsl_dual_source.sql脚本
⚠️  警告：未找到配置，请确保已执行SQL脚本插入数据
```

### 失败示例
```
❌ 测试失败：Connection refused
   可能原因：
   1. 未执行test_dsl_dual_source.sql脚本
   2. 数据库表T_EZADMIN_FORM不存在
   3. 数据源配置错误
```

## 🔧 常见问题

### Q1: 测试3和测试4失败，提示"未找到配置"
**原因**: 未执行SQL脚本插入数据库测试数据
**解决**: 执行 `src/test/resources/test_dsl_dual_source.sql`

### Q2: 所有数据库相关测试失败，提示"Connection refused"
**原因**: 数据库未启动或连接配置错误
**解决**: 检查数据库连接配置和状态

### Q3: 测试7保存失败，提示"Permission denied"
**原因**: 没有文件写入权限
**解决**: 检查项目目录权限

### Q4: 表T_EZADMIN_FORM或T_EZADMIN_LIST不存在
**原因**: 数据库表未创建
**解决**: 执行DDL脚本创建表（见上文提供的建表语句）

## 📁 测试文件位置

```
ezadmin-common/
├── src/
│   ├── main/
│   │   └── resources/
│   │       └── topezadmin/config/layui/dsl/
│   │           ├── form/
│   │           │   └── test-file-form.json        # 测试表单（文件版）
│   │           └── list/
│   │               └── test-file-list.json        # 测试列表（文件版）
│   └── test/
│       ├── java/
│       │   └── top/ezadmin/service/
│       │       └── DslDualSourceTest.java         # 测试类
│       └── resources/
│           └── test_dsl_dual_source.sql           # 测试SQL脚本
```

## ✨ 测试覆盖

- ✅ 文件加载（表单）
- ✅ 文件加载（列表）
- ✅ 数据库加载（表单）
- ✅ 数据库加载（列表）
- ✅ 文件优先策略
- ✅ 降级策略（文件→数据库）
- ✅ 不存在处理
- ✅ 双写保存

## 🎯 下一步

测试通过后，可以：
1. 在实际应用中访问 `/topezadmin/form/page-test-file-form`
2. 在实际应用中访问 `/topezadmin/list/page-test-db-list`
3. 使用AI修改DSL，验证双写功能
4. 删除文件，验证数据库降级功能

"menu": {
"item_name": "CATEGORY_ID",
"label": "分类",
"alias": "A",
"initData": {
"dataSource": "dataSource",
"dataSql": "SELECT ID value, CATEGORY_NAME label FROM  T_DOC_CATEGORY WHERE DELETE_FLAG=0"
}
}
