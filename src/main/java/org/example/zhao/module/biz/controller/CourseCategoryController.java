package org.example.zhao.module.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.module.biz.entity.CourseCategory;
import org.example.zhao.module.biz.mapper.CourseCategoryMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courseCategory")
@RequiredArgsConstructor
public class CourseCategoryController {

    private final CourseCategoryMapper courseCategoryMapper;

    @GetMapping("/list")
    public R<Page<CourseCategory>> list(@RequestParam(defaultValue = "1") long page,
                                        @RequestParam(defaultValue = "10") long size,
                                        @RequestParam(required = false) String categoryName) {
        LambdaQueryWrapper<CourseCategory> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(categoryName)) qw.like(CourseCategory::getCategoryName, categoryName);
        qw.orderByAsc(CourseCategory::getSort).orderByDesc(CourseCategory::getCreateTime);
        return R.ok(courseCategoryMapper.selectPage(new Page<>(page, size), qw));
    }

    @PostMapping
    public R<Long> create(@RequestBody CourseCategory category) {
        courseCategoryMapper.insert(category);
        return R.ok(category.getId());
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody CourseCategory category) {
        category.setId(id);
        courseCategoryMapper.updateById(category);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        courseCategoryMapper.deleteById(id);
        return R.ok();
    }
}

