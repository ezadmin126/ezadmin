### 组件类型
组件如果有invalidate-tips class. 则在jquery validate验证不通过时，会额外弹框展示错误信息
**输入组件**
- `input` - 单行文本
- `textarea` - 多行文本
- `hidden` - 隐藏域
- `tinymce` - 富文本编辑器

 
**选择组件**
- `select` - 下拉框
- `select-multiple` - 多选下拉 第三方插件xm-select
- `cascader` - 级联选择 第三方插件lay-cascader
- `radio` - 单选框
- `checkbox` - 复选框


```json
{
  "item_name": "REGION_ID", //确保大写
  "label": "地区",
  "component": "cascader",
  "classAppend": "layui-col-md8",
  "initData": {
    "dataJson": [
      {
        "label": "北京市",
        "value": "110000",
        "children": [
          {"label": "东城区", "value": "110101"},
          {"label": "西城区", "value": "110102"}
        ]
      }
    ]
  },
  "props": {// Layui原生属性 或自定义属性
      "placeholder": "0-9999",
      "type": "number",
      "step": "1",
      "min": "0",
      "lay-affix": "number",
      "maxlength": "4",
      "description": "- 前台展示的浏览次数=预置浏览次数+用户实际浏览次数。",
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

 
**其他组件**
- `date` - 日期时间
- `upload` - 文件上传

### validate 验证
在props里面添加validate属性，支持jquery validate的rule和message属性
如：
```json
  "validate": {
    "rule": {
      "required": true,
      "email": true
    },
    "message": {
      "required": "邮箱不能为空",
      "email": "邮箱格式错误"
    }
  }
```            

**按钮组件**
- `button-normal` - 普通按钮 ，如编辑，删除
- `button-toolbar` - 工具栏按钮，如新增
- `button-dropdown` - 下拉按钮
- `button-bread` - 面包屑按钮
- `button-span` - 跨度按钮
```json
{
  "item_name": "edit",
  "label": "编辑",
  "component": "button-normal",
  "props": {
    "opentype": "MODAL",
    "windowname": "编辑",
    "url": "/topezadmin/form/page-user-form?id={{=d.id}}",
    "area": ["50%", "100%"] //支持layer格式
  }
}
```
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




**表格展示组件**
- `tdText` - 文本
- `tdPic` - 图片展示
- `tdLink` - 链接
- `tdSelect` - 下拉展示
- `tdSelectMultiple` - 多选展示
- `tdCascader` - 级联展示

### 组件属性
| 属性 | 类型 | 必填 | 说明                                                    |
|------|------|------|-------------------------------------------------------|
| item_name | String | 是 | 字段名，对应数据库字段                                           |
| label | String | 是 | 显示标签                                                  |
| component | String | 是 | 固定值：`select-multiple`                                 |
| classAppend | String | 否 | layui-col-md8 主要使用layui布局设置占用宽度                       |
| operator | String | 否 | 查询操作符，建议使用 `in`                                       |
| initData | Object | 是 | 数据源配置  dataJson 直接配置json, dataSql，配置返回label/value的sql |
| props | Object | 否 | 额外属性配置   支持所有html5/layui/xmselelct/lay-cascader的属性    |



### 按钮属性
```json
{
  "props": {
    "opentype": "MODAL",        // 打开方式
    "windowname": "窗口标题",
    "url": "/path/to/page",
    "area": ["50%", "100%"],          // 窗口大小
    "display": "显示条件"       // laytpl表达式
     
  }
}
```
 