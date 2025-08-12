package com.hertever.food_agent.service;

import com.hertever.food_agent.dto.Agent;
import com.hertever.food_agent.repository.AgentRepository;
import com.hertever.food_agent.util.CustomTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentService {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);
    
    @Autowired
    private CustomTransactionManager customTransactionManager;
    
    @Autowired
    private AgentRepository agentRepository;
    
    public boolean reserve() {
        return customTransactionManager.executeInTransactionWithBoolean("agent-reserve", connection -> {
            Agent agent = agentRepository.findAvailableAgentForUpdate(connection);
            if (agent == null) {
                logger.warn("No available agent found for reservation");
                return false;
            }
            
            logger.info("Found available agent with ID: {}", agent.getId());
            agentRepository.updateReservedStatus(connection, agent.getId(), true);
            logger.debug("Agent {} marked as reserved", agent.getId());
            
            logger.info("Agent {} reservation completed successfully", agent.getId());
            return true;
        });
    }
    
    public boolean book(String orderId) {
        return customTransactionManager.executeInTransactionWithBoolean("agent-book", connection -> {
            Agent agent = agentRepository.findReservedAgentForUpdate(connection);
            if (agent == null) {
                logger.warn("No reserved agent found for booking with orderId: {}", orderId);
                return false;
            }
            
            logger.info("Found reserved agent with ID: {} for booking with orderId: {}", agent.getId(), orderId);
            agentRepository.updateOrderId(connection, agent.getId(), orderId, false);
            logger.debug("Agent {} booked with orderId: {}", agent.getId(), orderId);
            
            logger.info("Agent {} booking completed successfully with orderId: {}", agent.getId(), orderId);
            return true;
        });
    }
}