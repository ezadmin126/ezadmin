# ezadmin

#### 介绍
低代码后端框架，它使用 HTML 配置来生成页面，可以节省页面开发工作量，极大提升开发前端页面的效率

#### 软件架构
 ezadmin是一款使用html配置页面的后台低代码框架，支持通过SQL快速生成列表，表单。
 页面使用layui,同时也支持原生html。
 
 解析html->获取配置->生成数据->服务端组装页面->渲染页面


#### 安装教程

springboot:

```

 <dependency>
            <groupId>top.ezadmin</groupId>
            <artifactId>ezadmin-core</artifactId>
            <version>1.0.2</version>
 </dependency>

```

普通servlet 容器（springmvc,struts，jfinal等）:
新建filter,
初始化EzBootstrap，
执行ezBootstrap.doFilter(request, response).




#### 使用说明


 **1.图形化操作
** 启动应用，访问： /ezadmin/list/listEdit-
输入SQL，按照提示操作即可。

![快速生成](https://foruda.gitee.com/images/1691328210738686172/6c26e9db_12589662.png "WeChat452871e40704e9a599a77c89addb3595.png")


![表单编辑](https://foruda.gitee.com/images/1691327429680270024/12862860_12589662.png "WeChat59c730a0454dd79070ba2554b00b7dc2.png")


![表单编辑](https://foruda.gitee.com/images/1691327507728093326/468ce5d3_12589662.png "WeChat3963e30e791bee568c431066aabc0aaf.png")


![列表](https://foruda.gitee.com/images/1691328101469046192/27627e26_12589662.png "WeChatf2199789b09af515019dcc5821beed61.png")




 **2.直接编辑配置文件
** 

按照如下格式，将配置文件至于项目  classes/ezadmin/config/list   classes/ezadmin/config/form下面即可。

列表：  访问 /ezadmin/list/list-bnclist
```
 <!doctype html>
<html lang="en">
 <head>
  <meta charset="UTF-8">
  <title>商机列表</title>
 </head>
 <body id="bnclist" datasource="dataSource"   firstcol="numbers">
  <div id="append_head"></div>
  <div class="layui-fluid">

   <form class="layui-form" id="search">
    <div class=" layui-inline ">
     <label class="layui-form-label">姓名</label>
     <div class="layui-input-inline">
      <input class=" layui-input list-search-item " type="text"  item_name="USER_NAME"  >
     </div>
    </div>
    <div class=" layui-inline ">
     <label class="layui-form-label">添加时间</label>
     <div class="layui-input-inline">
         <object class=" layui-input list-search-item " type="daterange"  item_name="ADD_TIME"  ></object>
     </div>
    </div>
   </form>
   <hr class="layui-border-blue">
   <div class="btn-group   bd-highlight" id="tableButton">
       <button   item_name="新增"  opentype="PARENT"  type="table">新增</button>
   </div>
   <table id="table" class="layui-table" style=" width:100%">
    <thead>
     <tr id="column">
      <th type="rowbutton" id="rowbutton" laydata="{field:'oper',minWidth:150,fixed:'right'}">
          <button   type="single">编辑</button>
          <button   type="single">删除</button>
      </th>
       <th item_name="USER_NAME"  >姓名</th>
      <th item_name="USER_PHONE"  >电话</th>
     </tr>
    </thead>
    <tbody>
     <tr>
      <td> express:</td>
      <td colspan="100"><pre id="express" class="layui-code" orderby="order by ID desc" groupby="">StringBuilder sql=new StringBuilder();
sql.append("
    SELECT
        T.ID,
        USER_NAME,
        USER_PHONE
    FROM
        T_CORE_BNC T 
    WHERE
        1 = 1");
return search(sql);</pre></td>
     </tr>
     <tr>
      <td> count:</td>
      <td colspan="100"><pre id="count" class="layui-code"></pre></td>
     </tr>
    </tbody>
   </table>
  </div>
  <div id="append_foot"></div>

 </body>
</html>
```
表单：访问 /ezadmin/form/form-bnclist
```
<!doctype html>
<html lang="en">
 <head>
  <meta charset="UTF-8">
  <title>商机管理</title>

 </head>
 <body id="bnclist" datasource="dataSource" success_url="reloadlocal">
  <div id="append_head" class="append"></div>
  <div class="layui-container">
   <form id="inputForm" method="post" class="layui-form">
    <div class="layui-card" group_name="EZ_DEFAULT_GROUP">
     <div class="layui-card-header">
       EZ_DEFAULT_GROUP 
     </div>
     <div class="layui-card-body">
      <div class="layui-form-item ">
          <label class="layui-form-label">姓名</label>
       <div class="layui-input-block form-group">
        <input class="form-item"   item_name="USER_NAME" type="text" >
       </div>
      </div>
      <div class="layui-form-item ">
          <label class="layui-form-label">电话</label>
       <div class="layui-input-block form-group">
        <input class="form-item"   item_name="USER_PHONE" type="text" >
       </div>
      </div>
      </div>
    </div>
   </form>
   <form id="configForm" class="layui-form">
    <div class="layui-card" group_name="表单数据配置">
     <div class="layui-card-header">
       表单数据配置 
     </div>
     <div class="layui-card-body">
     </div>
    </div>
    <div class="layui-form-item "><label class="layui-form-label">初始表达式</label>
     <div class="layui-input-block form-group">
      <pre class="form-item layui-code" name="init_express" item_name="init_express" type="">StringBuilder sql=new StringBuilder();
sql.append("select USER_NAME,USER_PHONE  from T_CORE_BNC where  ID=${ID}  ");
return select(sql).get(0);</pre>
     </div>
    </div>
    <div class="layui-form-item "><label class="layui-form-label">提交表达式</label>
     <div class="layui-input-block form-group">
      <pre class="form-item layui-code" name="submit_express" item_name="submit_express" type="">
import com.ezadmin.plugins.express.jdbc.UpdateParam;
import com.ezadmin.plugins.express.jdbc.InsertParam;                  		
import com.ezadmin.web.EzResult;                  		
companyId=$$("COMPANY_ID");                 		
sessionUserId=$$("EZ_SESSION_USER_ID_KEY");        
sessionUserName=$$("EZ_SESSION_USER_NAME_KEY");    
if(!isNotBlank("ID")){                  		
    param=new InsertParam();                		
    param.table("T_CORE_BNC");               		
     param.add("#{USER_NAME}");               		
    param.add("#{USER_PHONE}");
    param.add("#{ADD_TIME,value=NOW()}"); 
    param.add("#{ADD_ID,value='"+sessionUserId+"'}"); 
    param.add("#{ADD_NAME,value='"+sessionUserName+"'}"); 
id=insertSimple(param);
return id; 
}else{                 								
    param=new UpdateParam();
    param.table("T_CORE_BNC");               		
     param.add("#{USER_NAME}");               		
    param.add("#{USER_PHONE}");
   param.add("#{UPDATE_TIME,value=NOW()}");
   param.add("#{UPDATE_ID,value='"+sessionUserId+"'}");
   param.add("#{UPDATE_NAME,value='"+sessionUserName+"'}");
  StringBuilder updateSql=new StringBuilder();
  updateSql.append(" where ID=#{ID} ");
  if(companyId&gt;1){
      updateSql.append(" and COMPANY_ID= "+companyId);
  }
 param.where(updateSql.toString());
 updateSimple(param);
 return ID;
}</pre>
     </div>
    </div>
    <div class="layui-form-item "><label class="layui-form-label">删除表达式</label>
     <div class="layui-input-block form-group">
      <pre class="form-item layui-code" name="delete_express" item_name="delete_express" type="">update("UPDATE T_CORE_BNC set delete_flag=1 where ID=${ID}");</pre>
     </div>
    </div>
    <div class="layui-form-item "><label class="layui-form-label">状态表达式</label>
     <div class="layui-input-block form-group">
      <pre class="form-item layui-code" name="status_express" item_name="status_express" type=""></pre>
     </div>
    </div>
    <div class="layui-form-item "><label class="layui-form-label">分组表达式</label>
     <div class="layui-input-block form-group">
      <pre class="form-item layui-code" name="group_data" item_name="group_data" type=""></pre>
     </div>
    </div>
   </form>
  </div>
  <div id="append_foot" class="append">
  </div>
 </body>
</html>
```




#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
