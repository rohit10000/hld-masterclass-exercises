package org.example.mysql;

public class DatabaseConfig {
    public static final String SOURCE_URL = System.getProperty("db.source.url", "jdbc:mysql://localhost:3306/hertever");
    public static final String SOURCE_USERNAME = System.getProperty("db.source.username", "root");
    public static final String SOURCE_PASSWORD = System.getProperty("db.source.password", "rohitsingh");

    public static final String REPLICA_URL = System.getProperty("db.replica.url", "jdbc:mysql://localhost:3307/hertever");
    public static final String REPLICA_USERNAME = System.getProperty("db.replica.username", "root");
    public static final String REPLICA_PASSWORD = System.getProperty("db.replica.password", "rohitsingh");

    // ProxySQL URL with compatibility parameters
    public static final String PROXY_SQL_URL = System.getProperty("db.proxysql.url", buildProxySqlUrl());
    public static final String PROXY_SQL_USERNAME = System.getProperty("db.proxysql.username", "root");
    public static final String PROXY_SQL_PASSWORD = System.getProperty("db.proxysql.password", "rohitsingh");

    private static String buildProxySqlUrl() {
        return "jdbc:mysql://127.0.0.1:6033/hertever?" +
                "useSSL=false&" +
                "allowPublicKeyRetrieval=true&" +
                "serverTimezone=UTC&" +
                "useJDBCCompliantTimezoneShift=true&" +
                "useLegacyDatetimeCode=false&" +
                "useInformationSchema=true&" +
                "nullCatalogMeansCurrent=true&" +
                "autoReconnect=true";
    }
}