package org.example.zhao.module.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.exception.BizException;
import org.example.zhao.module.biz.dto.ScheduleCreateReq;
import org.example.zhao.module.biz.entity.CourseSchedule;
import org.example.zhao.module.biz.mapper.CourseScheduleMapper;
import org.example.zhao.module.biz.service.ScheduleService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final CourseScheduleMapper courseScheduleMapper;

    @Override
    public boolean hasConflict(ScheduleCreateReq req, Long excludeScheduleId) {
        LambdaQueryWrapper<CourseSchedule> base = new LambdaQueryWrapper<CourseSchedule>()
                .eq(CourseSchedule::getScheduleDate, req.getScheduleDate())
                .eq(CourseSchedule::getClassPeriod, req.getClassPeriod());
        if (excludeScheduleId != null) base.ne(CourseSchedule::getId, excludeScheduleId);

        Long teacherCnt = courseScheduleMapper.selectCount(base.clone().eq(CourseSchedule::getTeacherId, req.getTeacherId()));
        if (teacherCnt != null && teacherCnt > 0) return true;

        Long classroomCnt = courseScheduleMapper.selectCount(base.clone().eq(CourseSchedule::getClassroom, req.getClassroom()));
        return classroomCnt != null && classroomCnt > 0;
    }

    @Override
    public Long create(ScheduleCreateReq req) {
        if (hasConflict(req, null)) {
            throw new BizException("排课冲突：该教师或教室在该时间段已有排课");
        }
        CourseSchedule s = new CourseSchedule();
        s.setCourseId(req.getCourseId());
        s.setTeacherId(req.getTeacherId());
        s.setClassroom(req.getClassroom());
        s.setScheduleDate(req.getScheduleDate());
        s.setClassPeriod(req.getClassPeriod());
        courseScheduleMapper.insert(s);
        return s.getId();
    }

    @Override
    public void delete(Long id) {
        courseScheduleMapper.deleteById(id);
    }

    @Override
    public CourseSchedule getById(Long id) {
        return courseScheduleMapper.selectById(id);
    }
}

