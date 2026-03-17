package org.example.zhao.module.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.module.biz.entity.Teacher;
import org.example.zhao.module.biz.mapper.TeacherMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherMapper teacherMapper;

    @GetMapping("/list")
    public R<Page<Teacher>> list(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size,
                                 @RequestParam(required = false) String teacherName,
                                 @RequestParam(required = false) String subject,
                                 @RequestParam(required = false) String phone) {
        LambdaQueryWrapper<Teacher> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(teacherName)) qw.like(Teacher::getTeacherName, teacherName);
        if (StringUtils.hasText(subject)) qw.like(Teacher::getSubject, subject);
        if (StringUtils.hasText(phone)) qw.like(Teacher::getPhone, phone);
        qw.orderByDesc(Teacher::getCreateTime);
        return R.ok(teacherMapper.selectPage(new Page<>(page, size), qw));
    }

    @GetMapping("/listAll")
    public R<java.util.List<Teacher>> listAll() {
        return R.ok(teacherMapper.selectList(new LambdaQueryWrapper<Teacher>().orderByAsc(Teacher::getTeacherName)));
    }

    @PostMapping
    public R<Long> create(@RequestBody Teacher teacher) {
        teacherMapper.insert(teacher);
        return R.ok(teacher.getId());
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Teacher teacher) {
        teacher.setId(id);
        teacherMapper.updateById(teacher);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        teacherMapper.deleteById(id);
        return R.ok();
    }
}

