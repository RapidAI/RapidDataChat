package com.RapidDataChat.backend.util;

import com.RapidDataChat.backend.model.DatabaseInfo;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

public class DatabaseUtil {
    /**
     * 获取JDBC URL
     *
     * @param databaseInfo 数据库信息
     * @return JDBC URL
     */
    public static String getJdbcUrl(DatabaseInfo databaseInfo) {
        String hostPort = databaseInfo.getHost() + ":" + databaseInfo.getPort();
        String dbName = databaseInfo.getDatabaseName();
        switch (databaseInfo.getDatabaseType().toLowerCase()) {
            case "mysql":
                return "jdbc:mysql://" + hostPort + "/" + dbName;
            case "mariadb":
                return "jdbc:mariadb://" + hostPort + "/" + dbName;
            case "damengdm8":
                return "jdbc:dm://" + hostPort;
            case "xunwen2":
                return "jdbc:xunwen2://" + hostPort + "/" + dbName;
            case "clickhouse":
                return "jdbc:clickhouse://" + hostPort + "/" + dbName;
            case "db2":
                return "jdbc:db2://" + hostPort + "/" + dbName;
            case "h2":
                return "jdbc:h2:tcp://" + hostPort + "/" + dbName;
            case "hive":
                return "jdbc:hive2://" + hostPort + "/" + dbName;
            case "kingbase":
                return "jdbc:kingbase://" + hostPort + "/" + dbName;
            case "mongodb":
                return "mongodb://" + hostPort + "/" + dbName;
            case "oceanbase":
                return "jdbc:oceanbase://" + hostPort + "/" + dbName;
            case "oracle":
                return "jdbc:oracle:thin:@" + hostPort + ":" + dbName;
            case "postgresql":
                return "jdbc:postgresql://" + hostPort + "/" + dbName;
            case "prestodb":
                return "jdbc:presto://" + hostPort + "/" + dbName;
            case "redis":
                return "redis://" + hostPort;
            case "snowflake":
                return "jdbc:snowflake://" + hostPort + "/" + dbName;
            case "sqlite":
                return "jdbc:sqlite:" + dbName;
            case "sqlserver":
                return "jdbc:sqlserver://" + hostPort + ";databaseName=" + dbName;
            default:
                throw new IllegalArgumentException("Unsupported database type: " + databaseInfo.getDatabaseType());
        }
    }

    //创建数据库连接
    public static DataSource createDataSource(DatabaseInfo databaseInfo) {
        String jdbcUrl = getJdbcUrl(databaseInfo);
        return new SingleConnectionDataSource(jdbcUrl, databaseInfo.getUsername(), databaseInfo.getPassword(), false);
    }
}
