package org.example.zhao.module.biz.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentCreateReq {
    @NotNull
    private Long studentId;
    @NotNull
    private Long courseId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private LocalDateTime payTime;
    @NotBlank
    private String payType;
    private String remark;
}

