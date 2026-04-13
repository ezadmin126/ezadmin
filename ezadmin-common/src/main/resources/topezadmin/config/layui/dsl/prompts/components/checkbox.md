# checkbox 组件

## 组件说明

复选框，用于从多个选项中选择多个值，所有选项平铺展示。

## 使用场景

- 兴趣爱好选择
- 权限勾选
- 特性开关
- 任何多选场景（选项数量3-8个为宜）

## 基本配置

```json
{
  "item_name": "hobbies",
  "label": "兴趣爱好",
  "component": "checkbox",
  "col": 12,
  "initData": {
    "dataJson": [
      {"label": "读书", "value": "1"},
      {"label": "运动", "value": "2"},
      {"label": "旅游", "value": "3"}
    ]
  }
}
```

## 配置项说明

### 基础属性

| 属性 | 类型 | 必填 | 说明                                  |
|------|------|------|-------------------------------------|
| item_name | String | 是 | 字段名，对应数据库字段                         |
| label | String | 是 | 显示标签                                |
| component | String | 是 | 固定值：`checkbox`                      |
| operator | String | 否 | 查询操作符，建议使用 `in`                     |
| initData | Object | 是 | 数据源配置                               |
| description | String | 字段说明文本 | `"可选择多个"`                           |
| props | Object | 否 | 组件属性,支持所有layui  checkbox 属性，html5属性 |
| classAppend | String | 否 | layui-col-md8 主要使用layui布局设置占用宽度                       |

### initData 配置

**静态数据（dataJson）：**
```json
{
  "initData": {
    "dataJson": [
      {"label": "选项1", "value": "1"},
      {"label": "选项2", "value": "2"}
    ]
  }
}
```

**动态数据（dataSql）：**
```json
{
  "initData": {
    "dataSource": "dataSource",
    "dataSql": "SELECT id value, name label FROM table_name"
  }
}
```

### props 属性

| 属性 | 类型 | 说明 | 示例 |
|------|------|------|------|
| lay-verify | String | Layui表单验证规则 | `"required"` |
| disabled | Boolean | 是否禁用 | `true` |
| required | Boolean | 是否必填(显示红色*) | `true` |
| description | String | 字段说明文本 | `"可多选"` |

## 完整示例

### 兴趣爱好选择

```json
{
  "item_name": "hobbies",
  "label": "兴趣爱好",
  "component": "checkbox",
  "classAppend":  "layui-col-md12",
  "initData": {
    "dataJson": [
      {"label": "读书", "value": "reading"},
      {"label": "运动", "value": "sports"},
      {"label": "旅游", "value": "travel"},
      {"label": "音乐", "value": "music"},
      {"label": "摄影", "value": "photography"}
    ]
  },
  "props": {
    "description": "可选择多项",
    "validate": { //jquery validate 使用 validate
      "rule": {
        "range": [
          0,
          9999
        ]
      },
      "message": {
        "range": "请输入0-9999之间的数字"
      }
    }
  }
}
```

### 权限勾选

```json
{
  "item_name": "permissions",
  "label": "权限",
  "component": "checkbox",
  "classAppend":  "layui-col-md12",
  "initData": {
    "dataSource": "dataSource",
    "dataSql": "SELECT id value, permission_name label FROM permissions WHERE module='user' AND status=1"
  },
  "props": {
    "lay-verify": "required",
    "description": "请至少选择一项权限"
  }
}
```

### 功能开关

```json
{
  "item_name": "features",
  "label": "启用功能",
  "component": "checkbox",
  "classAppend":  "layui-col-md12",
  "initData": {
    "dataJson": [
      {"label": "邮件通知", "value": "email_notify"},
      {"label": "短信通知", "value": "sms_notify"},
      {"label": "站内消息", "value": "site_message"},
      {"label": "微信推送", "value": "wechat_push"}
    ]
  }
}
```

### 搜索项中使用

```json
{
  "search": [{
    "item_name": "hobbies",
    "label": "兴趣",
    "component": "checkbox",
    "operator": "in",
    "initData": {
      "dataJson": [
        {"label": "读书", "value": "reading"},
        {"label": "运动", "value": "sports"},
        {"label": "旅游", "value": "travel"}
      ]
    }
  }]
}
```

### 完整表单示例

```json
{
  "id": "user-profile-form",
  "name": "用户资料",
  "dataSource": "dataSource",
  "cardList": [{
    "type": "card",
    "label": "基本信息",
    "classAppend":  "layui-col-md12",
    "fieldList": [{
      "item_name": "username",
      "label": "用户名",
      "component": "input",
      "classAppend":  "layui-col-md8",
      "props": {
        "placeholder": "请输入用户名",
        "lay-verify": "required"
      }
    }, {
      "item_name": "nickname",
      "label": "昵称",
      "component": "input",
      "classAppend":  "layui-col-md8",
      "props": {
        "placeholder": "请输入昵称"
      }
    }, {
      "item_name": "hobbies",
      "label": "兴趣爱好",
      "component": "checkbox",
      "classAppend":  "layui-col-md12",
      "initData": {
        "dataJson": [
          {"label": "读书", "value": "reading"},
          {"label": "运动", "value": "sports"},
          {"label": "旅游", "value": "travel"},
          {"label": "音乐", "value": "music"},
          {"label": "摄影", "value": "photography"},
          {"label": "美食", "value": "food"}
        ]
      },
      "props": {
        "description": "选择你的兴趣爱好，可多选"
      }
    }, {
      "item_name": "notification_types",
      "label": "通知方式",
      "component": "checkbox",
      "classAppend":  "layui-col-md12",
      "initData": {
        "dataJson": [
          {"label": "邮件", "value": "email"},
          {"label": "短信", "value": "sms"},
          {"label": "站内信", "value": "site"}
        ]
      },
      "props": {
        "lay-verify": "required",
        "description": "至少选择一种通知方式"
      }
    }]
  }],
  "initExpress": [
    "map = selectOne('SELECT * FROM users WHERE id=${ID}');",
    "if(!map) {",
    "  map = new HashMap();",
    "  map.put('notification_types', 'email,site');",  // 默认选中
    "}",
    "return map;"
  ],
  "submitExpress": [
    "if(!isNotBlank('id')) {",
    "  return insert('INSERT INTO users(username,nickname,hobbies,notification_types,create_time) VALUES(#{username},#{nickname},#{hobbies},#{notification_types},NOW())')",
    "} else {",
    "  return update('UPDATE users SET username=#{username},nickname=#{nickname},hobbies=#{hobbies},notification_types=#{notification_types},update_time=NOW() WHERE id=#{id}')",
    "}"
  ]
}
```

## 数据格式

### 存储格式

选中的值以逗号分隔的字符串形式存储：
```
"reading,sports,travel"
```

### 初始化格式

编辑时需要返回逗号分隔的字符串：
```javascript
{
  "initExpress": [
    "map = selectOne('SELECT * FROM users WHERE id=${ID}');",
    "// hobbies 字段格式：'reading,sports,travel'",
    "return map;"
  ]
}
```

## 监听选择事件

可以通过 Layui 的表单监听实现联动：

```javascript
// 监听复选框变化
form.on('checkbox(lay-filter-hobbies)', function(data){
  console.log('当前项是否选中：', data.elem.checked);
  console.log('当前项的值：', data.value);
  console.log('当前项的文本：', data.elem.title);

  // 获取所有选中的值
  var values = [];
  $('input[name="hobbies"]:checked').each(function(){
    values.push($(this).val());
  });
  console.log('所有选中的值：', values);
});
```

## 注意事项

1. **选项数量**：复选框适合3-8个选项，选项过多建议使用 select-multiple 组件
2. **数据格式**：选项数据必须包含 `label` 和 `value` 字段
3. **存储格式**：选中的值以逗号分隔的字符串存储（如："1,2,3"）
4. **数据库字段**：建议使用 VARCHAR 类型，长度根据实际需要设置
5. **默认值**：可在 initExpress 中设置默认选中项，格式为逗号分隔的字符串
6. **必填验证**：使用 `lay-verify: "required"` 要求至少选中一项
7. **SQL查询**：dataSql中必须使用别名 `value` 和 `label`

## 与其他组件的选择

| 场景 | 推荐组件 | 原因 |
|------|---------|------|
| 3-8个选项 | checkbox | 所有选项一目了然 |
| 8个以上选项 | select-multiple | 节省空间，支持搜索 |
| 权限勾选 | checkbox | 更直观 |
| 标签选择 | select-multiple | 选项较多，支持搜索 |

## 常见问题

### 如何设置默认选中？

在 initExpress 中设置逗号分隔的字符串：
```javascript
{
  "initExpress": [
    "map = new HashMap();",
    "map.put('hobbies', 'reading,sports');",  // 默认选中读书和运动
    "return map;"
  ]
}
```

### 如何获取所有选中的值？

使用 jQuery 获取：
```javascript
var values = [];
$('input[name="hobbies"]:checked').each(function(){
  values.push($(this).val());
});
console.log(values.join(','));
```

### 选项不显示？

检查：
1. initData 配置是否正确
2. 数据格式是否包含 label 和 value
3. SQL查询是否有数据返回

### 如何实现全选/取消全选？

可以添加自定义按钮：
```javascript
// 全选
$('input[name="hobbies"]').prop('checked', true);
form.render('checkbox');

// 取消全选
$('input[name="hobbies"]').prop('checked', false);
form.render('checkbox');
```

### 如何限制选择数量？

通过监听实现：
```javascript
form.on('checkbox(lay-filter-hobbies)', function(data){
  var checkedCount = $('input[name="hobbies"]:checked').length;
  if(checkedCount > 3) {
    data.elem.checked = false;
    form.render('checkbox');
    layer.msg('最多只能选择3项');
  }
});
```

## 相关组件

- [radio](./radio.md) - 单选框
- [select-multiple](./select-multiple.md) - 多选下拉（选项较多时使用）
