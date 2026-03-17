package org.example.zhao.module.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RevenueItem {
    private String name;
    private BigDecimal value;
}

