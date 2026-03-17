package org.example.zhao.module.system.service;

public interface TeacherBindingService {
    /**
     * @return teacherId; if not bound, return null
     */
    Long getTeacherIdByUserId(Long userId);
}

