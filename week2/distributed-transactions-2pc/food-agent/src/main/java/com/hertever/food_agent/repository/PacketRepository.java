package com.hertever.food_agent.repository;

import com.hertever.food_agent.dto.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;

@Repository
public class PacketRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(PacketRepository.class);
    
    @Autowired
    private DataSource dataSource;
    
    public Packet findAvailablePacketByFoodIdForUpdate(Connection connection, int foodId) throws SQLException {
        String sql = "SELECT id, food_id, is_reserved, order_id FROM packets WHERE food_id = ? AND is_reserved = false AND order_id IS NULL FOR UPDATE";
        logger.debug("Executing query to find available packet for foodId {}: {}", foodId, sql);
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, foodId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Packet packet = new Packet(
                    rs.getInt("id"),
                    rs.getInt("food_id"),
                    rs.getBoolean("is_reserved"),
                    rs.getString("order_id")
                );
                logger.debug("Found available packet: {} for foodId: {}", packet.getId(), foodId);
                return packet;
            }
            logger.debug("No available packet found for foodId: {}", foodId);
            return null;
        }
    }
    
    public Packet findReservedPacketByFoodIdForUpdate(Connection connection, Integer foodId) throws SQLException {
        String sql = "SELECT id, food_id, is_reserved, order_id FROM packets WHERE food_id = ? AND is_reserved = true AND order_id IS NULL FOR UPDATE";
        logger.debug("Executing query to find reserved packet for foodId {}: {}", foodId, sql);
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, foodId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Packet packet = new Packet(
                    rs.getInt("id"),
                    rs.getInt("food_id"),
                    rs.getBoolean("is_reserved"),
                    rs.getString("order_id")
                );
                logger.debug("Found reserved packet: {} for foodId: {}", packet.getId(), foodId);
                return packet;
            }
            logger.debug("No reserved packet found for foodId: {}", foodId);
            return null;
        }
    }
    
    public void updateReservedStatus(Connection connection, Integer packetId, boolean isReserved) throws SQLException {
        String sql = "UPDATE packets SET is_reserved = ? WHERE id = ?";
        logger.debug("Updating packet {} reserved status to: {}", packetId, isReserved);
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isReserved);
            stmt.setInt(2, packetId);
            int rowsUpdated = stmt.executeUpdate();
            logger.debug("Updated {} rows for packet {}", rowsUpdated, packetId);
        }
    }
    
    public void updateOrderId(Connection connection, Integer packetId, String orderId, boolean isReserved) throws SQLException {
        String sql = "UPDATE packets SET is_reserved = ?, order_id = ? WHERE id = ?";
        logger.debug("Updating packet {} with orderId: {} and reserved status: {}", packetId, orderId, isReserved);
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isReserved);
            stmt.setString(2, orderId);
            stmt.setInt(3, packetId);
            int rowsUpdated = stmt.executeUpdate();
            logger.debug("Updated {} rows for packet {} with orderId: {}", rowsUpdated, packetId, orderId);
        }
    }
}