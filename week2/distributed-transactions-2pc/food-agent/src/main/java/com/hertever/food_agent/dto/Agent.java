package com.hertever.food_agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agent {
    private Integer id;
    private boolean isReserved;
    private String orderId;
}