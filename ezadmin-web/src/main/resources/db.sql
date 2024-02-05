CREATE TABLE t_ez_cloud (
                            ID BIGINT AUTO_INCREMENT,
                            EZ_CODE VARCHAR(100),
                            EZ_TYPE INT,
                            APP_NAME VARCHAR(100),
                            EZ_CONFIG TEXT,
                            ADD_TIME TIMESTAMP,
                            UPDATE_TIME TIMESTAMP,
                            IS_DEL INT,
                            DATASOURCE VARCHAR(100),
                            EZ_NAME VARCHAR(255),
                            PRIMARY KEY (ID)
);

INSERT INTO  t_ez_cloud
(ID, EZ_CODE, EZ_TYPE, APP_NAME, EZ_CONFIG, ADD_TIME, UPDATE_TIME, IS_DEL, DATASOURCE, EZ_NAME)
VALUES(1, 'listmanage', 1, 'EZ', '<html>
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
    <label>应用名称</label>
    <div>
     <object item_name="APP_NAME" type="input-text" plugin_code="input-text"></object>
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
   <button opentype="_BLANK" url="/ezlist/loadEdit.html" item_name="addd" type="button-table" plugin_code="button-table">新增</button>
   <button opentype="_BLANK" windowname="SQL生成列表" url="/ezlist/importSql.html" item_name="addd2" type="button-table" plugin_code="button-table">SQL生成列表</button>
  </div>
  <table>
   <tbody>
    <tr id="column">
     <th id="rowbutton" width="200" fixed="right">
      <button opentype="_BLANK" url="/ezlist/loadEdit.html?cloudId=${ID}" item_name="update" type="button-single" plugin_code="button-single" class="layui-border-blue">可视化编辑</button>
      <button opentype="_BLANK" url="/ezlist/export.html?cloudId=${ID}&amp;ezType=1" item_name="export" type="button-single" plugin_code="button-single" class="layui-border-orange">源码编辑</button>
     </th>
     <th item_name="ID" head="th-numbers" width="60" fixed="left">ID</th>
     <th item_name="EZ_CODE" url="/ezcloud/list/list-${EZ_CODE}" body="td-link" opentype="_BLANK" fixed="left">列表编码</th>
     <th item_name="APP_NAME">应用名称</th>
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
        t_ez_cloud tec
    WHERE
        tec.IS_DEL = 0
        AND EZ_TYPE = 1");
return search(sql);
]]>


  </pre>
  <pre id="count">

</pre>
  <pre item_name="displayorder_express" type=""></pre>
  <div id="append_foot"></div>
 </body>
</html>', '2024-01-22 19:24:21', '2024-01-29 10:56:06', 0, 'dataSource', '列表管理');