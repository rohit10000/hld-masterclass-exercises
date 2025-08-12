package com.hertever.food_agent.repository;

import com.hertever.food_agent.dto.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;

@Repository
public class AgentRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentRepository.class);
    
    @Autowired
    private DataSource dataSource;
    
    public Agent findAvailableAgentForUpdate(Connection connection) throws SQLException {
        String sql = "SELECT id, is_reserved, order_id FROM agents WHERE is_reserved = false AND order_id IS NULL FOR UPDATE";
        logger.debug("Executing query to find available agent: {}", sql);
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Agent agent = new Agent(
                    rs.getInt("id"),
                    rs.getBoolean("is_reserved"),
                    rs.getString("order_id")
                );
                logger.debug("Found available agent: {}", agent.getId());
                return agent;
            }
            logger.debug("No available agent found");
            return null;
        }
    }
    
    public Agent findReservedAgentForUpdate(Connection connection) throws SQLException {
        String sql = "SELECT id, is_reserved, order_id FROM agents WHERE is_reserved = true AND order_id IS NULL FOR UPDATE";
        logger.debug("Executing query to find reserved agent: {}", sql);
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Agent agent = new Agent(
                    rs.getInt("id"),
                    rs.getBoolean("is_reserved"),
                    rs.getString("order_id")
                );
                logger.debug("Found reserved agent: {}", agent.getId());
                return agent;
            }
            logger.debug("No reserved agent found");
            return null;
        }
    }
    
    public void updateReservedStatus(Connection connection, Integer agentId, boolean isReserved) throws SQLException {
        String sql = "UPDATE agents SET is_reserved = ? WHERE id = ?";
        logger.debug("Updating agent {} reserved status to: {}", agentId, isReserved);
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isReserved);
            stmt.setInt(2, agentId);
            int rowsUpdated = stmt.executeUpdate();
            logger.debug("Updated {} rows for agent {}", rowsUpdated, agentId);
        }
    }
    
    public void updateOrderId(Connection connection, Integer agentId, String orderId, boolean isReserved) throws SQLException {
        String sql = "UPDATE agents SET is_reserved = ?, order_id = ? WHERE id = ?";
        logger.debug("Updating agent {} with orderId: {} and reserved status: {}", agentId, orderId, isReserved);
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isReserved);
            stmt.setString(2, orderId);
            stmt.setInt(3, agentId);
            int rowsUpdated = stmt.executeUpdate();
            logger.debug("Updated {} rows for agent {} with orderId: {}", rowsUpdated, agentId, orderId);
        }
    }
}