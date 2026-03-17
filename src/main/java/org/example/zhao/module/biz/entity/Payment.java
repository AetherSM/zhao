package org.example.zhao.module.biz.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment")
public class Payment {
    @TableId
    private Long id;
    private Long studentId;
    private Long courseId;
    private BigDecimal amount;
    private LocalDateTime payTime;
    private String payType;
    private String remark;
    private LocalDateTime createTime;
}

