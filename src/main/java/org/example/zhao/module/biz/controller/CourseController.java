package org.example.zhao.module.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.module.biz.entity.Course;
import org.example.zhao.module.biz.mapper.CourseMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseMapper courseMapper;

    @GetMapping("/list")
    public R<Page<Course>> list(@RequestParam(defaultValue = "1") long page,
                                @RequestParam(defaultValue = "10") long size,
                                @RequestParam(required = false) String courseName,
                                @RequestParam(required = false) Long categoryId,
                                @RequestParam(required = false) String applyGrade) {
        LambdaQueryWrapper<Course> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(courseName)) qw.like(Course::getCourseName, courseName);
        if (categoryId != null) qw.eq(Course::getCategoryId, categoryId);
        if (StringUtils.hasText(applyGrade)) qw.like(Course::getApplyGrade, applyGrade);
        qw.orderByDesc(Course::getCreateTime);
        return R.ok(courseMapper.selectPage(new Page<>(page, size), qw));
    }

    @GetMapping("/listAll")
    public R<java.util.List<Course>> listAll() {
        return R.ok(courseMapper.selectList(new LambdaQueryWrapper<Course>().orderByAsc(Course::getCourseName)));
    }

    @PostMapping
    public R<Long> create(@RequestBody Course course) {
        courseMapper.insert(course);
        return R.ok(course.getId());
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Course course) {
        course.setId(id);
        courseMapper.updateById(course);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        courseMapper.deleteById(id);
        return R.ok();
    }
}

