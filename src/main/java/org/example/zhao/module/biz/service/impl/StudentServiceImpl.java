package org.example.zhao.module.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.exception.BizException;
import org.example.zhao.module.biz.dto.StudentCreateReq;
import org.example.zhao.module.biz.entity.Student;
import org.example.zhao.module.biz.mapper.StudentMapper;
import org.example.zhao.module.biz.service.StudentService;
import org.example.zhao.module.system.entity.SysRole;
import org.example.zhao.module.system.entity.SysUser;
import org.example.zhao.module.system.entity.SysUserRole;
import org.example.zhao.module.system.entity.SysUserStudent;
import org.example.zhao.module.system.mapper.SysRoleMapper;
import org.example.zhao.module.system.mapper.SysUserMapper;
import org.example.zhao.module.system.mapper.SysUserRoleMapper;
import org.example.zhao.module.system.mapper.SysUserStudentMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysUserStudentMapper sysUserStudentMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStudent(StudentCreateReq req) {
        // 1. Validate Account Info
        String username = req.getUsername();
        if (!StringUtils.hasText(username)) {
            // Fallback to phone if username is missing
            if (StringUtils.hasText(req.getPhone())) {
                username = req.getPhone();
            } else {
                throw new BizException("创建学员必须提供用户名或手机号以生成系统账号");
            }
        }

        // 2. Check if user exists
        SysUser existingUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (existingUser != null) {
            throw new BizException("账号已存在: " + username);
        }

        // 3. Insert Student
        studentMapper.insert(req);
        Long studentId = req.getId();

        // 4. Create SysUser
        SysUser user = new SysUser();
        user.setUsername(username);
        String pwd = StringUtils.hasText(req.getPassword()) ? req.getPassword() : "123456";
        user.setPassword(encoder.encode(pwd));
        user.setRealName(req.getStudentName());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        sysUserMapper.insert(user);

        // 5. Link to STUDENT role
        SysRole role = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, "STUDENT"));
        if (role != null) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(role.getId());
            sysUserRoleMapper.insert(userRole);
        }

        // 6. Bind User and Student
        SysUserStudent us = new SysUserStudent();
        us.setUserId(user.getId());
        us.setStudentId(studentId);
        sysUserStudentMapper.insert(us);

        return studentId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStudent(Long id, Student student) {
        student.setId(id);
        studentMapper.updateById(student);
    }
}
