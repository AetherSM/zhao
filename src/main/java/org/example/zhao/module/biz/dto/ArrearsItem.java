package org.example.zhao.module.biz.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ArrearsItem {
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private BigDecimal totalFee;
    private BigDecimal paidFee;
    private BigDecimal arrearsFee;
}

