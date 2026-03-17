package org.example.zhao.module.biz.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class ScheduleCreateReq {
    @NotNull
    private Long courseId;
    @NotNull
    private Long teacherId;
    @NotBlank
    private String classroom;
    @NotNull
    private LocalDate scheduleDate;
    @NotBlank
    private String classPeriod;
}

