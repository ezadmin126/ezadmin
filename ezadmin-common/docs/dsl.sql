create table  T_EZADMIN_LIST
(
    ID             bigint auto_increment comment '编辑ID'
        primary key,
    LIST_ID        varchar(100)                       null comment '列表编码',
    DATASOURCE     varchar(100)                       null comment '数据源',
    LIST_NAME      varchar(255)                       null comment '列表名称',
    LIST_DSL       text                               null comment '列表配置-dsl',
    SELECT_EXPRESS text                               null comment '列表配置-查询表达式',
    COUNT_EXPRESS  text                               null comment '列表配置-总数表达式默认为空',
    ADD_TIME       datetime default CURRENT_TIMESTAMP null comment '添加时间',
    UPDATE_TIME    datetime default CURRENT_TIMESTAMP null comment '更新时间',
    IS_DEL         int                                null comment '是否删除',
    ADD_NAME       varchar(100)                       null comment '添加人名称',
    UPDATE_NAME    varchar(100)                       null comment '更新人名称',
    DELETE_FLAG    int      default 0                 not null comment '删除标识 0未删除 1已删除',
    ADD_ID         bigint                             null comment '添加人',
    UPDATE_ID      bigint                             null
)
    comment 'ezadmin列表配置表' charset = utf8mb4;

create table  T_EZADMIN_FORM
(
    ID             bigint auto_increment comment '编辑ID'
        primary key,
    FORM_ID        varchar(100)                       null comment '表单编码',
    DATASOURCE     varchar(100)                       null comment '数据源',
    FORM_NAME      varchar(255)                       null comment '表单名称',
    FORM_DSL       text                               null comment '表单配置-dsl',
    INIT_EXPRESS   text                               null comment '表单配置-查询表达式',
    SUBMIT_EXPRESS text                               null comment '表单配置-提交',
    DELETE_EXPRESS text                               null comment '表单配置-删除',
    ADD_TIME       datetime default CURRENT_TIMESTAMP null comment '添加时间',
    UPDATE_TIME    datetime default CURRENT_TIMESTAMP null comment '更新时间',
    IS_DEL         int                                null comment '是否删除',
    ADD_NAME       varchar(100)                       null comment '添加人名称',
    UPDATE_NAME    varchar(100)                       null comment '更新人名称',
    DELETE_FLAG    int      default 0                 not null comment '删除标识 0未删除 1已删除',
    ADD_ID         bigint                             null,
    UPDATE_ID      bigint                             null
)
    comment 'ezadmin表单配置表' charset = utf8mb4;

create table  T_EZADMIN_COMPONENT
(
    ID               bigint auto_increment comment '编辑ID'
        primary key,
    COMPONENT_CODE   varchar(100)                       null comment '组件编码',
    COMPONENT_NAME   varchar(255)                       null comment '组件名称',
    COMPONENT_CONFIG text                               null comment '组件代码',
    ADD_TIME         datetime default CURRENT_TIMESTAMP null comment '添加时间',
    UPDATE_TIME      datetime default CURRENT_TIMESTAMP null comment '更新时间',
    IS_DEL           int                                null comment '是否删除',
    ADD_NAME         varchar(100)                       null comment '添加人名称',
    UPDATE_NAME      varchar(100)                       null comment '更新人名称',
    DELETE_FLAG      int      default 0                 not null comment '删除标识 0未删除 1已删除',
    ADD_ID           bigint                             null comment '添加人',
    UPDATE_ID        bigint                             null comment '修改人'
)
    comment 'EZ 组件表' charset = utf8mb4;

