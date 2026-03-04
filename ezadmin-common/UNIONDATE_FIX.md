# uniondate 组件 customSearch 修复说明

## 问题描述

在 `listTemplate.html` 的 `mergeWhereParams` 方法中，uniondate 组件生成 customSearch 的逻辑存在以下问题：

1. **语法错误**: `customSearchItem.c.s=items;` - `c` 属性未初始化就被使用
2. **数据结构不匹配**: 生成的JSON结构不符合 `CustomSearchDTO` 的定义

## 修复内容

### 1. JavaScript 代码修复 (listTemplate.html:679-736)

**修复前的问题代码：**
```javascript
var customSearchItem= {};
var items=[];
// ... 构建 items 数组
customSearchItem.c.s=items;  // ❌ c 未初始化
customSearchItem.t="AND";
customSearch.g.push(customSearchItem);
```

**修复后的正确代码：**
```javascript
// 创建分组对象 (CustomSearchGroup)
var customSearchGroup = {};
customSearchGroup.t = "AND";
customSearchGroup.c = [];

// 创建子条件对象 (CustomSearchDTO)
var childSearchDTO = {};
childSearchDTO.s = [];

// 根据select是否有选中值，生成不同的条件
if(select.val() == ""){
    // select没有选中，遍历所有option生成OR连接的条件
    var firstItem = true;
    select.find("option").each(function(){
        var optionValue = $(this).attr("value");
        if(optionValue == "" || optionValue == undefined) return;

        var singleCondition = {};
        singleCondition.f = optionValue;
        singleCondition.o = "BETWEEN";
        singleCondition.t = firstItem ? "AND" : "OR";
        singleCondition.v = input.val();
        childSearchDTO.s.push(singleCondition);
        firstItem = false;
    });
} else {
    // select有选中值，只生成单个字段的查询条件
    var singleCondition = {};
    singleCondition.f = select.val();
    singleCondition.o = "BETWEEN";
    singleCondition.t = "AND";
    singleCondition.v = input.val();
    childSearchDTO.s.push(singleCondition);
}

if(childSearchDTO.s.length > 0){
    customSearchGroup.c.push(childSearchDTO);
    customSearch.g.push(customSearchGroup);
}
```

### 2. Java 代码修复 (SqlGenerate.java:385-422)

为了支持 BETWEEN 操作符处理日期范围值（格式：`2024-01-01 - 2024-12-31`），在 `customWhere` 方法中添加了自动分割逻辑：

```java
String value = StringUtils.safeDb(s.get(i).getV());
String valueS = "";
String valueE = "";
String operator = StringUtils.safeDb(s.get(i).getO());

// 如果操作符是 BETWEEN 且值包含 " - "，则分割为开始和结束值
if ("BETWEEN".equalsIgnoreCase(operator) && value.contains(" - ")) {
    String[] valueSplit = value.split(" - ");
    if (valueSplit.length == 2) {
        valueS = valueSplit[0].trim();
        valueE = valueSplit[1].trim();
        value = "";  // 清空 value，使用 valueS 和 valueE
    }
}

String w = transOneFileSql(type, alias, field, value, valueS, valueE, jdbctype, operator, false);
```

## 数据结构说明

### CustomSearchDTO 结构
```java
public class CustomSearchDTO {
    private List<CustomSearchGroup> g;  // 分组列表
    private List<CustomSearchSingle> s;  // 单条件列表
    private List<CustomSearchOrder> o;   // 排序列表
}
```

### CustomSearchGroup 结构
```java
public class CustomSearchGroup {
    private String t;                    // 连接类型: AND/OR
    private List<CustomSearchDTO> c;     // 子条件列表
}
```

### CustomSearchSingle 结构
```java
public class CustomSearchSingle {
    private String t;  // 连接类型: AND/OR
    private String f;  // 字段名
    private String o;  // 操作符: BETWEEN/EQ/LIKE等
    private String v;  // 值
}
```

## 生成的JSON示例

### 场景1：select 有选中值
用户选择了 "create_time" 字段，输入日期范围 "2024-01-01 - 2024-12-31"

**生成的JSON：**
```json
{
  "g": [{
    "t": "AND",
    "c": [{
      "s": [{
        "f": "create_time",
        "o": "BETWEEN",
        "t": "AND",
        "v": "2024-01-01 - 2024-12-31"
      }]
    }]
  }]
}
```

**生成的SQL：**
```sql
(    (create_time >=  2024-01-01create_time <=  2024-12-31 )   )
```

### 场景2：select 没有选中值
用户没有选择字段，可选字段有：create_time, update_time, delete_time
输入日期范围 "2024-01-01 - 2024-12-31"

**生成的JSON：**
```json
{
  "g": [{
    "t": "AND",
    "c": [{
      "s": [
        {
          "f": "create_time",
          "o": "BETWEEN",
          "t": "AND",
          "v": "2024-01-01 - 2024-12-31"
        },
        {
          "f": "update_time",
          "o": "BETWEEN",
          "t": "OR",
          "v": "2024-01-01 - 2024-12-31"
        },
        {
          "f": "delete_time",
          "o": "BETWEEN",
          "t": "OR",
          "v": "2024-01-01 - 2024-12-31"
        }
      ]
    }]
  }]
}
```

**生成的SQL：**
```sql
(    (create_time >=  2024-01-01create_time <=  2024-12-31  OR update_time >=  2024-01-01 OR update_time <=  2024-12-31  OR delete_time >=  2024-01-01 OR delete_time <=  2024-12-31 )   )
```

### 场景3：多个 uniondate 组件
两个组件：
1. 组件1选中 "create_time"，范围 "2024-01-01 - 2024-06-30"
2. 组件2未选中，可选 update_time/delete_time，范围 "2024-07-01 - 2024-12-31"

**生成的JSON：**
```json
{
  "g": [
    {
      "t": "AND",
      "c": [{
        "s": [{
          "f": "create_time",
          "o": "BETWEEN",
          "t": "AND",
          "v": "2024-01-01 - 2024-06-30"
        }]
      }]
    },
    {
      "t": "AND",
      "c": [{
        "s": [
          {
            "f": "update_time",
            "o": "BETWEEN",
            "t": "AND",
            "v": "2024-07-01 - 2024-12-31"
          },
          {
            "f": "delete_time",
            "o": "BETWEEN",
            "t": "OR",
            "v": "2024-07-01 - 2024-12-31"
          }
        ]
      }]
    }
  ]
}
```

## 测试覆盖

创建了 `UniondateCustomSearchTest.java` 包含5个测试用例：

1. ✅ `testUniondateWithSelectedField` - 测试 select 有选中值的情况
2. ✅ `testUniondateWithoutSelectedField` - 测试 select 没有选中值的情况
3. ✅ `testMultipleUniondateComponents` - 测试多个 uniondate 组件
4. ✅ `testSqlGenerationWithSelectedField` - 测试SQL生成（单字段）
5. ✅ `testSqlGenerationWithoutSelectedField` - 测试SQL生成（多字段OR连接）

所有测试均通过：
```
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 使用示例

```html
<!-- uniondate 组件示例 -->
<div component="uniondate" item_name="date_search">
    <select>
        <option value="">请选择</option>
        <option value="create_time">创建时间</option>
        <option value="update_time">更新时间</option>
        <option value="delete_time">删除时间</option>
    </select>
    <input type="text" value="2024-01-01 - 2024-12-31" class="ez-date">
</div>
```

当用户填写日期并点击搜索时：
- 如果**选中了**"创建时间"，则只查询 create_time 字段
- 如果**未选中**任何选项，则查询所有三个字段（create_time OR update_time OR delete_time）
