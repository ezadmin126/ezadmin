echo 'export GPG_TTY=$(tty)' >> ~/.bashrc
source ~/.bashrc

# EzAdmin框架使用规则

## 基本概念

1. EzAdmin是一个通过配置生成后台管理系统的框架
2. 通过HTML配置文件可以生成列表和表单页面
3. 支持多种插件类型，适用于不同场景
4. 可以通过SQL直接生成列表和表单配置
5. 配置文件保存在topezadmin/config/layui/目录下


## 列表生成规则

1. 列表配置文件保存在topezadmin/config/layui/list/目录下
2. 列表通过<body>标签的id属性确定唯一标识
3. 列表URL格式为/topezadmin/list/list-{listcode}
4. 列表可以包含搜索区域、按钮区域、表格区域
5. 列表数据通过express标签中的SQL语句获取
6. 支持分页、排序、导出等功能


## 表单生成规则

1. 表单配置文件保存在topezadmin/config/layui/form/目录下
2. 表单通过<body>标签的id属性确定唯一标识
3. 表单URL格式为/topezadmin/form/form-{formcode}
4. 表单可以包含多个card区域，每个区域可以有多个表单项
5. 表单数据通过init_express标签中的SQL语句获取
6. 表单提交通过submit_express标签中的代码处理


## SQL生成列表/表单规则

1. 通过/topezadmin/listEdit/importSql-访问SQL导入界面
2. 输入SQL语句后，系统会解析SQL中的字段
3. 系统会自动为每个字段生成对应的配置项
4. 可以修改字段的显示名称、控件类型、搜索条件等
5. 生成的列表/表单配置会保存到对应目录
6. 支持预览和修改生成的配置


## 插件使用规则

1. 列表和表单都支持多种插件类型
2. 插件通过type属性指定，如input-text、select-search
3. 插件的数据源可以是静态数据，也可以是SQL查询结果
4. 插件可以有多种属性，如item_name、jdbctype、oper等
5. 插件的属性决定了它的行为和显示方式
6. 插件的HTML代码保存在plugins目录下


## 表达式使用规则

1. 表达式用于获取数据、处理提交等
2. 表达式使用Java代码，支持各种Java语法
3. 常见表达式包括init_express、submit_express、delete_express等
4. 表达式中可以使用特殊函数如$()、$$()、search()等
5. $()用于获取请求参数，$$()用于获取会话参数
6. search()用于执行SQL查询并自动处理条件


## 配置项使用规则

1. item_name：字段名称，与SQL查询结果对应
2. type：控件类型，如input-text、select-search
3. alias：SQL中的表别名
4. oper：搜索操作符，如EQ、LIKE、BETWEEN
5. jdbctype：数据库字段类型，如VARCHAR、DATETIME
6. body：单元格内容显示方式，如td-text、td-link
7. data：控件数据源，可以是SQL语句或静态数据


## 页面布局规则

1. 页面布局通过col属性控制，基于layui的栅格系统
2. 列表可以设置固定列、行按钮、表头等
3. 表单可以分为多个card区域，每个区域有标题和内容
4. 可以通过append_head和append_foot添加自定义代码
5. 支持响应式布局，适应不同屏幕大小
6. 可以设置tab实现多标签页切换


## 高级功能规则

1. 支持自定义按钮和操作
2. 支持多种数据源和数据类型
3. 支持文件和图片上传
4. 支持级联选择和树形选择
5. 支持自定义验证规则
6. 支持数据导出和打印


## SQL直接生成列表示例

1. 访问/topezadmin/listEdit/importSql-进入SQL导入界面
2. 输入查询SQL，如SELECT ID, PROD_CODE, PROD_NAME FROM T_BASE_PRODUCT
3. 系统解析SQL，识别字段ID, PROD_CODE, PROD_NAME
4. 配置列表名称、数据源、ID字段等基本信息
5. 为每个字段配置显示名称、控件类型、是否搜索等
6. 预览并保存生成的列表配置
7. 访问/topezadmin/list/list-{生成的列表代码}查看列表


## 注意事项和最佳实践

1. SQL应尽量简单清晰，避免复杂的子查询和连接
2. 合理使用别名，特别是存在多表连接时
3. 自定义表达式需注意SQL注入风险
4. 合理设置控件类型，提高用户体验
5. 使用缓存数据源提高性能
6. 按需配置搜索条件，避免过多或不必要的条件
7. 定期检查和优化配置，提高系统性能
