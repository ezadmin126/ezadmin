# date 组件

## 组件说明

日期时间选择器，基于 Layui 日期组件实现，支持日期、时间、日期时间、年、月、时间段等多种选择模式。

## 使用场景

- 出生日期选择
- 创建/修改时间选择
- 有效期选择
- 任何需要日期时间输入的场景

## 基本配置

```json
{
  "item_name": "birthday",
  "label": "出生日期",
  "component": "date",
   "classAppend":  "layui-col-md8",
  "props": {
    "type": "date",
     "validate": { //jquery validate 使用 validate
        "rule": {
           "required": true
        },
        "message": {
           "range": "请选择日期"
        }
     }
  }
}
```

## 配置项说明

### 基础属性

| 属性          | 类型     | 必填     | 说明                               |
|-------------|--------|--------|----------------------------------|
| item_name   | String | 是      | 字段名，对应数据库字段                      |
| label       | String | 是      | 显示标签                             |
| component   | String | 是      | 固定值：`date`                       |
| operator    | String | 否      | 查询操作符，如 `eq`、`ge`、`le`、`between` |
| description | String | 字段说明文本 | `"可选择多个"`                        |
| props       | Object | 否      | 组件属性,支持所有layui date属性，html5属性    |
| classAppend | String | 否      | layui-col-md8 主要使用layui布局设置占用宽度  |

### props 属性

| 属性          | 类型      | 说明       | 示例                                      | 默认值        |
|-------------|---------|----------|-----------------------------------------|------------|
| type        | String  | 日期类型     | `date`、`datetime`、`year`、`month`、`time` | `date`     |
| range       | Boolean | 是否开启范围选择 | `true`                                  | `false`    |
| format      | String  | 显示格式     | `yyyy-MM-dd`                            | 根据type自动设置 |
| placeholder | String  | 提示文本     | `"请选择日期"`                               | 根据type自动设置 |
| lay-verify  | String  | 表单验证规则   | `"required"`                            | -          |
| min         | String  | 最小日期     | `"2020-01-01"`                          | -          |
| max         | String  | 最大日期     | `"2099-12-31"`                          | -          |
| disabled    | Boolean | 是否禁用     | `true`                                  | `false`    |
| description | String  | 字段说明文本   | `"请选择出生日期"`                             | -          |

### type 类型说明

| 类型       | 说明      | 格式示例                |
|----------|---------|---------------------|
| year     | 年选择器    | 2024                |
| month    | 年月选择器   | 2024-01             |
| date     | 日期选择器   | 2024-01-01          |
| time     | 时间选择器   | 12:30:00            |
| datetime | 日期时间选择器 | 2024-01-01 12:30:00 |

## 完整示例

### 日期选择

```json
{
  "item_name": "birthday",
  "label": "出生日期",
  "component": "date",
   "classAppend":  "layui-col-md8",
  "props": {
    "type": "date",
    "placeholder": "请选择出生日期",
    "lay-verify": "required"
  }
}
```

### 日期时间选择

```json
{
  "item_name": "publish_time",
  "label": "发布时间",
  "component": "date",
   "classAppend":  "layui-col-md8",
  "props": {
    "type": "datetime",
    "placeholder": "请选择发布时间",
    "lay-verify": "required"
  }
}
```

### 年份选择

```json
{
  "item_name": "graduation_year",
  "label": "毕业年份",
  "component": "date",
   "classAppend":  "layui-col-md8",
  "props": {
    "type": "year",
    "placeholder": "请选择年份"
  }
}
```

### 年月选择

```json
{
  "item_name": "month",
  "label": "月份",
  "component": "date",
   "classAppend":  "layui-col-md8",
  "props": {
    "type": "month",
    "placeholder": "请选择月份"
  }
}
```

### 时间选择

```json
{
  "item_name": "work_time",
  "label": "上班时间",
  "component": "date",
   "classAppend":  "layui-col-md8",
  "props": {
    "type": "time",
    "placeholder": "请选择时间"
  }
}
```

### 日期范围选择

```json
{
  "item_name": "date_range",
  "label": "日期范围",
  "component": "date",
   "classAppend":  "layui-col-md8",
  "props": {
    "type": "date",
    "range": true,
    "placeholder": "请选择日期范围"
  }
}
```

### 限制日期范围

```json
{
  "item_name": "effective_date",
  "label": "生效日期",
  "component": "date",
   "classAppend":  "layui-col-md8",
  "props": {
    "type": "date",
    "min": "2024-01-01",
    "max": "2024-12-31",
    "placeholder": "请选择日期"
  }
}
```

### 搜索项中使用

```json
{
  "search": [{
    "item_name": "create_time",
    "label": "创建时间",
    "component": "date",
    "operator": "between",
    "props": {
      "type": "date",
      "range": true
    }
  }, {
    "item_name": "birthday",
    "label": "出生日期",
    "component": "date",
    "operator": "eq",
    "props": {
      "type": "date"
    }
  }]
}
```

### 完整表单示例

```json
{
  "id": "employee-form",
  "name": "员工表单",
  "dataSource": "dataSource",
  "cardList": [{
    "type": "card",
    "label": "基本信息",
     "classAppend":"layui-col-md12",
    "fieldList": [{
      "item_name": "name",
      "label": "姓名",
      "component": "input",
      "classAppend":  "layui-col-md8",
      "props": {
        "placeholder": "请输入姓名",
        "lay-verify": "required"
      }
    }, {
      "item_name": "gender",
      "label": "性别",
      "component": "radio",
      "classAppend":  "layui-col-md8",
      "initData": {
        "dataJson": [
          {"label": "男", "value": "1"},
          {"label": "女", "value": "2"}
        ]
      }
    }, {
      "item_name": "birthday",
      "label": "出生日期",
      "component": "date",
      "classAppend":  "layui-col-md8",
      "props": {
        "type": "date",
        "placeholder": "请选择出生日期",
        "max": "2010-12-31",
        "lay-verify": "required"
      }
    }, {
      "item_name": "join_date",
      "label": "入职日期",
      "component": "date",
      "classAppend":  "layui-col-md8",
      "props": {
        "type": "date",
        "placeholder": "请选择入职日期",
        "lay-verify": "required"
      }
    }, {
      "item_name": "contract_start_time",
      "label": "合同开始时间",
      "component": "date",
      "classAppend":  "layui-col-md8",
      "props": {
        "type": "datetime",
        "placeholder": "请选择开始时间"
      }
    }, {
      "item_name": "contract_end_time",
      "label": "合同结束时间",
      "component": "date",
      "classAppend":  "layui-col-md8",
      "props": {
        "type": "datetime",
        "placeholder": "请选择结束时间"
      }
    }, {
      "item_name": "work_time",
      "label": "上班时间",
      "component": "date",
      "classAppend":  "layui-col-md8",
      "props": {
        "type": "time",
        "placeholder": "请选择上班时间"
      }
    }, {
      "item_name": "off_work_time",
      "label": "下班时间",
      "component": "date",
      "classAppend":  "layui-col-md8",
      "props": {
        "type": "time",
        "placeholder": "请选择下班时间"
      }
    }]
  }],
  "initExpress": ["return selectOne('SELECT * FROM employees WHERE id=${ID}')"],
  "submitExpress": [
    "if(!isNotBlank('id')) {",
    "  return insert('INSERT INTO employees(name,gender,birthday,join_date,contract_start_time,contract_end_time,work_time,off_work_time,create_time) VALUES(#{name},#{gender},#{birthday},#{join_date},#{contract_start_time},#{contract_end_time},#{work_time},#{off_work_time},NOW())')",
    "} else {",
    "  return update('UPDATE employees SET name=#{name},gender=#{gender},birthday=#{birthday},join_date=#{join_date},contract_start_time=#{contract_start_time},contract_end_time=#{contract_end_time},work_time=#{work_time},off_work_time=#{off_work_time},update_time=NOW() WHERE id=#{id}')",
    "}"
  ]
}
```

## 格式化说明

### 日期格式

| format      | 示例          |
|-------------|-------------|
| yyyy-MM-dd  | 2024-01-01  |
| yyyy/MM/dd  | 2024/01/01  |
| yyyy年MM月dd日 | 2024年01月01日 |
| yyyyMMdd    | 20240101    |

### 时间格式

| format   | 示例       |
|----------|----------|
| HH:mm:ss | 12:30:00 |
| HH:mm    | 12:30    |
| HHmmss   | 123000   |

### 日期时间格式

| format              | 示例                  |
|---------------------|---------------------|
| yyyy-MM-dd HH:mm:ss | 2024-01-01 12:30:00 |
| yyyy-MM-dd HH:mm    | 2024-01-01 12:30    |
| yyyy/MM/dd HH:mm:ss | 2024/01/01 12:30:00 |

## 注意事项

1. **数据库字段类型**：
    - date类型：DATE
    - datetime类型：DATETIME
    - time类型：TIME
    - year类型：INT 或 VARCHAR
    - month类型：VARCHAR

2. **格式匹配**：props.format 必须与数据库存储格式匹配

3. **范围选择**：开启 range 时，返回的值格式为 "开始日期 - 结束日期"

4. **查询操作符**：
    - 单个日期：`eq`、`ge`、`le`
    - 日期范围：`between`

5. **默认值**：可在 initExpress 中设置默认值，格式要与 type 匹配

6. **时区问题**：注意服务器与客户端的时区差异

## 常见问题

### 如何设置默认为当前日期？

在 initExpress 中设置：

```javascript
{
  "initExpress": [
    "map = new HashMap();",
    "map.put('create_date', new SimpleDateFormat('yyyy-MM-dd').format(new Date()));",
    "return map;"
  ]
}
```

### 日期范围查询如何处理？

搜索项配置：

```json
{
  "item_name": "create_time",
  "operator": "between",
  "props": {
    "range": true
  }
}
```

系统会自动将范围值拆分为开始和结束时间。

### 如何限制只能选择今天之后的日期？

```json
{
  "props": {
    "min": "0"  // 0表示今天
  }
}
```

或使用具体日期：

```json
{
  "props": {
    "min": "2024-01-01"
  }
}
```

### 日期格式不正确？

检查：

1. props.format 是否设置正确
2. 数据库字段类型是否匹配
3. 初始化数据的格式是否与 format 一致

## 相关组件

- [input](./input.md) - 单行文本输入
