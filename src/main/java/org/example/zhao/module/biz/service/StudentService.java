package org.example.zhao.module.biz.service;

import org.example.zhao.module.biz.dto.StudentCreateReq;
import org.example.zhao.module.biz.entity.Student;

public interface StudentService {
    Long createStudent(StudentCreateReq req);
    void updateStudent(Long id, Student student);
}
