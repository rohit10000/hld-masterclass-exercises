package com.hertever.food_agent.service;

import com.hertever.food_agent.dto.Packet;
import com.hertever.food_agent.repository.PacketRepository;
import com.hertever.food_agent.util.CustomTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FoodService {
    
    private static final Logger logger = LoggerFactory.getLogger(FoodService.class);
    
    @Autowired
    private CustomTransactionManager customTransactionManager;
    
    @Autowired
    private PacketRepository packetRepository;
    
    public boolean reserve(int foodId) {
        return customTransactionManager.executeInTransactionWithBoolean("food-reserve", connection -> {
            Packet packet = packetRepository.findAvailablePacketByFoodIdForUpdate(connection, foodId);
            if (packet == null) {
                logger.warn("No available food packet found for foodId: {}", foodId);
                return false;
            }
            
            logger.info("Found available food packet with ID: {} for foodId: {}", packet.getId(), foodId);
            packetRepository.updateReservedStatus(connection, packet.getId(), true);
            logger.debug("Food packet {} marked as reserved", packet.getId());
            
            logger.info("Food packet {} reservation completed successfully for foodId: {}", packet.getId(), foodId);
            return true;
        });
    }
    
    public boolean book(String orderId, Integer foodId) {
        return customTransactionManager.executeInTransactionWithBoolean("food-book", connection -> {
            Packet packet = packetRepository.findReservedPacketByFoodIdForUpdate(connection, foodId);
            if (packet == null) {
                logger.warn("No reserved food packet found for orderId: {} and foodId: {}", orderId, foodId);
                return false;
            }
            
            logger.info("Found reserved food packet with ID: {} for orderId: {} and foodId: {}", packet.getId(), orderId, foodId);
            packetRepository.updateOrderId(connection, packet.getId(), orderId, false);
            logger.debug("Food packet {} booked with orderId: {}", packet.getId(), orderId);
            
            logger.info("Food packet {} booking completed successfully for orderId: {} and foodId: {}", packet.getId(), orderId, foodId);
            return true;
        });
    }
}