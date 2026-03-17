package org.example.zhao.module.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.module.biz.dto.ScheduleCreateReq;
import org.example.zhao.module.biz.entity.CourseSchedule;
import org.example.zhao.module.biz.mapper.CourseScheduleMapper;
import org.example.zhao.module.biz.service.ScheduleService;
import org.example.zhao.common.exception.BizException;
import org.example.zhao.module.system.service.TeacherBindingService;
import org.example.zhao.security.SecurityUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final CourseScheduleMapper courseScheduleMapper;
    private final TeacherBindingService teacherBindingService;

    @GetMapping("/list")
    public R<Page<CourseSchedule>> list(@RequestParam(defaultValue = "1") long page,
                                        @RequestParam(defaultValue = "10") long size,
                                        @RequestParam(required = false) Long teacherId,
                                        @RequestParam(required = false) Long courseId,
                                        @RequestParam(required = false) LocalDate scheduleDate) {
        LambdaQueryWrapper<CourseSchedule> qw = new LambdaQueryWrapper<>();
        if (SecurityUtil.hasRole("TEACHER")) {
            Long uid = SecurityUtil.currentUserId();
            Long bindTeacherId = teacherBindingService.getTeacherIdByUserId(uid);
            if (bindTeacherId == null) throw new BizException("教师账号未绑定教师信息");
            qw.eq(CourseSchedule::getTeacherId, bindTeacherId);
        } else if (teacherId != null) {
            qw.eq(CourseSchedule::getTeacherId, teacherId);
        }
        if (courseId != null) qw.eq(CourseSchedule::getCourseId, courseId);
        if (scheduleDate != null) qw.eq(CourseSchedule::getScheduleDate, scheduleDate);
        qw.orderByDesc(CourseSchedule::getCreateTime);
        return R.ok(courseScheduleMapper.selectPage(new Page<>(page, size), qw));
    }

    @PostMapping
    public R<Long> create(@Validated @RequestBody ScheduleCreateReq req) {
        if (SecurityUtil.hasRole("TEACHER")) {
            Long uid = SecurityUtil.currentUserId();
            Long bindTeacherId = teacherBindingService.getTeacherIdByUserId(uid);
            if (bindTeacherId == null) throw new BizException("教师账号未绑定教师信息");
            // 教师只能给自己排课，强制覆盖
            req.setTeacherId(bindTeacherId);
        }
        return R.ok(scheduleService.create(req));
    }

    @GetMapping("/checkConflict")
    public R<Boolean> checkConflict(@RequestParam Long teacherId,
                                    @RequestParam String classroom,
                                    @RequestParam LocalDate scheduleDate,
                                    @RequestParam String classPeriod) {
        ScheduleCreateReq req = new ScheduleCreateReq();
        req.setTeacherId(teacherId);
        req.setClassroom(classroom);
        req.setScheduleDate(scheduleDate);
        req.setClassPeriod(classPeriod);
        req.setCourseId(0L);
        return R.ok(scheduleService.hasConflict(req, null));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return R.ok();
    }
}

