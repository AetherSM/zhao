package org.example.zhao.module.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.common.exception.BizException;
import org.example.zhao.module.biz.dto.AttendanceCreateReq;
import org.example.zhao.module.biz.entity.CourseSchedule;
import org.example.zhao.module.biz.entity.StudentAttendance;
import org.example.zhao.module.biz.mapper.CourseScheduleMapper;
import org.example.zhao.module.biz.mapper.StudentAttendanceMapper;
import org.example.zhao.module.system.service.TeacherBindingService;
import org.example.zhao.security.SecurityUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final StudentAttendanceMapper studentAttendanceMapper;
    private final CourseScheduleMapper courseScheduleMapper;
    private final TeacherBindingService teacherBindingService;

    @GetMapping("/list")
    public R<Page<StudentAttendance>> list(@RequestParam(defaultValue = "1") long page,
                                           @RequestParam(defaultValue = "10") long size,
                                           @RequestParam(required = false) Long studentId,
                                           @RequestParam(required = false) Long scheduleId) {
        Page<StudentAttendance> p = new Page<>(page, size);
        if (SecurityUtil.hasRole("TEACHER")) {
            Long uid = SecurityUtil.currentUserId();
            Long bindTeacherId = teacherBindingService.getTeacherIdByUserId(uid);
            if (bindTeacherId == null) throw new BizException("教师账号未绑定教师信息");
            // 教师只看自己排课下的考勤（忽略 studentId/scheduleId 防止越权）
            return R.ok((Page<StudentAttendance>) studentAttendanceMapper.selectPageByTeacherId(p, bindTeacherId));
        }

        LambdaQueryWrapper<StudentAttendance> qw = new LambdaQueryWrapper<>();
        if (studentId != null) qw.eq(StudentAttendance::getStudentId, studentId);
        if (scheduleId != null) qw.eq(StudentAttendance::getScheduleId, scheduleId);
        qw.orderByDesc(StudentAttendance::getAttendanceTime);
        return R.ok(studentAttendanceMapper.selectPage(p, qw));
    }

    @PostMapping
    public R<Long> create(@Validated @RequestBody AttendanceCreateReq req) {
        if (SecurityUtil.hasRole("TEACHER")) {
            Long uid = SecurityUtil.currentUserId();
            Long bindTeacherId = teacherBindingService.getTeacherIdByUserId(uid);
            if (bindTeacherId == null) throw new BizException("教师账号未绑定教师信息");
            CourseSchedule s = courseScheduleMapper.selectById(req.getScheduleId());
            if (s == null) throw new BizException("排课不存在");
            if (!bindTeacherId.equals(s.getTeacherId())) throw new BizException("无权限：只能给自己的排课登记考勤");
        }
        StudentAttendance a = new StudentAttendance();
        a.setStudentId(req.getStudentId());
        a.setScheduleId(req.getScheduleId());
        a.setAttendanceStatus(req.getAttendanceStatus());
        a.setAttendanceTime(req.getAttendanceTime());
        a.setRemark(req.getRemark());
        a.setCreateTime(req.getAttendanceTime());
        studentAttendanceMapper.insert(a);
        return R.ok(a.getId());
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        studentAttendanceMapper.deleteById(id);
        return R.ok();
    }
}

