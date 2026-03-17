package org.example.zhao.module.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.module.biz.entity.Student;
import org.example.zhao.module.biz.mapper.StudentMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentMapper studentMapper;

    @GetMapping("/list")
    public R<Page<Student>> list(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size,
                                 @RequestParam(required = false) String studentName,
                                 @RequestParam(required = false) String grade,
                                 @RequestParam(required = false) String phone) {
        LambdaQueryWrapper<Student> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(studentName)) qw.like(Student::getStudentName, studentName);
        if (StringUtils.hasText(grade)) qw.eq(Student::getGrade, grade);
        if (StringUtils.hasText(phone)) qw.like(Student::getPhone, phone);
        qw.orderByDesc(Student::getCreateTime);
        return R.ok(studentMapper.selectPage(new Page<>(page, size), qw));
    }

    @PostMapping
    public R<Long> create(@RequestBody Student student) {
        studentMapper.insert(student);
        return R.ok(student.getId());
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Student student) {
        student.setId(id);
        studentMapper.updateById(student);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        studentMapper.deleteById(id);
        return R.ok();
    }
}

