package org.example.zhao.module.system.service;

public interface StudentBindingService {
    /**
     * @return studentId; if not bound, return null
     */
    Long getStudentIdByUserId(Long userId);
}
