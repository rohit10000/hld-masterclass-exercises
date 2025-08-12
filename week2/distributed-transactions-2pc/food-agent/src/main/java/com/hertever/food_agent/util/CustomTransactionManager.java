package com.hertever.food_agent.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class CustomTransactionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomTransactionManager.class);
    
    @FunctionalInterface
    public interface TransactionOperation<T> {
        T execute(Connection connection) throws SQLException;
    }
    
    @Autowired
    private DataSource dataSource;
    
    public <T> T executeInTransaction(String operationName, TransactionOperation<T> operation) {
        logger.debug("Starting transaction for operation: {}", operationName);
        Connection connection = null;
        
        try {
            connection = dataSource.getConnection();
            logger.debug("Database connection obtained for operation: {}", operationName);
            connection.setAutoCommit(false);
            logger.debug("Transaction started for operation: {}", operationName);
            
            T result = operation.execute(connection);
            
            connection.commit();
            logger.info("Transaction committed successfully for operation: {}", operationName);
            return result;
            
        } catch (Exception e) {
            logger.error("Error during transaction for operation: {}", operationName, e);
            if (connection != null) {
                try {
                    connection.rollback();
                    logger.debug("Transaction rolled back for operation: {}", operationName);
                } catch (SQLException rollbackEx) {
                    logger.error("Error during rollback for operation: {}", operationName, rollbackEx);
                }
            }
            throw new RuntimeException("Transaction failed for operation: " + operationName, e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    logger.debug("Database connection closed for operation: {}", operationName);
                } catch (SQLException e) {
                    logger.error("Error closing connection for operation: {}", operationName, e);
                }
            }
        }
    }
    
    public boolean executeInTransactionWithBoolean(String operationName, TransactionOperation<Boolean> operation) {
        try {
            return executeInTransaction(operationName, operation);
        } catch (RuntimeException e) {
            logger.error("Transaction operation failed: {}", operationName, e);
            return false;
        }
    }
}