package org.example.zhao.module.biz.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student_attendance")
public class StudentAttendance {
    @TableId
    private Long id;
    private Long studentId;
    private Long scheduleId;
    private Integer attendanceStatus;
    private LocalDateTime attendanceTime;
    private String remark;
    private LocalDateTime createTime;
}

