package org.example.mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostService {
    private final MySQLConnection mySQLConnection;
    // Flag to use either proxy connection or not.
    private final Boolean useProxyConnection = Boolean.TRUE;

    public PostService(MySQLConnection mySQLConnection) {
        this.mySQLConnection = mySQLConnection;
    }

    public List<Post> getPostsByOwner(String owner) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM post WHERE owner = ?";
        Connection connection = useProxyConnection
                ? mySQLConnection.getProxySqlConnection()
                : mySQLConnection.getReplicaConnection();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, owner);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(new Post(
                            rs.getInt("id"),
                            rs.getString("owner"),
                            rs.getString("content")
                    ));
                }
            }
        }
        System.out.println("Data read from replica.");
        return posts;
    }

    public void createPost(String owner, String content) throws SQLException {
        String sql = "INSERT INTO post(owner, content) VALUES(?, ?)";
        Connection connection = useProxyConnection
                ? mySQLConnection.getProxySqlConnection()
                : mySQLConnection.getSourceConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setString(1, owner);
            preparedStatement.setString(2, content);
            preparedStatement.executeUpdate();
        }
        System.out.println("Data written to source");
    }
}