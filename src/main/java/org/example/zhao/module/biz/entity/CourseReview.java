package org.example.zhao.module.biz.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("course_review")
public class CourseReview {
    @TableId
    private Long id;
    private Long studentId;
    private Long courseId;
    private Long teacherId;
    private Integer rating;
    private String comment;
    private LocalDateTime createTime;
    private Integer deleted;
}
