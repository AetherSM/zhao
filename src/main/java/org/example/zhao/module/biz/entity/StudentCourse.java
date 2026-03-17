package org.example.zhao.module.biz.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student_course")
public class StudentCourse {
    @TableId
    private Long id;
    private Long studentId;
    private Long courseId;
    private LocalDateTime signTime;
    private LocalDateTime createTime;
}

