SET MODE MySQL;
create table if not exists T_EZADMIN_EDIT
(
    ID          bigint auto_increment
        primary key,
    EZ_CODE     varchar(100)                            null,
    DATASOURCE  varchar(100)                            null,
    EZ_NAME     varchar(255)                            null,
    EZ_TYPE     int                                     null,
    APP_NAME    varchar(100)                            null,
    EZ_CONFIG   longtext                                null,
ADD_TIME    timestamp default CURRENT_TIMESTAMP       null  ,
    UPDATE_TIME timestamp default CURRENT_TIMESTAMP   null,
    IS_DEL      int                                     null,
    ADD_NAME    varchar(100)                            null,
    UPDATE_NAME varchar(100)                            null
);

create index T_EZADMIN_EDIT_EZ_CODE_index
    on T_EZADMIN_EDIT (EZ_CODE);

create index T_EZADMIN_EDIT_EZ_TYPE_index
    on T_EZADMIN_EDIT (EZ_TYPE);

create table if not exists T_EZADMIN_HISTORY
(
    ID          bigint auto_increment
        primary key,
    EZ_CODE     varchar(100)                            null,
    DATASOURCE  varchar(100)                            null,
    EZ_NAME     varchar(255)                            null,
    EZ_TYPE     int                                     null,
    APP_NAME    varchar(100)                            null,
    EZ_CONFIG   longtext                                null,
    ADD_TIME    timestamp default CURRENT_TIMESTAMP       null  ,
    UPDATE_TIME timestamp default CURRENT_TIMESTAMP   null,
    IS_DEL      int                                     null,
    ADD_NAME    varchar(100)                            null,
    UPDATE_NAME varchar(100)                            null
);

create index T_EZADMIN_HISTORY_EZ_CODE_index
    on T_EZADMIN_HISTORY (EZ_CODE);

create index T_EZADMIN_HISTORY_EZ_TYPE_index
    on T_EZADMIN_HISTORY (EZ_TYPE);

create table if not exists T_EZADMIN_PUBLISH
(
    ID          bigint auto_increment
        primary key,
    EZ_CODE     varchar(100)                            null,
    DATASOURCE  varchar(100)                            null,
    EZ_NAME     varchar(255)                            null,
    EZ_TYPE     int                                     null,
    APP_NAME    varchar(100)                            null,
    EZ_CONFIG   longtext                                null,
ADD_TIME    timestamp default CURRENT_TIMESTAMP       null  ,
    UPDATE_TIME timestamp default CURRENT_TIMESTAMP   null,
    IS_DEL      int                                     null,
    ADD_NAME    varchar(100)                            null,
    UPDATE_NAME varchar(100)                            null
);

create index T_EZADMIN_PUBLISH_EZ_CODE_index
    on T_EZADMIN_PUBLISH (EZ_CODE);

create index T_EZADMIN_PUBLISH_EZ_TYPE_index
    on T_EZADMIN_PUBLISH (EZ_TYPE);


INSERT INTO  T_EZADMIN_EDIT (ID, EZ_CODE, DATASOURCE, EZ_NAME, EZ_TYPE, APP_NAME, EZ_CONFIG, ADD_TIME, UPDATE_TIME, IS_DEL, ADD_NAME, UPDATE_NAME) VALUES (1, 'listmanage', 'dataSource', '列表管理', 1, 'EZ', '<html>
 <head>
  <title>列表管理</title>
 </head>
 <body id="listmanage" datasource="dataSource" adminstyle="layui" rowbuttonwidth="200">
  <div id="append_head"></div>
  <ul id="tab">
  </ul>
  <form id="search">
   <div>
    <label>列表编码</label>
    <div>
     <object item_name="EZ_CODE" type="input-text" plugin_code="input-text"></object>
    </div>
   </div>
   <div>
    <label>添加时间</label>
    <div>
     <object item_name="ADD_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" plugin_code="daterange"></object>
    </div>
   </div>
   <div>
    <label>修改时间</label>
    <div>
     <object item_name="UPDATE_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" plugin_code="daterange"></object>
    </div>
   </div>
   <div>
    <label>数据源</label>
    <div>
     <object item_name="DATASOURCE" type="input-text" plugin_code="input-text"></object>
    </div>
   </div>
  </form>
  <div id="tableButton">
   <button opentype="_BLANK" url="/topezadmin/listEdit/loadEdit-" item_name="addd" type="button-table" plugin_code="button-table">新增</button>
   <button opentype="_BLANK" windowname="SQL生成列表" url="/topezadmin/listEdit/importSql-" item_name="addd2" type="button-table" plugin_code="button-table">SQL生成列表</button>
  </div>
  <table>
   <tbody>
    <tr id="column">
     <th id="rowbutton" width="200" fixed="right">
      <button opentype="_BLANK" url="/topezadmin/listEdit/loadEdit-${EZ_CODE}" item_name="update" type="button-group" plugin_code="button-group" class="layui-border-blue">可视化编辑</button>
      <button opentype="_BLANK" url="/topezadmin/listEdit/list-${EZ_CODE}" item_name="preview" type="button-single" plugin_code="button-single" class="layui-border-orange">预览</button>
      <button opentype="_BLANK" url="/topezadmin/listEdit/sourceEdit-${EZ_CODE}?EZ_TYPE=1" item_name="export" type="button-single" plugin_code="button-single" class="layui-border-orange">源码编辑</button>
      <button opentype="CONFIRM_AJAX" windowname="确定发布吗" url="/topezadmin/listEdit/publish-${EZ_CODE}?EZ_TYPE=1" item_name="publish" type="button-single" plugin_code="button-single" class="layui-border-orange">发布生产</button>
      <button opentype="_BLANK" url="/topezadmin/list/list-${EZ_CODE}?EZ_TYPE=1" item_name="view" type="button-single" plugin_code="button-single" class="layui-border-orange">查看生产</button>
      <button opentype="MODEL" windowname="发布历史" url="/topezadmin/listEdit/list-listhistory?EZ_CODE=${EZ_CODE}" item_name="history" type="button-single" plugin_code="button-single" class="layui-border-orange">历史</button>
     </th>
     <th item_name="ID" head="th-numbers" width="60" fixed="left">ID</th>
     <th item_name="EZ_CODE" url="/topezadmin/listEdit/list-${EZ_CODE}" body="td-link" opentype="_BLANK" fixed="left">列表编码</th>
     <th item_name="EZ_NAME">列表名称</th>
     <th item_name="ADD_TIME" jdbctype="DATETIME">添加时间</th>
     <th item_name="UPDATE_TIME" jdbctype="DATETIME">修改时间</th>
     <th item_name="DATASOURCE">数据源</th>
    </tr>
   </tbody>
  </table>
  <pre id="express" orderby="ORDER BY UPDATE_TIME DESC" groupby="">

<![CDATA[
StringBuilder sql=new StringBuilder();
sql.append("
    SELECT
        ID,
        EZ_CODE,EZ_NAME,
        APP_NAME,
        ADD_TIME,
        UPDATE_TIME,
        DATASOURCE
    FROM
        T_EZADMIN_EDIT tec
    WHERE
        tec.IS_DEL = 0
        AND EZ_TYPE = 1");
return search(sql);
]]>


  </pre>
  <pre id="count">

</pre>
  <pre item_name="displayorder_express" type="">

<![CDATA[

]]>


  </pre>
  <div id="append_foot"></div>
 </body>
</html>', '2024-03-14 19:54:45', '2024-03-14 19:54:45', 0, null, null);
INSERT INTO  T_EZADMIN_EDIT (ID, EZ_CODE, DATASOURCE, EZ_NAME, EZ_TYPE, APP_NAME, EZ_CONFIG, ADD_TIME, UPDATE_TIME, IS_DEL, ADD_NAME, UPDATE_NAME) VALUES (2, 'listhistory', 'dataSource', '列表发布历史', 1, 'EZ', '<html>
 <head>
  <title>列表发布历史</title>
 </head>
 <body id="listhistory" datasource="dataSource" adminstyle="layui" rowbuttonwidth="200">
  <div id="append_head">

  </div>
  <ul id="tab">
  </ul>
  <form id="search">
   <div>
    <label>列表编码</label>
    <div>
     <object item_name="EZ_CODE" type="input-text" plugin_code="input-text"></object>
    </div>
   </div>

   <div>
    <label>添加时间</label>
    <div>
     <object item_name="ADD_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" plugin_code="daterange"></object>
    </div>
   </div>
   <div>
    <label>修改时间</label>
    <div>
     <object item_name="UPDATE_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" plugin_code="daterange"></object>
    </div>
   </div>
   <div>
    <label>数据源</label>
    <div>
     <object item_name="DATASOURCE" type="input-text" plugin_code="input-text"></object>
    </div>
   </div>
  </form>
  <div id="tableButton">
  </div>
  <table>
   <tbody>
    <tr id="column">
     <th id="rowbutton" width="200" fixed="right">
     </th>
     <th item_name="ID" head="th-numbers" width="60" fixed="left">ID</th>
     <th item_name="EZ_CODE"  body="td-text" opentype="_BLANK" fixed="left">列表编码</th>

     <th item_name="EZ_NAME">列表名称</th>
     <th item_name="ADD_TIME" jdbctype="DATETIME">添加时间</th>
     <th item_name="UPDATE_TIME" jdbctype="DATETIME">修改时间</th>
     <th item_name="DATASOURCE">数据源</th>
    </tr>
   </tbody>
  </table>
  <pre id="express" orderby="ORDER BY ID DESC" groupby="">

<![CDATA[
StringBuilder sql=new StringBuilder();
sql.append("
    SELECT
        ID,
        EZ_CODE,EZ_NAME,
        APP_NAME,
        ADD_TIME,
        UPDATE_TIME,
        DATASOURCE
    FROM
        T_EZADMIN_HISTORY tec
    WHERE
          EZ_TYPE = 1");
return search(sql);
]]>


  </pre>
  <pre id="count">

</pre>
  <pre item_name="displayorder_express" type="">

<![CDATA[

]]>


  </pre>
  <div id="append_foot"></div>
 </body>
</html>', '2024-03-14 16:50:04', '2024-03-14 16:33:17', 0, null, null);
INSERT INTO  T_EZADMIN_EDIT (ID, EZ_CODE, DATASOURCE, EZ_NAME, EZ_TYPE, APP_NAME, EZ_CONFIG, ADD_TIME, UPDATE_TIME, IS_DEL, ADD_NAME, UPDATE_NAME) VALUES (3, 'formmanage', 'dataSource', '表单管理', 1, 'EZ', '<html>
 <head>
  <title>表单管理</title>
 </head>
 <body id="listmanage" datasource="dataSource" adminstyle="layui" rowbuttonwidth="200">
  <div id="append_head">

  </div>
  <ul id="tab">
  </ul>
  <form id="search">
   <div>
    <label>表单编码</label>
    <div>
     <object item_name="EZ_CODE" type="input-text" plugin_code="input-text"></object>
    </div>
   </div>

   <div>
    <label>添加时间</label>
    <div>
     <object item_name="ADD_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" plugin_code="daterange"></object>
    </div>
   </div>
   <div>
    <label>修改时间</label>
    <div>
     <object item_name="UPDATE_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" plugin_code="daterange"></object>
    </div>
   </div>
   <div>
    <label>数据源</label>
    <div>
     <object item_name="DATASOURCE" type="input-text" plugin_code="input-text"></object>
    </div>
   </div>
  </form>
  <div id="tableButton">
   <button opentype="_BLANK" url="/topezadmin/formEdit/loadEdit-" item_name="addd" type="button-table" plugin_code="button-table">新增</button>
   <button opentype="_BLANK" windowname="SQL生成列表" url="/topezadmin/formEdit/importSql-" item_name="addd2" type="button-table" plugin_code="button-table">SQL生成表单</button>
  </div>
  <table>
   <tbody>
    <tr id="column">
     <th id="rowbutton" width="200" fixed="right">
      <button opentype="_BLANK" url="/topezadmin/formEdit/loadEdit-${EZ_CODE}" item_name="update" type="button-group" plugin_code="button-group" class="layui-border-blue">可视化编辑</button>
      <button opentype="_BLANK" url="/topezadmin/formEdit/form-${EZ_CODE}" item_name="preview" type="button-single" plugin_code="button-single" class="layui-border-orange">预览</button>
      <button opentype="_BLANK" url="/topezadmin/formEdit/sourceEdit-${EZ_CODE}?EZ_TYPE=1" item_name="export" type="button-single" plugin_code="button-single" class="layui-border-orange">源码编辑</button>
      <button opentype="CONFIRM_AJAX" url="/topezadmin/listEdit/publish-${EZ_CODE}?EZ_TYPE=1" item_name="publish" type="button-single" plugin_code="button-single" class="layui-border-orange">发布生产</button>
      <button opentype="_BLANK" url="/topezadmin/form/form-${EZ_CODE}?EZ_TYPE=1" item_name="view" type="button-single" plugin_code="button-single" class="layui-border-orange">查看生产</button>
      <button opentype="MODEL" url="/topezadmin/list/list-listhistory?EZ_CODE=${EZ_CODE}" item_name="history" type="button-single" plugin_code="button-single" class="layui-border-orange">历史</button>
     </th>
     <th item_name="ID" head="th-numbers" width="60" fixed="left">ID</th>
     <th item_name="EZ_CODE" url="/topezadmin/listEdit/list-${EZ_CODE}" body="td-link" opentype="_BLANK" fixed="left">表单编码</th>

     <th item_name="EZ_NAME">表单名称</th>
     <th item_name="ADD_TIME" jdbctype="DATETIME">添加时间</th>
     <th item_name="UPDATE_TIME" jdbctype="DATETIME">修改时间</th>
     <th item_name="DATASOURCE">数据源</th>
    </tr>
   </tbody>
  </table>
  <pre id="express" orderby="ORDER BY UPDATE_TIME DESC" groupby="">

<![CDATA[
StringBuilder sql=new StringBuilder();
sql.append("
    SELECT
        ID,
        EZ_CODE,EZ_NAME,
        APP_NAME,
        ADD_TIME,
        UPDATE_TIME,
        DATASOURCE
    FROM
        T_EZADMIN_EDIT tec
    WHERE
        tec.IS_DEL = 0
        AND EZ_TYPE = 2");
return search(sql);
]]>


  </pre>
  <pre id="count">

</pre>
  <pre item_name="displayorder_express" type="">

<![CDATA[

]]>


  </pre>
  <div id="append_foot"></div>
 </body>
</html>', '2024-03-14 18:12:23', '2024-03-14 16:33:17', 0, null, null);
INSERT INTO  T_EZADMIN_PUBLISH (ID, EZ_CODE, DATASOURCE, EZ_NAME, EZ_TYPE, APP_NAME, EZ_CONFIG, ADD_TIME, UPDATE_TIME, IS_DEL, ADD_NAME, UPDATE_NAME) VALUES (1, 'listmanage', 'dataSource', '列表管理', 1, 'EZ', '<html>
 <head>
  <title>列表管理</title>
 </head>
 <body id="listmanage" datasource="dataSource" adminstyle="layui" rowbuttonwidth="200">
  <div id="append_head"></div>
  <ul id="tab">
  </ul>
  <form id="search">
   <div>
    <label>列表编码</label>
    <div>
     <object item_name="EZ_CODE" type="input-text" plugin_code="input-text"></object>
    </div>
   </div>
   <div>
    <label>添加时间</label>
    <div>
     <object item_name="ADD_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" plugin_code="daterange"></object>
    </div>
   </div>
   <div>
    <label>修改时间</label>
    <div>
     <object item_name="UPDATE_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" plugin_code="daterange"></object>
    </div>
   </div>
   <div>
    <label>数据源</label>
    <div>
     <object item_name="DATASOURCE" type="input-text" plugin_code="input-text"></object>
    </div>
   </div>
  </form>
  <div id="tableButton">
   <button opentype="_BLANK" url="/topezadmin/listEdit/loadEdit-" item_name="addd" type="button-table" plugin_code="button-table">新增</button>
   <button opentype="_BLANK" windowname="SQL生成列表" url="/topezadmin/listEdit/importSql-" item_name="addd2" type="button-table" plugin_code="button-table">SQL生成列表</button>
  </div>
  <table>
   <tbody>
    <tr id="column">
     <th id="rowbutton" width="200" fixed="right">
      <button opentype="_BLANK" url="/topezadmin/listEdit/loadEdit-${EZ_CODE}" item_name="update" type="button-group" plugin_code="button-group" class="layui-border-blue">可视化编辑</button>
      <button opentype="_BLANK" url="/topezadmin/listEdit/list-${EZ_CODE}" item_name="preview" type="button-single" plugin_code="button-single" class="layui-border-orange">预览</button>
      <button opentype="_BLANK" url="/topezadmin/listEdit/sourceEdit-${EZ_CODE}?EZ_TYPE=1" item_name="export" type="button-single" plugin_code="button-single" class="layui-border-orange">源码编辑</button>
      <button opentype="CONFIRM_AJAX" windowname="确定发布吗" url="/topezadmin/listEdit/publish-${EZ_CODE}?EZ_TYPE=1" item_name="publish" type="button-single" plugin_code="button-single" class="layui-border-orange">发布生产</button>
      <button opentype="_BLANK" url="/topezadmin/list/list-${EZ_CODE}?EZ_TYPE=1" item_name="view" type="button-single" plugin_code="button-single" class="layui-border-orange">查看生产</button>
      <button opentype="MODEL" windowname="发布历史" url="/topezadmin/listEdit/list-listhistory?EZ_CODE=${EZ_CODE}" item_name="history" type="button-single" plugin_code="button-single" class="layui-border-orange">历史</button>
     </th>
     <th item_name="ID" head="th-numbers" width="60" fixed="left">ID</th>
     <th item_name="EZ_CODE" url="/topezadmin/listEdit/list-${EZ_CODE}" body="td-link" opentype="_BLANK" fixed="left">列表编码</th>
     <th item_name="EZ_NAME">列表名称</th>
     <th item_name="ADD_TIME" jdbctype="DATETIME">添加时间</th>
     <th item_name="UPDATE_TIME" jdbctype="DATETIME">修改时间</th>
     <th item_name="DATASOURCE">数据源</th>
    </tr>
   </tbody>
  </table>
  <pre id="express" orderby="ORDER BY UPDATE_TIME DESC" groupby="">

<![CDATA[
StringBuilder sql=new StringBuilder();
sql.append("
    SELECT
        ID,
        EZ_CODE,EZ_NAME,
        APP_NAME,
        ADD_TIME,
        UPDATE_TIME,
        DATASOURCE
    FROM
        T_EZADMIN_EDIT tec
    WHERE
        tec.IS_DEL = 0
        AND EZ_TYPE = 1");
return search(sql);
]]>


  </pre>
  <pre id="count">

</pre>
  <pre item_name="displayorder_express" type="">

<![CDATA[

]]>


  </pre>
  <div id="append_foot"></div>
 </body>
</html>', '2024-03-14 19:54:57', '2024-03-14 19:54:57', 0, null, null);
INSERT INTO  T_EZADMIN_PUBLISH (ID, EZ_CODE, DATASOURCE, EZ_NAME, EZ_TYPE, APP_NAME, EZ_CONFIG, ADD_TIME, UPDATE_TIME, IS_DEL, ADD_NAME, UPDATE_NAME) VALUES (3, 'listhistory', 'dataSource', '列表发布历史', 1, null, '<html>
 <head>
  <title>列表发布历史</title>
 </head>
 <body id="listhistory" datasource="dataSource" adminstyle="layui" rowbuttonwidth="200">
  <div id="append_head">

  </div>
  <ul id="tab">
  </ul>
  <form id="search">
   <div>
    <label>列表编码</label>
    <div>
     <object item_name="EZ_CODE" type="input-text" plugin_code="input-text"></object>
    </div>
   </div>

   <div>
    <label>添加时间</label>
    <div>
     <object item_name="ADD_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" plugin_code="daterange"></object>
    </div>
   </div>
   <div>
    <label>修改时间</label>
    <div>
     <object item_name="UPDATE_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" plugin_code="daterange"></object>
    </div>
   </div>
   <div>
    <label>数据源</label>
    <div>
     <object item_name="DATASOURCE" type="input-text" plugin_code="input-text"></object>
    </div>
   </div>
  </form>
  <div id="tableButton">
  </div>
  <table>
   <tbody>
    <tr id="column">
     <th id="rowbutton" width="200" fixed="right">
     </th>
     <th item_name="ID" head="th-numbers" width="60" fixed="left">ID</th>
     <th item_name="EZ_CODE"  body="td-text" opentype="_BLANK" fixed="left">列表编码</th>

     <th item_name="EZ_NAME">列表名称</th>
     <th item_name="ADD_TIME" jdbctype="DATETIME">添加时间</th>
     <th item_name="UPDATE_TIME" jdbctype="DATETIME">修改时间</th>
     <th item_name="DATASOURCE">数据源</th>
    </tr>
   </tbody>
  </table>
  <pre id="express" orderby="ORDER BY ID DESC" groupby="">

<![CDATA[
StringBuilder sql=new StringBuilder();
sql.append("
    SELECT
        ID,
        EZ_CODE,EZ_NAME,
        APP_NAME,
        ADD_TIME,
        UPDATE_TIME,
        DATASOURCE
    FROM
        T_EZADMIN_HISTORY tec
    WHERE
          EZ_TYPE = 1");
return search(sql);
]]>


  </pre>
  <pre id="count">

</pre>
  <pre item_name="displayorder_express" type="">

<![CDATA[

]]>


  </pre>
  <div id="append_foot"></div>
 </body>
</html>', '2024-03-14 19:57:01', null, 0, null, null);
