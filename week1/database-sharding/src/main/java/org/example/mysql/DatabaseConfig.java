package org.example.mysql;

public class DatabaseConfig {
    public static final String SOURCE_URL = System.getProperty("db.source.url", "jdbc:mysql://localhost:3306/hertever");
    public static final String SOURCE_USERNAME = System.getProperty("db.source.username", "root");
    public static final String SOURCE_PASSWORD = System.getProperty("db.source.password", "rohitsingh");
    
    public static final String REPLICA_URL = System.getProperty("db.replica.url", "jdbc:mysql://localhost:3307/hertever");
    public static final String REPLICA_USERNAME = System.getProperty("db.replica.username", "root");
    public static final String REPLICA_PASSWORD = System.getProperty("db.replica.password", "rohitreplica");
}