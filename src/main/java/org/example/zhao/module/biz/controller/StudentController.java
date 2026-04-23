package org.example.zhao.module.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.module.biz.entity.Student;
import org.example.zhao.module.biz.mapper.StudentMapper;
import org.example.zhao.module.biz.dto.StudentCreateReq;
import org.example.zhao.module.biz.service.StudentService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentMapper studentMapper;
    private final StudentService studentService;

    @GetMapping("/list")
    public R<Page<Student>> list(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size,
                                 @RequestParam(required = false) String studentName,
                                 @RequestParam(required = false) String grade,
                                 @RequestParam(required = false) String phone) {
        /*创建查询条件包装器*/
        LambdaQueryWrapper<Student> qw = new LambdaQueryWrapper<>();
        /*如果传入了学生姓名进行模糊查询*/
        if (StringUtils.hasText(studentName)) qw.like(Student::getStudentName, studentName);
        /*如果传入年级,添加精确匹配条件*/
        if (StringUtils.hasText(grade)) qw.eq(Student::getGrade, grade);
        /*如果传入手机号,添加模糊查询条件*/
        if (StringUtils.hasText(phone)) qw.like(Student::getPhone, phone);
        /*按时间降序排序*/
        qw.orderByDesc(Student::getCreateTime);
        /*返回分页结果*/
        return R.ok(studentMapper.selectPage(new Page<>(page, size), qw));
    }

    @PostMapping
    public R<Long> create(@RequestBody StudentCreateReq student) {
        return R.ok(studentService.createStudent(student));
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Student student) {
        studentService.updateStudent(id, student);
        return R.ok();
    }

    @GetMapping("/{id}")
    public R<Student> getById(@PathVariable Long id) {
        return R.ok(studentMapper.selectById(id));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        studentMapper.deleteById(id);
        return R.ok();
    }

    /**
     * 获取所有学员的 ID 和姓名，用于下拉选择
     */
    @GetMapping("/all-names")
    public R<List<Map<String, Object>>> getAllStudentNames() {
        List<Student> students = studentMapper.selectList(new LambdaQueryWrapper<Student>()
                .select(Student::getId, Student::getStudentName)
                .eq(Student::getDeleted, 0));
        
        List<Map<String, Object>> result = students.stream()
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", s.getId().toString()); // 转换为String避免前端精度问题
                    map.put("name", s.getStudentName());
                    return map;
                })
                .collect(Collectors.toList());
        return R.ok(result);
    }
}

