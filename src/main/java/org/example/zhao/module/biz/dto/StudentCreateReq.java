package org.example.zhao.module.biz.dto;

import lombok.Data;
import org.example.zhao.module.biz.entity.Student;

@Data
public class StudentCreateReq extends Student {
    private String username;
    private String password;
}
