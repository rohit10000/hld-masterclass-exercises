package org.example.mysql;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class MySQLConnection implements AutoCloseable {
    private Connection sourceConnection;
    private Connection replicaConnection;

    public MySQLConnection() {
        try {
            createSourceConnection();
            createReplicaConnection();
        } catch (SQLException e) {
            closeConnections();
            throw new RuntimeException("Failed to establish database connections", e);
        }
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
        System.out.println("Connection closed successfully.");
    }

    @Override
    public void close() {
        closeConnections();
    }
}
