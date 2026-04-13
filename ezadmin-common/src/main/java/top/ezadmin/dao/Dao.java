package top.ezadmin.dao;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.NumberUtils;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dao {
    public static final Logger log = LoggerFactory.getLogger(Dao.class);

    private static Dao dao = new Dao();

    private static int IO_BUFFER_SIZE = 4096;

    private Dao() {

    }

    public static Dao getInstance() {
        return dao;
    }

    public Map<String, Object> executeQueryOne(DataSource dataSource, String sql, Object[] bindArgs) throws Exception {
        List<Map<String, Object>> list = executeQuery(dataSource, sql, bindArgs);
        return list.size() > 0 ? list.get(0) : new HashMap<String, Object>();
    }

    /**
     * 兼容blob
     *
     * @throws Exception
     */
    public List<Map<String, String>> executeQueryString(DataSource dataSource, String sql, Object[] bindArgs) throws Exception {
        List<Map<String, String>> datas = new ArrayList<>(0);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            if (bindArgs != null) {
                for (int i = 0; i < bindArgs.length; i++) {
                    preparedStatement.setObject(i + 1, bindArgs[i]);
                }
            }
            resultSet = preparedStatement.executeQuery();
            datas = getDatasString(resultSet);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e2) {
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e3) {
            }
        }
        return datas;
    }

    public List<Map<String, Object>> executeQuery(DataSource dataSource, String sql, Object[] bindArgs) throws Exception {
        return executeQuery(dataSource, sql, bindArgs,true);
    }
    public List<Map<String, Object>> executeQuery(DataSource dataSource, String sql, Object[] bindArgs,boolean upperCaseFieldName) throws Exception {
        List<Map<String, Object>> datas = new ArrayList<>(0);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            if(dataSource==null){
                Utils.addLog("dataSource is null");
                return datas;
            }
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            if (bindArgs != null) {
                for (int i = 0; i < bindArgs.length; i++) {
                    if (bindArgs[i] instanceof Integer) {
                        preparedStatement.setInt(i + 1, NumberUtils.toInt(Utils.trimNull(bindArgs[i])));
                    } else if (bindArgs[i] instanceof String) {
                        preparedStatement.setString(i + 1, Utils.trimNull(bindArgs[i]));
                    } else {
                        preparedStatement.setObject(i + 1, bindArgs[i]);
                    }
                }
            }
            resultSet = preparedStatement.executeQuery();
            datas = getDatas(resultSet,upperCaseFieldName);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e2) {
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e3) {
            }
        }
        return datas;
    }


    public long executeUpdate(DataSource dataSource, String sql, Object[] bindArgs) throws Exception {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (bindArgs != null) {
                for (int i = 0; i < bindArgs.length; i++) {
                    if (bindArgs[i] instanceof Date) {
                        preparedStatement.setTimestamp(i + 1, new Timestamp(((Date) bindArgs[i]).getTime()));
                    } else if (bindArgs[i] instanceof ClobParam) {
                        String text = ((ClobParam) bindArgs[i]).getClob();
                        Clob clob = connection.createClob();
                        clob.setString(1, text);
                        preparedStatement.setClob(i + 1, clob);
                    } else if (bindArgs[i] instanceof InputStream) {
                        preparedStatement.setBinaryStream(i + 1, (InputStream) bindArgs[i]);
                    } else {
                        preparedStatement.setObject(i + 1, bindArgs[i]);
                    }
                }
            }
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            while (generatedKeys.next()) {
                long generateKey = generatedKeys.getLong(1);

                return generateKey;
            }
        } catch (Exception e) {
            Utils.addLog("sql:" + sql + ",param:" + JSONUtils.toJSONString(bindArgs), e);
            throw e;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e2) {
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e3) {
            }
        }
        return 0;
    }

    public long executeUpdateNotClose(Connection connection, String sql, Object[] bindArgs) throws Exception {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (bindArgs != null) {
                for (int i = 0; i < bindArgs.length; i++) {
                    if (bindArgs[i] instanceof Date) {
                        preparedStatement.setTimestamp(i + 1, new Timestamp(((Date) bindArgs[i]).getTime()));
                    }
                    if (bindArgs[i] instanceof ClobParam) {
                        String text = ((ClobParam) bindArgs[i]).getClob();
                        Clob clob = connection.createClob();
                        clob.setString(1, text);
                        preparedStatement.setClob(i + 1, clob);
                    } else {
                        preparedStatement.setObject(i + 1, bindArgs[i]);
                    }
                }
            }
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            while (generatedKeys.next()) {
                long generateKey = generatedKeys.getLong(1);

                return generateKey;
            }
        } catch (Exception e) {
            throw e;
        } finally {
        }
        return 0;
    }


    public Long executeCountQuery(DataSource dataSource, String sql, Object[] bindArgs) throws Exception {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            if (bindArgs != null) {
                for (int i = 0; i < bindArgs.length; i++) {
                    preparedStatement.setObject(i + 1, bindArgs[i]);
                }
            }
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long count = resultSet.getLong(1);
                return count;
            }
            return 0l;

        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e2) {
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e3) {
            }
        }
    }

    public List<Long> executeListOneQuery(DataSource dataSource, String sql, Object[] bindArgs) throws Exception {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            if (bindArgs != null) {
                for (int i = 0; i < bindArgs.length; i++) {
                    preparedStatement.setObject(i + 1, bindArgs[i]);
                }
            }
            List<Long> list = new ArrayList<>();
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long count = resultSet.getLong(1);
                list.add(count);
            }
            return list;

        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e2) {
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e3) {
            }
        }
    }


    /**
     * 将结果集对象封装成List<Map<String, Object>> 对象
     *
     * @param resultSet 结果多想
     * @throws Exception
     */
    private static List<Map<String, String>> getDatasString(ResultSet resultSet) throws Exception {
        List<Map<String, String>> datas = new ArrayList<>();
        /**获取结果集的数据结构对象**/
        ResultSetMetaData metaData = resultSet.getMetaData();
        while (resultSet.next()) {
            Map<String, String> rowMap = new HashMap<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                try {
                    Object objectValue = resultSet.getObject(i);


                    if (objectValue != null && objectValue instanceof Timestamp) {
                        objectValue = objectValue.toString();
                    }
                    if (objectValue != null && objectValue instanceof Clob) {
                        Clob clob = (Clob) objectValue;


                        int ll = IO_BUFFER_SIZE;
                        if (clob.length() > IO_BUFFER_SIZE) {
                            ll = Integer.valueOf(clob.length() + "");
                        }
                        StringWriter writer = new StringWriter(ll);
                        try (Reader reader = clob.getCharacterStream()) {

                            Utils.copyAndCloseInput(reader, writer, ll);
                        }
                        objectValue = writer.toString();
                    }


                    if (StringUtils.isNotBlank(metaData.getColumnLabel(i))) {
                        rowMap.put(StringUtils.upperCase(metaData.getColumnLabel(i)), Utils.trimNull(objectValue));
                    } else {
                        rowMap.put(StringUtils.upperCase(metaData.getColumnName(i)), Utils.trimNull(objectValue));
                    }
                } catch (Exception e) {
                    log.error("", e);
                    rowMap.put(StringUtils.upperCase(metaData.getColumnName(i)), "");
                }
            }
            datas.add(rowMap);
        }
        return datas;
    }

    /**
     * 将结果集对象封装成List<Map<String, Object>> 对象
     *
     * @param resultSet 结果多想
     * @throws Exception
     */
    private static List<Map<String, Object>> getDatas(ResultSet resultSet,boolean upperCaseFieldName) throws Exception {
        List<Map<String, Object>> datas = new ArrayList<>();
        /**获取结果集的数据结构对象**/
        ResultSetMetaData metaData = resultSet.getMetaData();
        while (resultSet.next()) {
            Map<String, Object> rowMap = new HashMap<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                try {
                    Object objectValue = resultSet.getObject(i);


                    if (objectValue != null && objectValue instanceof Timestamp) {
                        objectValue = objectValue.toString();
                    } else if (objectValue != null && objectValue instanceof Clob) {
                        Clob clob = (Clob) objectValue;


                        int ll = IO_BUFFER_SIZE;
                        if (clob.length() > IO_BUFFER_SIZE) {
                            ll = Integer.valueOf(clob.length() + "");
                        }
                        StringWriter writer = new StringWriter(ll);
                        try (Reader reader = clob.getCharacterStream()) {

                            Utils.copyAndCloseInput(reader, writer, ll);
                        }
                        objectValue = writer.toString();
                    } else if (objectValue != null && objectValue instanceof Boolean) {
                        //tinyint 默认转成int
                        objectValue = resultSet.getInt(i);
//
//                        if( Boolean.parseBoolean(Utils.trimNull(objectValue))){
//                            objectValue="1";
//                        }else{
//                            objectValue="0";
//                        }
                    }

                    if (!(objectValue instanceof byte[])) {
                        objectValue =  objectValue ;
                    } else {
                        objectValue = new String((byte[]) objectValue);
                    }

                    if (StringUtils.isNotBlank(metaData.getColumnLabel(i))) {
                        String columnName =  metaData.getColumnLabel(i);
                        if (upperCaseFieldName) {
                            columnName=StringUtils.upperCase(metaData.getColumnLabel(i));
                        }
                        String label = StringUtils.upperCase(metaData.getColumnLabel(i));
                        if (StringUtils.contains(label, "BOOLEAN")) {
                            rowMap.put(columnName, Utils.isTrue(objectValue));
                        } else {
                            rowMap.put(columnName, objectValue);
                        }
                    } else {
                        String columnName =  metaData.getColumnName(i);
                        if (upperCaseFieldName) {
                            columnName=StringUtils.upperCase(metaData.getColumnName(i));
                        }
                        rowMap.put(columnName, objectValue);
                    }
                } catch (Exception e) {
                    log.error("", e);
                    String columnName =  metaData.getColumnName(i);
                    if (upperCaseFieldName) {
                        columnName=StringUtils.upperCase(metaData.getColumnName(i));
                    }
                    rowMap.put(columnName, "");
                }
            }
            datas.add(rowMap);
        }
        return datas;
    }

}
