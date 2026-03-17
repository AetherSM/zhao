package org.example.zhao.module.biz.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.zhao.module.common.entity.BaseEntity;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course")
public class Course extends BaseEntity {
    @TableId
    private Long id;
    private String courseName;
    private Long categoryId;
    private BigDecimal price;
    private Integer totalHours;
    private String applyGrade;
    private String description;
}

