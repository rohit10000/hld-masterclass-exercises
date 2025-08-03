package org.example.mysql;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class DatabaseSharding {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (MySQLConnection mySQLConnection = new MySQLConnection()) {
            PostService postService = new PostService(mySQLConnection);
            
            System.out.print("Enter operation [read or write]: ");
            String operation = scanner.nextLine().trim();
            
            switch (operation.toLowerCase()) {
                case "read":
                    handleReadOperation(postService);
                    break;
                case "write":
                    handleWriteOperation(postService);
                    break;
                default:
                    System.out.println("Invalid operation. Please enter 'read' or 'write'.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void handleReadOperation(PostService postService) {
        try {
            System.out.print("Enter owner: ");
            String owner = scanner.nextLine().trim();
            
            if (owner.isEmpty()) {
                System.out.println("Owner cannot be empty.");
                return;
            }
            
            List<Post> posts = postService.getPostsByOwner(owner);
            
            if (posts.isEmpty()) {
                System.out.println("No posts found for owner: " + owner);
            } else {
                System.out.println("Found " + posts.size() + " post(s):");
                for (Post post : posts) {
                    System.out.printf("ID: %d, Owner: %s, Content: %s%n", 
                            post.getId(), post.getOwner(), post.getContent());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading posts: " + e.getMessage());
        }
    }

    private static void handleWriteOperation(PostService postService) {
        try {
            System.out.print("Enter owner: ");
            String owner = scanner.nextLine().trim();
            
            if (owner.isEmpty()) {
                System.out.println("Owner cannot be empty.");
                return;
            }
            
            System.out.print("Enter content: ");
            String content = scanner.nextLine().trim();
            
            if (content.isEmpty()) {
                System.out.println("Content cannot be empty.");
                return;
            }
            
            postService.createPost(owner, content);
            System.out.println("Post created successfully!");
        } catch (SQLException e) {
            System.err.println("Error creating post: " + e.getMessage());
        }
    }
}
