-- =============================================
-- DSL 双数据源功能测试数据
-- =============================================

-- 1. 测试表单DSL（数据库版）
INSERT INTO T_EZADMIN_FORM (
    FORM_ID,
    FORM_NAME,
    DATASOURCE,
    FORM_DSL,
    INIT_EXPRESS,
    SUBMIT_EXPRESS,
    DELETE_EXPRESS,
    ADD_TIME,
    DELETE_FLAG
) VALUES (
    'test-db-form',
    '测试表单(数据库版)',
    'defaultDataSource',
    '{
        "id": "test-db-form",
        "title": "测试表单(数据库版)",
        "description": "此表单配置存储在数据库中",
        "cardList": [
            {
                "title": "基本信息",
                "fieldList": [
                    [
                        {
                            "field": "name",
                            "label": "姓名",
                            "component": "input",
                            "classAppend": "layui-col-md6",
                            "props": {
                                "placeholder": "请输入姓名",
                                "required": true
                            }
                        },
                        {
                            "field": "age",
                            "label": "年龄",
                            "component": "input",
                            "classAppend": "layui-col-md6",
                            "props": {
                                "type": "number",
                                "placeholder": "请输入年龄"
                            }
                        }
                    ],
                    [
                        {
                            "field": "email",
                            "label": "邮箱",
                            "component": "input",
                            "classAppend": "layui-col-md12",
                            "props": {
                                "type": "email",
                                "placeholder": "请输入邮箱"
                            }
                        }
                    ]
                ]
            }
        ]
    }',
    'SELECT * FROM t_user WHERE id = #{ID}',
    'INSERT INTO t_user (name, age, email) VALUES (#{name}, #{age}, #{email}); return LAST_INSERT_ID();',
    'UPDATE t_user SET DELETE_FLAG = 1 WHERE id = #{ID}',
    NOW(),
    0
);

-- 2. 测试列表DSL（数据库版）
INSERT INTO T_EZADMIN_LIST (
    LIST_ID,
    LIST_NAME,
    DATASOURCE,
    LIST_DSL,
    SELECT_EXPRESS,
    COUNT_EXPRESS,
    ADD_TIME,
    DELETE_FLAG
) VALUES (
    'test-db-list',
    '测试列表(数据库版)',
    'defaultDataSource',
    '{
        "id": "test-db-list",
        "title": "测试列表(数据库版)",
        "description": "此列表配置存储在数据库中",
        "column": [
            {
                "field": "id",
                "label": "ID",
                "component": "tdText",
                "width": 80
            },
            {
                "field": "name",
                "label": "姓名",
                "component": "tdText",
                "width": 150
            },
            {
                "field": "age",
                "label": "年龄",
                "component": "tdText",
                "width": 100
            },
            {
                "field": "email",
                "label": "邮箱",
                "component": "tdText",
                "width": 200
            }
        ],
        "search": [
            [
                {
                    "field": "name",
                    "label": "姓名",
                    "component": "input",
                    "operator": "like",
                    "props": {
                        "placeholder": "请输入姓名"
                    }
                }
            ]
        ],
        "body": {
            "limit": 20
        }
    }',
    'SELECT id, name, age, email, create_time FROM t_user WHERE DELETE_FLAG = 0 #{AND name LIKE CONCAT(''%'',#name,''%'')}',
    'SELECT COUNT(1) FROM t_user WHERE DELETE_FLAG = 0 #{AND name LIKE CONCAT(''%'',#name,''%'')}',
    NOW(),
    0
);

-- 3. 查询验证
SELECT
    FORM_ID AS '表单ID',
    FORM_NAME AS '表单名称',
    DATASOURCE AS '数据源',
    CHAR_LENGTH(FORM_DSL) AS 'DSL长度',
    CHAR_LENGTH(INIT_EXPRESS) AS '初始化表达式长度',
    CHAR_LENGTH(SUBMIT_EXPRESS) AS '提交表达式长度'
FROM T_EZADMIN_FORM
WHERE FORM_ID = 'test-db-form';

SELECT
    LIST_ID AS '列表ID',
    LIST_NAME AS '列表名称',
    DATASOURCE AS '数据源',
    CHAR_LENGTH(LIST_DSL) AS 'DSL长度',
    CHAR_LENGTH(SELECT_EXPRESS) AS '查询表达式长度',
    CHAR_LENGTH(COUNT_EXPRESS) AS '总数表达式长度'
FROM T_EZADMIN_LIST
WHERE LIST_ID = 'test-db-list';
