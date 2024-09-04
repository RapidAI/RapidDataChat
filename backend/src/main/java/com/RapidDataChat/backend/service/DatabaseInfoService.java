package com.RapidDataChat.backend.service;

import com.RapidDataChat.backend.model.ColumnInfo;
import com.RapidDataChat.backend.model.DatabaseInfo;
import com.RapidDataChat.backend.model.TableInfo;
import com.RapidDataChat.backend.repository.ColumnInfoRepository;
import com.RapidDataChat.backend.repository.DatabaseInfoRepository;
import com.RapidDataChat.backend.repository.TableInfoRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.RapidDataChat.backend.util.DatabaseUtil.createDataSource;
@Service
@Slf4j
public class DatabaseInfoService {

    @Autowired
    private DatabaseInfoRepository databaseInfoRepository;

    @Autowired
    private TableInfoRepository tableInfoRepository;

    @Autowired
    private ColumnInfoRepository columnInfoRepository;

    @Autowired
    private ChatService chatService;

    // 查询功能

    /**
     * 查询所有数据库信息
     *
     * @return 所有数据库信息的列表
     */
    public List<DatabaseInfo> findAll() {
        return databaseInfoRepository.selectList(null);
    }

    /**
     * 根据ID查询数据库信息
     *
     * @param id 数据库ID
     * @return 对应的数据库信息
     */
    public DatabaseInfo findById(int id) {
        return databaseInfoRepository.selectById(id);
    }

    // 保存和更新功能

    /**
     * 保存新的数据库信息
     *
     * @param databaseInfo 要保存的数据库信息
     * @return 保存后的数据库信息
     */
    public DatabaseInfo save(DatabaseInfo databaseInfo) {
        databaseInfoRepository.insert(databaseInfo);
        return databaseInfo;
    }

    /**
     * 更新数据库信息
     *
     * @param databaseInfo 更新后的数据库信息
     * @param id           要更新的数据库ID
     * @return 更新是否成功
     */
    @Transactional
    public boolean update(DatabaseInfo databaseInfo, int id) {
        DatabaseInfo existingDatabaseInfo = databaseInfoRepository.selectById(id);
        if (existingDatabaseInfo != null) {
            existingDatabaseInfo.setDatabaseName(databaseInfo.getDatabaseName());
            existingDatabaseInfo.setHost(databaseInfo.getHost());
            existingDatabaseInfo.setPort(databaseInfo.getPort());
            existingDatabaseInfo.setUsername(databaseInfo.getUsername());
            existingDatabaseInfo.setPassword(databaseInfo.getPassword());
            existingDatabaseInfo.setAuthMethod(databaseInfo.getAuthMethod());
            existingDatabaseInfo.setDatabaseType(databaseInfo.getDatabaseType());
            existingDatabaseInfo.setDatabaseDescription(databaseInfo.getDatabaseDescription());
            databaseInfoRepository.updateById(existingDatabaseInfo);
            return true;
        }
        return false;
    }

    // 删除功能

    /**
     * 删除数据库信息及其相关表和列信息
     *
     * @param id 要删除的数据库ID
     * @return 删除是否成功
     */
    @Transactional
    public boolean delete(int id) {
        DatabaseInfo databaseInfo = databaseInfoRepository.selectById(id);
        if (databaseInfo != null) {
            List<TableInfo> tables = tableInfoRepository.selectByDatabaseInfoId(id);
            for (TableInfo table : tables) {
                columnInfoRepository.deleteByTableInfoId(table.getTableInfoId());
            }
            tableInfoRepository.deleteByDatabaseInfoId(id);
            databaseInfoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 验证功能

    /**
     * 验证数据库连接
     *
     * @param databaseInfo 要验证的数据库信息
     * @return 连接失败的错误信息，成功则返回 null
     */
    public String validateDatabaseConnection(DatabaseInfo databaseInfo) {
        DataSource dataSource = null;
        try {
            // 创建数据源
            dataSource = createDataSource(databaseInfo);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            // 执行测试查询
            jdbcTemplate.execute("SELECT 1");
            return null; // 连接成功，返回 null
        } catch (Exception e) {
            // 连接失败，返回详细错误信息，包括 cause 的详细信息
            String errorMessage = "数据库连接失败：" + e.getMessage();
            Throwable cause = e.getCause();
            if (cause != null) {
                errorMessage += "，详细信息：" + cause.getMessage();
            }
            return errorMessage;
        } finally {
            if (dataSource != null) {
                try {
                    // 关闭连接
                    dataSource.getConnection().close();
                } catch (SQLException e) {
                    // 处理关闭连接时的异常，但不影响返回结果
                }
            }
        }
    }


    /**
     * 同步数据库表、视图和列信息
     *
     * @param databaseInfo 要同步的数据库信息
     * @return 同步是否成功
     * @throws SQLException
     */
    @SneakyThrows
    @Transactional
    public boolean sync(DatabaseInfo databaseInfo) {
        DataSource dataSource = createDataSource(databaseInfo);
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = getSchema(databaseInfo); // 获取模式信息
            log.info("Catalog: " + catalog + " Schema: " + schema);

            Set<String> currentTablesAndViews = new HashSet<>();

            // 同步表信息
            syncTables(connection, metaData, catalog, schema, databaseInfo, currentTablesAndViews);

            // 同步视图信息
            syncViews(connection, metaData, catalog, schema, databaseInfo, currentTablesAndViews);

            // 标记不存在的表和视图
            markAbsentTables(databaseInfo, currentTablesAndViews);

            // 更新同步时间
            databaseInfo.setSyncTime(new Date());
            databaseInfoRepository.updateById(databaseInfo);
            return true;
        }
    }

    /**
     * 同步表信息
     *
     * @param connection 数据库连接
     * @param metaData   数据库元数据
     * @param catalog    数据库目录
     * @param schema     模式名
     * @param databaseInfo 数据库信息
     * @param currentTablesAndViews 当前存在的表和视图集合
     * @throws SQLException
     */
    private void syncTables(Connection connection, DatabaseMetaData metaData, String catalog, String schema, DatabaseInfo databaseInfo, Set<String> currentTablesAndViews) throws SQLException {
        ResultSet tables = metaData.getTables(catalog, schema, "%", new String[]{"TABLE"});
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            String tableComment = getTableComment(connection, schema, tableName, databaseInfo.getDatabaseType());
            currentTablesAndViews.add(tableName);

            TableInfo tableInfo = tableInfoRepository.selectByDatabaseInfoIdAndTableName(databaseInfo.getDatabaseInfoId(), tableName);
            if (tableInfo == null) {
                tableInfo = new TableInfo();
                tableInfo.setDatabaseInfoId(databaseInfo.getDatabaseInfoId());
                tableInfo.setTableName(tableName);
                tableInfo.setTableComment(tableComment);
                tableInfoRepository.insert(tableInfo);
            } else {
                tableInfo.setTableComment(tableComment);
                tableInfoRepository.updateById(tableInfo);
            }

            // 同步列信息
            syncColumns(metaData, catalog, schema, tableInfo, databaseInfo.getDatabaseType());
        }
    }


    /**
     * 同步视图信息
     *
     * @param connection 数据库连接
     * @param metaData   数据库元数据
     * @param catalog    数据库目录
     * @param schema     模式名
     * @param databaseInfo 数据库信息
     * @param currentTablesAndViews 当前存在的表和视图集合
     * @throws SQLException
     */
    private void syncViews(Connection connection, DatabaseMetaData metaData, String catalog, String schema, DatabaseInfo databaseInfo, Set<String> currentTablesAndViews) throws SQLException {
        ResultSet views = metaData.getTables(catalog, schema, "%", new String[]{"VIEW"});
        while (views.next()) {
            String viewName = views.getString("TABLE_NAME");
            String viewComment = getTableComment(connection, schema, viewName, databaseInfo.getDatabaseType());
            currentTablesAndViews.add(viewName);

            TableInfo viewInfo = tableInfoRepository.selectByDatabaseInfoIdAndTableName(databaseInfo.getDatabaseInfoId(), viewName);
            if (viewInfo == null) {
                viewInfo = new TableInfo();
                viewInfo.setDatabaseInfoId(databaseInfo.getDatabaseInfoId());
                viewInfo.setTableName(viewName);
                viewInfo.setTableComment(viewComment);
                tableInfoRepository.insert(viewInfo);
            } else {
                viewInfo.setTableComment(viewComment);
                tableInfoRepository.updateById(viewInfo);
            }

            // 同步视图的列信息
            syncColumns(metaData, catalog, schema, viewInfo, databaseInfo.getDatabaseType());
        }
    }
    /**
     * 获取表的注释
     *
     * @param connection 数据库连接
     * @param schema     模式名
     * @param tableName  表名
     * @param dbType     数据库类型
     * @return 表的注释
     * @throws SQLException
     */
    private String getTableComment(Connection connection, String schema, String tableName, String dbType) throws SQLException {
        String query;
        switch (dbType.toLowerCase()) {
            case "postgresql":
                query = "SELECT obj_description(oid) AS table_comment FROM pg_class WHERE relname = ? AND relnamespace = (SELECT oid FROM pg_namespace WHERE nspname = ?)";
                break;
            case "mysql":
            case "mariadb":
                query = "SELECT table_comment FROM information_schema.tables WHERE table_schema = ? AND table_name = ?";
                break;
            // fixme 其他数据库类型的查询语句
            default:
                throw new UnsupportedOperationException("Unsupported database type: " + dbType);
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            if (dbType.equalsIgnoreCase("mysql") || dbType.equalsIgnoreCase("mariadb")) {
                stmt.setString(1, schema);
                stmt.setString(2, tableName);
            } else {
                stmt.setString(1, tableName);
                stmt.setString(2, schema);
            }

            // 打印出查询语句和参数，调试用
            System.out.println("Executing query: " + query);
            System.out.println("With parameters: schema=" + schema + ", tableName=" + tableName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("table_comment");
                }
            }
        }
        return "";
    }

    /**
     * 同步列信息
     *
     * @param metaData  数据库元数据
     * @param catalog   数据库目录
     * @param schema    模式名
     * @param tableInfo 表信息
     * @param dbType    数据库类型
     * @throws SQLException
     */
    private void syncColumns(DatabaseMetaData metaData, String catalog, String schema, TableInfo tableInfo, String dbType) throws SQLException {
        ResultSet columns = metaData.getColumns(catalog, schema, tableInfo.getTableName(), "%");
        Set<String> currentColumns = new HashSet<>();

        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String columnComment = columns.getString("REMARKS");
            String dataType = columns.getString("TYPE_NAME");
            currentColumns.add(columnName);

            ColumnInfo columnInfo = columnInfoRepository.selectByTableInfoIdAndColumnName(tableInfo.getTableInfoId(), columnName);
            if (columnInfo == null) {
                columnInfo = new ColumnInfo();
                columnInfo.setTableInfoId(tableInfo.getTableInfoId());
                columnInfo.setDatabaseInfoId(tableInfo.getDatabaseInfoId());
                columnInfo.setColumnName(columnName);
                columnInfo.setColumnComment(columnComment);
                columnInfo.setDataType(dataType);
                columnInfo.setIsCurrent(true);
                columnInfoRepository.insert(columnInfo);
            } else {
                columnInfo.setColumnComment(columnComment);
                columnInfo.setDataType(dataType);
                columnInfo.setIsCurrent(true);
                columnInfoRepository.updateById(columnInfo);
            }
        }

        // 标记不存在的列
        markAbsentColumns(tableInfo, currentColumns);
    }

    /**
     * 获取模式信息
     *
     * @param databaseInfo 数据库信息
     * @return 模式名
     */
    private String getSchema(DatabaseInfo databaseInfo) {
        String dbType = databaseInfo.getDatabaseType().toLowerCase();
        switch (dbType) {
            case "postgresql":
                return "public";
            case "hive":
            case "kingbase":
            case "mysql":
                return databaseInfo.getDatabaseName();
            // fixme 其他数据库类型的模式处理
            default:
                return null; // 默认不使用schema
        }
    }

    /**
     * 标记不存在的表
     *
     * @param databaseInfo  数据库信息
     * @param currentTables 当前存在的表集合
     */
    private void markAbsentTables(DatabaseInfo databaseInfo, Set<String> currentTables) {
        List<TableInfo> existingTables = tableInfoRepository.selectByDatabaseInfoId(databaseInfo.getDatabaseInfoId());
        for (TableInfo existingTable : existingTables) {
            if (!currentTables.contains(existingTable.getTableName())) {
                tableInfoRepository.deleteById(existingTable.getTableInfoId());
            }
        }
    }

    /**
     * 标记不存在的列
     *
     * @param tableInfo      表信息
     * @param currentColumns 当前存在的列集合
     */
    private void markAbsentColumns(TableInfo tableInfo, Set<String> currentColumns) {
        List<ColumnInfo> existingColumns = columnInfoRepository.selectByTableInfoId(tableInfo.getTableInfoId());
        for (ColumnInfo existingColumn : existingColumns) {
            if (!currentColumns.contains(existingColumn.getColumnName())) {
                existingColumn.setIsCurrent(false);
                columnInfoRepository.updateById(existingColumn);
            }
        }
    }

}
