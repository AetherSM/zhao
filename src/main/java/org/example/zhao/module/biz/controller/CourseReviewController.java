package org.example.zhao.module.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.common.exception.BizException;
import org.example.zhao.module.biz.entity.CourseReview;
import org.example.zhao.module.biz.mapper.CourseReviewMapper;
import org.example.zhao.module.system.service.StudentBindingService;
import org.example.zhao.security.SecurityUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/courseReview")
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewMapper courseReviewMapper;
    private final StudentBindingService studentBindingService;

    @GetMapping("/list")
    public R<Page<CourseReview>> list(@RequestParam(defaultValue = "1") long page,
                                      @RequestParam(defaultValue = "10") long size,
                                      @RequestParam(required = false) Long courseId,
                                      @RequestParam(required = false) Long teacherId) {
        LambdaQueryWrapper<CourseReview> qw = new LambdaQueryWrapper<>();
        if (courseId != null) qw.eq(CourseReview::getCourseId, courseId);
        if (teacherId != null) qw.eq(CourseReview::getTeacherId, teacherId);
        qw.orderByDesc(CourseReview::getCreateTime);
        return R.ok(courseReviewMapper.selectPage(new Page<>(page, size), qw));
    }

    @PostMapping
    public R<Long> create(@RequestBody CourseReview req) {
        if (SecurityUtil.hasRole("STUDENT")) {
            Long uid = SecurityUtil.currentUserId();
            Long studentId = studentBindingService.getStudentIdByUserId(uid);
            if (studentId == null) throw new BizException("学员账号未绑定学员信息");
            req.setStudentId(studentId);
        } else {
            throw new BizException("仅学员可发布评价");
        }
        req.setCreateTime(LocalDateTime.now());
        courseReviewMapper.insert(req);
        return R.ok(req.getId());
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new BizException("仅管理员可删除课程评价");
        }
        courseReviewMapper.deleteById(id);
        return R.ok();
    }
}
