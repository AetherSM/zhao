package org.example.zhao.module.biz.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EnrollReq {
    @NotNull
    private Long studentId;
    @NotNull
    private Long courseId;
}

