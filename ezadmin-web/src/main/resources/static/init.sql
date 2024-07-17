
create table if not exists  T_EZADMIN_EDIT
(
    ID           bigint UNSIGNED  auto_increment comment '主键'
    primary key,
    EZ_CODE     varchar(100)                       null comment '编码',
    DATASOURCE  varchar(100)                       null comment '数据源',
    EZ_NAME     varchar(255)                       null comment '名称',
    EZ_TYPE     int                                null comment '类型1列表2表单3详情',
    APP_NAME    varchar(100)                       null comment '应用名称',
    EZ_CONFIG   longtext                           null comment '配置',
    ADD_TIME    datetime default CURRENT_TIMESTAMP null comment '添加时间',
    UPDATE_TIME datetime default CURRENT_TIMESTAMP null comment '修改时间',
    IS_DEL      int                                null comment '1删除0未删除',
    ADD_NAME    varchar(100)                       null comment '添加人',
    UPDATE_NAME varchar(100)                       null comment '修改人'
    ) ENGINE=InnoDB DEFAULT CHARSET =utf8mb4 collate=utf8mb4_unicode_ci
    comment '配置表';

create index T_EZADMIN_EDIT_EZ_CODE_index
    on  T_EZADMIN_EDIT (EZ_CODE);

create index T_EZADMIN_EDIT_EZ_TYPE_index
    on  T_EZADMIN_EDIT (EZ_TYPE);


create table if not exists  T_EZADMIN_PUBLISH
(
    ID          bigint UNSIGNED auto_increment comment '主键'
    primary key,
    EZ_CODE     varchar(100)                       null comment '编码',
    DATASOURCE  varchar(100)                       null comment '数据源',
    EZ_NAME     varchar(255)                       null comment '配置名称',
    EZ_TYPE     int                                null comment '类型1列表2表单3详情',
    APP_NAME    varchar(100)                       null comment '应用名称',
    EZ_CONFIG   longtext                           null comment '配置',
    ADD_TIME    datetime default CURRENT_TIMESTAMP null comment '添加时间',
    UPDATE_TIME datetime default CURRENT_TIMESTAMP null comment '修改时间',
    IS_DEL      int                                null comment '1删除0未删除',
    ADD_NAME    varchar(100)                       null comment '添加人',
    UPDATE_NAME varchar(100)                       null comment '修改人'
    )ENGINE=InnoDB DEFAULT CHARSET =utf8mb4 collate=utf8mb4_unicode_ci
    comment '发布配置表';

create index T_EZADMIN_PUBLISH_EZ_CODE_index
    on  T_EZADMIN_PUBLISH (EZ_CODE);

create index T_EZADMIN_PUBLISH_EZ_TYPE_index
    on  T_EZADMIN_PUBLISH (EZ_TYPE);


create table if not exists  T_EZADMIN_HISTORY
(
    ID          bigint UNSIGNED auto_increment comment '主键'
    primary key,
    EZ_CODE     varchar(100)                       null comment '编码',
    DATASOURCE  varchar(100)                       null comment '数据源',
    EZ_NAME     varchar(255)                       null comment '名称',
    EZ_TYPE     int                                null comment '类型',
    APP_NAME    varchar(100)                       null comment '应用名称',
    EZ_CONFIG   longtext                           null comment '配置',
    ADD_TIME    datetime default CURRENT_TIMESTAMP null comment '添加时间',
    UPDATE_TIME datetime default CURRENT_TIMESTAMP null comment '修改时间',
    IS_DEL      int                                null comment '删除配置',
    ADD_NAME    varchar(100)                       null comment '添加人',
    UPDATE_NAME varchar(100)                       null comment '修改'
    ) ENGINE=InnoDB DEFAULT CHARSET =utf8mb4 collate=utf8mb4_unicode_ci
    comment '发布历史';

create index T_EZADMIN_HISTORY_EZ_CODE_index
    on   T_EZADMIN_HISTORY (EZ_CODE);

create index T_EZADMIN_HISTORY_EZ_TYPE_index
    on   T_EZADMIN_HISTORY (EZ_TYPE);


INSERT INTO  T_EZADMIN_EDIT (ID, EZ_CODE, DATASOURCE, EZ_NAME, EZ_TYPE, APP_NAME, EZ_CONFIG, ADD_TIME, UPDATE_TIME, IS_DEL, ADD_NAME, UPDATE_NAME) VALUES (1, 'listmanage', 'dataSource', '列表管理', 1, 'EZ', '<html>
 <head>
  <title>列表管理</title>
 </head>
 <body id="listmanage" datasource="dataSource" adminstyle="layui" rowbuttonwidth="200">
  <div id="append_head"></div>
  <ul id="tab">
  <li>
      <a class="tablink" select="true" item_name="Tab" url="/topezadmin/listEdit/list-listmanage">列表</a>
     </li>
     <li>
      <a class="tablink" select="false" item_name="f" url="/topezadmin/listEdit/list-formmanage">表单</a>
     </li>
  </ul>
  <form id="search">
   <div>
    <label>列表编码</label>
    <div>
     <object item_name="EZ_CODE" alias="tec" type="input-text" ></object>
    </div>
   </div>
   <div>
    <label>添加时间</label>
    <div>
     <object item_name="ADD_TIME" jdbctype="DATETIME" oper="BETWEEN" alias="tec" type="daterange" ></object>
    </div>
   </div>
   <div>
    <label>修改时间</label>
    <div>
     <object item_name="UPDATE_TIME" jdbctype="DATETIME" oper="BETWEEN" alias="tec" type="daterange" ></object>
    </div>
   </div>
   <div>
    <label>数据源</label>
    <div>
     <object item_name="DATASOURCE" alias="tec" type="input-text" ></object>
    </div>
   </div>
  </form>
  <div id="tableButton">
   <button opentype="MODEL" url="/topezadmin/listEdit/loadEdit-" item_name="addd" type="button-table" plugin_code="button-table">新增</button>
   <button opentype="MODEL" windowname="SQL生成列表" url="/topezadmin/listEdit/importSql-" item_name="addd2" type="button-table" plugin_code="button-table">SQL生成列表</button>
  </div>
  <table>
   <tbody>
    <tr id="column">
     <th id="rowbutton" width="200" fixed="right">
      <button opentype="MODEL" windowname="可视化编辑" url="/topezadmin/listEdit/loadEdit-${EZ_CODE}" item_name="update" type="button-group" plugin_code="button-group" class="layui-border-blue">可视化编辑</button>
      <button opentype="MODEL" windowname="预览"  url="/topezadmin/listEdit/list-${EZ_CODE}" item_name="preview" type="button-single" plugin_code="button-single" class="layui-border-orange">预览</button>
      <button opentype="MODEL" windowname="源码编辑" url="/topezadmin/listEdit/sourceEdit-${EZ_CODE}?EZ_TYPE=1" item_name="export" type="button-single" plugin_code="button-single" class="layui-border-orange">源码编辑</button>
      <button opentype="CONFIRM_AJAX" windowname="确定发布吗" url="/topezadmin/listEdit/publish-${EZ_CODE}?EZ_TYPE=1" item_name="publish" type="button-single" plugin_code="button-single" class="layui-border-orange">发布</button>
      <button opentype="MODEL" windowname="查看发布"  url="/topezadmin/list/list-${EZ_CODE}?EZ_TYPE=1" item_name="view" type="button-single" plugin_code="button-single" class="layui-border-orange">查看发布</button>
      <button opentype="MODEL" windowname="发布历史" url="/topezadmin/listEdit/list-listhistory?EZ_CODE=${EZ_CODE}" item_name="history" type="button-single" plugin_code="button-single" class="layui-border-orange">历史</button>
     </th>
     <th item_name="ID" head="th-numbers" width="60" fixed="left">ID</th>
     <th item_name="EZ_CODE" url="/topezadmin/listEdit/list-${EZ_CODE}" body="td-link" opentype="_BLANK" fixed="left">列表编码</th>
     <th item_name="EZ_NAME">列表名称</th>
     <th item_name="ADD_TIME" jdbctype="DATETIME">添加时间</th>
     <th item_name="UPDATE_TIME" jdbctype="DATETIME">修改时间</th>
     <th item_name="PUB_TIME" jdbctype="DATETIME">上次发布时间</th>
     <th item_name="DATASOURCE">数据源</th>
    </tr>
   </tbody>
  </table>
  <pre id="express" orderby="ORDER BY UPDATE_TIME DESC" groupby="">

<![CDATA[
StringBuilder sql=new StringBuilder();
sql.append("
 SELECT
        tec.ID,
         tec.EZ_CODE, tec.EZ_NAME,
         tec.APP_NAME,
         tec.ADD_TIME,
         tec.UPDATE_TIME,
         tec.DATASOURCE, TEP.UPDATE_TIME PUB_TIME
    FROM
        T_EZADMIN_EDIT tec LEFT JOIN T_EZADMIN_PUBLISH TEP
            on tec.EZ_TYPE = TEP.EZ_TYPE
                                                                  AND tec.EZ_CODE=TEP.EZ_CODE
    WHERE
        tec.IS_DEL = 0 and  ifnull(tec.APP_NAME,\'\')!=\'EZ\'
        AND tec.EZ_TYPE = 1");
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
</html>', '2024-03-14 19:54:45', '2024-03-15 09:58:50', 0, null, null);
INSERT INTO T_EZADMIN_EDIT (ID, EZ_CODE, DATASOURCE, EZ_NAME, EZ_TYPE, APP_NAME, EZ_CONFIG, ADD_TIME, UPDATE_TIME, IS_DEL, ADD_NAME, UPDATE_NAME) VALUES (2, 'listhistory', 'dataSource', '列表发布历史', 1, 'EZ', '<html>
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
     <object item_name="EZ_CODE" type="input-text" ></object>
    </div>
   </div>

   <div>
    <label>添加时间</label>
    <div>
     <object item_name="ADD_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" ></object>
    </div>
   </div>
   <div>
    <label>修改时间</label>
    <div>
     <object item_name="UPDATE_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" ></object>
    </div>
   </div>
   <div>
    <label>数据源</label>
    <div>
     <object item_name="DATASOURCE" type="input-text" ></object>
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
INSERT INTO T_EZADMIN_EDIT (ID, EZ_CODE, DATASOURCE, EZ_NAME, EZ_TYPE, APP_NAME, EZ_CONFIG, ADD_TIME, UPDATE_TIME, IS_DEL, ADD_NAME, UPDATE_NAME) VALUES (3, 'formmanage', 'dataSource', '表单管理', 1, 'EZ', '<html>
 <head>
  <title>表单管理</title>
 </head>
 <body id="formmanage" datasource="dataSource" adminstyle="layui" rowbuttonwidth="200">
  <div id="append_head"></div>
  <ul id="tab">
  </ul>
  <form id="search">
   <div>
    <label>表单编码</label>
    <div>
     <object item_name="EZ_CODE" type="input-text" ></object>
    </div>
   </div>
   <div>
    <label>添加时间</label>
    <div>
     <object item_name="ADD_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" ></object>
    </div>
   </div>
   <div>
    <label>修改时间</label>
    <div>
     <object item_name="UPDATE_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" ></object>
    </div>
   </div>
   <div>
    <label>数据源</label>
    <div>
     <object item_name="DATASOURCE" type="input-text" ></object>
    </div>
   </div>
  </form>
  <div id="tableButton">
   <button opentype="MODEL" windowname="新增" url="/topezadmin/formEdit/loadEdit-" item_name="addd" type="button-table" plugin_code="button-table">新增</button>
   <button opentype="MODEL" windowname="SQL生成列表" url="/topezadmin/formEdit/importSql-" item_name="addd2" type="button-table" plugin_code="button-table">SQL生成表单</button>
  </div>
  <table>
   <tbody>
    <tr id="column">
     <th id="rowbutton" width="200" fixed="right">
      <button opentype="MODEL" url="/topezadmin/formEdit/loadEdit-${EZ_CODE}" item_name="update" type="button-group" plugin_code="button-group" class="layui-border-blue" windowname="可视化编辑">可视化编辑</button>
      <button opentype="MODEL" url="/topezadmin/formEdit/form-${EZ_CODE}" item_name="preview" type="button-single" plugin_code="button-single" class="layui-border-orange" windowname="预览">预览</button>
      <button opentype="MODEL" url="/topezadmin/formEdit/sourceEdit-${EZ_CODE}?EZ_TYPE=1" item_name="export" type="button-single" plugin_code="button-single" class="layui-border-orange" windowname="源码编辑">源码编辑</button>
      <button opentype="CONFIRM_AJAX" windowname="确定发布吗" url="/topezadmin/formEdit/publish-${EZ_CODE}?EZ_TYPE=1" item_name="publish" type="button-single" plugin_code="button-single" class="layui-border-orange">发布</button>
      <button opentype="MODEL" url="/topezadmin/form/form-${EZ_CODE}?EZ_TYPE=1" item_name="view" type="button-single" plugin_code="button-single" class="layui-border-orange" windowname="查看发布">查看发布</button>
      <button opentype="MODEL" url="/topezadmin/list/list-listhistory?EZ_CODE=${EZ_CODE}" item_name="history" type="button-single" plugin_code="button-single" class="layui-border-orange">历史</button>
     </th>
     <th item_name="ID" head="th-numbers" width="60" fixed="left">ID</th>
     <th item_name="EZ_CODE" url="/topezadmin/formEdit/form-${EZ_CODE}" body="td-link" opentype="_BLANK" fixed="left">表单编码</th>
     <th item_name="EZ_NAME">表单名称</th>
     <th item_name="ADD_TIME" jdbctype="DATETIME">添加时间</th>
     <th item_name="UPDATE_TIME" jdbctype="DATETIME">修改时间</th>
     <th item_name="PUB_TIME" jdbctype="DATETIME">上次发布时间</th>
     <th item_name="DATASOURCE">数据源</th>
    </tr>
   </tbody>
  </table>
  <pre id="express" orderby="ORDER BY UPDATE_TIME DESC" groupby="">

<![CDATA[
StringBuilder sql=new StringBuilder();
sql.append("
   SELECT
        tec.ID,
         tec.EZ_CODE, tec.EZ_NAME,
         tec.APP_NAME,
         tec.ADD_TIME,
         tec.UPDATE_TIME,
         tec.DATASOURCE, TEP.UPDATE_TIME PUB_TIME
    FROM
        T_EZADMIN_EDIT tec LEFT JOIN T_EZADMIN_PUBLISH TEP
            on tec.EZ_TYPE = TEP.EZ_TYPE
                                                                  AND tec.EZ_CODE=TEP.EZ_CODE
    WHERE
        tec.IS_DEL = 0 and  ifnull(tec.APP_NAME,\'\')!=\'EZ\'
        AND tec.EZ_TYPE =2");
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
</html>', '2024-03-14 18:12:23', '2024-03-16 16:33:48', 0, null, null);
INSERT INTO T_EZADMIN_EDIT (ID, EZ_CODE, DATASOURCE, EZ_NAME, EZ_TYPE, APP_NAME, EZ_CONFIG, ADD_TIME, UPDATE_TIME, IS_DEL, ADD_NAME, UPDATE_NAME) VALUES (10, 'formhistory', 'dataSource', '表单发布历史', 1, 'EZ', '<html>
 <head>
  <title>表单发布历史</title>
 </head>
 <body id="formhistory" datasource="dataSource" adminstyle="layui" rowbuttonwidth="200">
  <div id="append_head">

  </div>
  <ul id="tab">
  </ul>
  <form id="search">
   <div>
    <label>表单编码</label>
    <div>
     <object item_name="EZ_CODE" type="input-text" ></object>
    </div>
   </div>

   <div>
    <label>添加时间</label>
    <div>
     <object item_name="ADD_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" ></object>
    </div>
   </div>
   <div>
    <label>修改时间</label>
    <div>
     <object item_name="UPDATE_TIME" jdbctype="DATETIME" oper="BETWEEN" type="daterange" ></object>
    </div>
   </div>
   <div>
    <label>数据源</label>
    <div>
     <object item_name="DATASOURCE" type="input-text" ></object>
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
     <th item_name="EZ_CODE"  body="td-text" opentype="_BLANK" fixed="left">表单编码</th>

     <th item_name="EZ_NAME">表单名称</th>
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
          EZ_TYPE = 2");
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


INSERT INTO T_EZADMIN_PUBLISH (ID, EZ_CODE, DATASOURCE, EZ_NAME, EZ_TYPE, APP_NAME, EZ_CONFIG, ADD_TIME, UPDATE_TIME, IS_DEL, ADD_NAME, UPDATE_NAME) VALUES (1, 'listmanage', 'dataSource', '列表管理', 1, 'EZ', '<html>
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
     <object item_name="EZ_CODE" alias="tec" type="input-text" ></object>
    </div>
   </div>
   <div>
    <label>添加时间</label>
    <div>
     <object item_name="ADD_TIME" jdbctype="DATETIME" oper="BETWEEN" alias="tec" type="daterange" ></object>
    </div>
   </div>
   <div>
    <label>修改时间</label>
    <div>
     <object item_name="UPDATE_TIME" jdbctype="DATETIME" oper="BETWEEN" alias="tec" type="daterange" ></object>
    </div>
   </div>
   <div>
    <label>数据源</label>
    <div>
     <object item_name="DATASOURCE" alias="tec" type="input-text" ></object>
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
      <button opentype="MODEL" url="/topezadmin/listEdit/loadEdit-${EZ_CODE}" item_name="update" type="button-group" plugin_code="button-group" class="layui-border-blue">可视化编辑</button>
      <button opentype="MODEL" url="/topezadmin/listEdit/list-${EZ_CODE}" item_name="preview" type="button-single" plugin_code="button-single" class="layui-border-orange">预览</button>
      <button opentype="MODEL" url="/topezadmin/listEdit/sourceEdit-${EZ_CODE}?EZ_TYPE=1" item_name="export" type="button-single" plugin_code="button-single" class="layui-border-orange">源码编辑</button>
      <button opentype="CONFIRM_AJAX" windowname="确定发布吗" url="/topezadmin/listEdit/publish-${EZ_CODE}?EZ_TYPE=1" item_name="publish" type="button-single" plugin_code="button-single" class="layui-border-orange">发布</button>
      <button opentype="MODEL" url="/topezadmin/list/list-${EZ_CODE}?EZ_TYPE=1" item_name="view" type="button-single" plugin_code="button-single" class="layui-border-orange">查看发布</button>
      <button opentype="MODEL" windowname="发布历史" url="/topezadmin/listEdit/list-listhistory?EZ_CODE=${EZ_CODE}" item_name="history" type="button-single" plugin_code="button-single" class="layui-border-orange">历史</button>
     </th>
     <th item_name="ID" head="th-numbers" width="60" fixed="left">ID</th>
     <th item_name="EZ_CODE" url="/topezadmin/listEdit/list-${EZ_CODE}" body="td-link" opentype="_BLANK" fixed="left">列表编码</th>
     <th item_name="EZ_NAME">列表名称</th>
     <th item_name="ADD_TIME" jdbctype="DATETIME">添加时间</th>
     <th item_name="UPDATE_TIME" jdbctype="DATETIME">修改时间</th>
     <th item_name="PUB_TIME" jdbctype="DATETIME">上次发布时间</th>
     <th item_name="DATASOURCE">数据源</th>
    </tr>
   </tbody>
  </table>
  <pre id="express" orderby="ORDER BY UPDATE_TIME DESC" groupby="">

<![CDATA[
StringBuilder sql=new StringBuilder();
sql.append("
 SELECT
        tec.ID,
         tec.EZ_CODE, tec.EZ_NAME,
         tec.APP_NAME,
         tec.ADD_TIME,
         tec.UPDATE_TIME,
         tec.DATASOURCE, TEP.UPDATE_TIME PUB_TIME
    FROM
        T_EZADMIN_EDIT tec LEFT JOIN T_EZADMIN_PUBLISH TEP
            on tec.EZ_TYPE = TEP.EZ_TYPE
                                                                  AND tec.EZ_CODE=TEP.EZ_CODE
    WHERE
        tec.IS_DEL = 0 AND  ifnull(tec.APP_NAME,\'\')!=\'EZ\'
        AND tec.EZ_TYPE = 1");
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
</html>', '2024-03-14 19:54:45', '2024-03-15 09:58:50', 0, null, null);

