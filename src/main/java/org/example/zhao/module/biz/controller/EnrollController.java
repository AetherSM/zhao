package org.example.zhao.module.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.common.exception.BizException;
import org.example.zhao.module.biz.dto.EnrollReq;
import org.example.zhao.module.biz.entity.StudentCourse;
import org.example.zhao.module.biz.mapper.StudentCourseMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/enroll")
@RequiredArgsConstructor
public class EnrollController {

    private final StudentCourseMapper studentCourseMapper;

    @GetMapping("/list")
    public R<Page<StudentCourse>> list(@RequestParam(defaultValue = "1") long page,
                                       @RequestParam(defaultValue = "10") long size,
                                       @RequestParam(required = false) Long studentId,
                                       @RequestParam(required = false) Long courseId) {
        LambdaQueryWrapper<StudentCourse> qw = new LambdaQueryWrapper<>();
        if (studentId != null) qw.eq(StudentCourse::getStudentId, studentId);
        if (courseId != null) qw.eq(StudentCourse::getCourseId, courseId);
        qw.orderByDesc(StudentCourse::getSignTime);
        return R.ok(studentCourseMapper.selectPage(new Page<>(page, size), qw));
    }

    @PostMapping
    public R<Long> enroll(@Validated @RequestBody EnrollReq req) {
        Long cnt = studentCourseMapper.selectCount(new LambdaQueryWrapper<StudentCourse>()
                .eq(StudentCourse::getStudentId, req.getStudentId())
                .eq(StudentCourse::getCourseId, req.getCourseId()));
        if (cnt != null && cnt > 0) throw new BizException("该学员已报名该课程");
        StudentCourse sc = new StudentCourse();
        sc.setStudentId(req.getStudentId());
        sc.setCourseId(req.getCourseId());
        sc.setSignTime(LocalDateTime.now());
        sc.setCreateTime(LocalDateTime.now());
        studentCourseMapper.insert(sc);
        return R.ok(sc.getId());
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        studentCourseMapper.deleteById(id);
        return R.ok();
    }
}

