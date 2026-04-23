package org.example.zhao.module.biz.dto;

import lombok.Data;
import org.example.zhao.module.biz.entity.Teacher;

@Data
public class TeacherCreateReq extends Teacher {
    private String username;
    private String password;
}
