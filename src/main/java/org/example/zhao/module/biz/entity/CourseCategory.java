package org.example.zhao.module.biz.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.zhao.module.common.entity.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_category")
public class CourseCategory extends BaseEntity {
    @TableId
    private Long id;
    private String categoryName;
    private Integer sort;
}

