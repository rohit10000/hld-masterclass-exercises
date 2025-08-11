package org.example.mysql;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

@Getter
public class MySQLConnection implements AutoCloseable {
    private Connection sourceConnection;
    private Connection replicaConnection;
    private Connection proxySqlConnection;

    public MySQLConnection() {
        try {
            createSourceConnection();
            createReplicaConnection();
            /* Todo: proxy sql connection failing to connect. some issue in the docker
                [docker logs -f proxysql] giving error message: Error during query on (1,host.docker.internal,3306,889): 1193,
                Unknown system variable 'query_cache_size'
            */
             createProxySqlConnection();

        } catch (SQLException e) {
            closeConnections();
            System.out.println(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Failed to establish database connections:", e);
        }
    }

    private void createProxySqlConnection() throws SQLException {
        proxySqlConnection = DriverManager.getConnection(
                DatabaseConfig.PROXY_SQL_URL,
                DatabaseConfig.PROXY_SQL_USERNAME,
                DatabaseConfig.PROXY_SQL_PASSWORD
        );
        validateConnection(proxySqlConnection, "proxy sql");
    }


    private void createSourceConnection() throws SQLException {
        sourceConnection = DriverManager.getConnection(
                DatabaseConfig.SOURCE_URL,
                DatabaseConfig.SOURCE_USERNAME,
                DatabaseConfig.SOURCE_PASSWORD
        );
        validateConnection(sourceConnection, "source");
    }

    private void createReplicaConnection() throws SQLException {
        replicaConnection = DriverManager.getConnection(
                DatabaseConfig.REPLICA_URL,
                DatabaseConfig.REPLICA_USERNAME,
                DatabaseConfig.REPLICA_PASSWORD
        );
        validateConnection(replicaConnection, "replica");
    }

    private void validateConnection(Connection connection, String identifier) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            System.out.println(identifier + " connected to mysql database successfully!");
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("Database: " + metaData.getDatabaseProductName());
            System.out.println("Version: " + metaData.getDatabaseProductVersion());
        } else {
            throw new SQLException("Failed to establish " + identifier + " connection");
        }
    }

    private void closeConnections() {
        if (sourceConnection != null) {
            try {
                sourceConnection.close();
            } catch (SQLException e) {
                System.err.println("Error closing source connection: " + e.getMessage());
            }
        }
        if (replicaConnection != null) {
            try {
                replicaConnection.close();
            } catch (SQLException e) {
                System.err.println("Error closing replica connection: " + e.getMessage());
            }
        }
        if (proxySqlConnection != null) {
            try {
                proxySqlConnection.close();
            } catch (SQLException e) {
                System.err.println("Error closing proxy-sql connection: " + e.getMessage());
            }
        }
        System.out.println("Connection closed successfully.");
    }

    @Override
    public void close() {
        closeConnections();
    }
}
