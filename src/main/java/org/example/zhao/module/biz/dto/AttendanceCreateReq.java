package org.example.zhao.module.biz.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class AttendanceCreateReq {
    @NotNull
    private Long studentId;
    @NotNull
    private Long scheduleId;
    /**
     * 1 正常 / 2 迟到 / 3 早退 / 4 缺勤
     */
    @NotNull
    private Integer attendanceStatus;
    @NotNull
    private LocalDateTime attendanceTime;
    private String remark;
}

