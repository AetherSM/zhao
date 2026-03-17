package org.example.zhao.module.biz.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.zhao.module.common.entity.BaseEntity;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_schedule")
public class CourseSchedule extends BaseEntity {
    @TableId
    private Long id;
    private Long courseId;
    private Long teacherId;
    private String classroom;
    private LocalDate scheduleDate;
    private String classPeriod;
}

