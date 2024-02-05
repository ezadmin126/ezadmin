EZadmin 是一款后端嵌入式开发工具，使用 HTML来配置生成页面，可以节省页面开发工作量，极大提升开发前端页面的效率；

支持：<br>
1.SQL生成可动态配置字段与搜索项的列表。 <br>
2.SQL生成可动态配置字段的导出。<br>
3.SQL生成可动态配置字段的表单。<br>
4.SQL生成可动态配置字段的API Restful接口。<br>
5.SQL生成可动态配置字段的树形列表。<br>
<br>
6.支持拖拽页面配置页面，卡片+栅栏模式。<br>
7.可自定义UI,自定义插件，便于与老系统集成。<br>
8.无缝对接老系统，快速抽丝剥茧。后续可一键迁移。<br>
9.内置layui模版，可扩展任意前段模版。<br>
10.支持打印过程执行的SQL明细。<br>

核心优势： 可嵌入至任意 骨灰级，元老级servlet应用，互不影响。UI可以通过覆写相关插件与原系统保持一致。

<br>

内置插件：<br>
列表：<br> 
    搜索项： 输入框，下拉框，级联下拉框，多选下拉框，日期区间，日期时间区间，数字区间等等。<br>
    表格： 文本，下拉文本，链接，富文本，首列（多选、单选、数字），图片，响应式图片等等。<br>
表单：<br>
    输入框，富文本，多行文本，下拉，级联下来，表格，多选下拉，单选，复选，上传，颜色选择，文本，隐藏域等等。<br>
  <br>  
接入方式 

1.加入jar包
如下为springboot。普通servlet应用只需要参考EzClientAutoConfiguration配置相关filter即可。
```xml
<dependencies>
  <dependency>
	<groupId>top.ezadmin</groupId>
	<artifactId>ezadmin-core</artifactId>
	<version>1.0.8-SNAPSHOT</version>
  </dependency>
</dependencies>
```

2.启动ezadmin-web应用
配置application.xml, 导入db.sql
设计好自己的页面。并导出。


3.将导出来的文件 放入自己的springboot应用中如下乳母：<br>
 classpath:/ezcloud/config/layui/list  （列表）<br>
 classpath:/ezcloud/config/layui/form  （表单）<br>
 即可。

<img src="https://github.com/ezadmin126/ezadmin/blob/main/ezadmin-web/src/main/resources/static/1.png">
<img src="https://github.com/ezadmin126/ezadmin/blob/main/ezadmin-web/src/main/resources/static/2.png">
<img src="https://github.com/ezadmin126/ezadmin/blob/main/ezadmin-web/src/main/resources/static/3.png">
<img src="https://github.com/ezadmin126/ezadmin/blob/main/ezadmin-web/src/main/resources/static/4.png">
https://github.com/ezadmin126/ezadmin/blob/main/ezadmin-web/src/main/resources/static/1.png <br>
https://github.com/ezadmin126/ezadmin/blob/main/ezadmin-web/src/main/resources/static/2.png <br>
https://github.com/ezadmin126/ezadmin/blob/main/ezadmin-web/src/main/resources/static/3.png <br>
https://github.com/ezadmin126/ezadmin/blob/main/ezadmin-web/src/main/resources/static/4.png <br>

 
