package org.example.zhao.module.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.module.biz.entity.Course;
import org.example.zhao.module.biz.mapper.CourseMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public R<List<Course>> listAll() {
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

    /**
     * 获取所有课程的 ID 和名称，用于下拉选择
     */
    @GetMapping("/all-names")
    public R<List<Map<String, Object>>> getAllCourseNames() {
        List<Course> courses = courseMapper.selectList(new LambdaQueryWrapper<Course>()
                .select(Course::getId, Course::getCourseName)
                .eq(Course::getDeleted, 0));

        List<Map<String, Object>> result = courses.stream()
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", c.getId().toString()); // 转换为String避免前端精度问题
                    map.put("name", c.getCourseName());
                    return map;
                })
                .collect(Collectors.toList());
        return R.ok(result);
    }
}
