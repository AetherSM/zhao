package org.example.zhao.module.biz.service;

import org.example.zhao.module.biz.dto.TeacherCreateReq;
import org.example.zhao.module.biz.entity.Teacher;

public interface TeacherService {
    Long createTeacher(TeacherCreateReq req);
    void updateTeacher(Long id, Teacher teacher);
}
