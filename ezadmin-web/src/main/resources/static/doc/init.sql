
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


