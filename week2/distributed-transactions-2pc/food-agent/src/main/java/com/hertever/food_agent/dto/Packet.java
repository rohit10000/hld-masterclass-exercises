package com.hertever.food_agent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Packet {
    private Integer id;
    private Integer foodId;
    private boolean isReserved;
    private String orderId;
}