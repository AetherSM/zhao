package org.example.zhao.module.biz.service;

import org.example.zhao.module.biz.dto.ScheduleCreateReq;
import org.example.zhao.module.biz.entity.CourseSchedule;

public interface ScheduleService {
    boolean hasConflict(ScheduleCreateReq req, Long excludeScheduleId);

    Long create(ScheduleCreateReq req);

    void delete(Long id);

    CourseSchedule getById(Long id);
}

