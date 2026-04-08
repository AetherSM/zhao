package org.example.zhao.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.exception.BizException;
import org.example.zhao.module.system.dto.LoginResp;
import org.example.zhao.module.system.entity.SysUser;
import org.example.zhao.module.system.mapper.SysRoleMapper;
import org.example.zhao.module.system.mapper.SysUserMapper;
import org.example.zhao.module.system.service.AuthService;
import org.example.zhao.security.JwtTokenProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import org.example.zhao.module.biz.entity.Student;
import org.example.zhao.module.biz.entity.Teacher;
import org.example.zhao.module.biz.mapper.StudentMapper;
import org.example.zhao.module.biz.mapper.TeacherMapper;
import org.example.zhao.module.system.entity.SysRole;
import org.example.zhao.module.system.entity.SysUserRole;
import org.example.zhao.module.system.entity.SysUserStudent;
import org.example.zhao.module.system.entity.SysUserTeacher;
import org.example.zhao.module.system.mapper.SysUserRoleMapper;
import org.example.zhao.module.system.mapper.SysUserStudentMapper;
import org.example.zhao.module.system.mapper.SysUserTeacherMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;
    private final SysUserTeacherMapper sysUserTeacherMapper;
    private final SysUserStudentMapper sysUserStudentMapper;
    private final JwtTokenProvider jwtTokenProvider;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public LoginResp login(String username, String password) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .last("limit 1"));
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BizException("账号已被禁用");
        }
        String dbPwd = user.getPassword();
        boolean ok;
        if (dbPwd != null && dbPwd.startsWith("$2")) {
            ok = encoder.matches(password, dbPwd);
        } else {
            // 兼容毕业设计演示：允许明文（建议你最终改成 BCrypt）
            ok = password.equals(dbPwd);
        }
        if (!ok) throw new BizException("用户名或密码错误");

        List<String> roleCodes = sysRoleMapper.selectRoleCodesByUserId(user.getId());
        String token = jwtTokenProvider.createToken(user.getId(), user.getUsername(), roleCodes);
        return new LoginResp(token, "Bearer", user.getId(), user.getUsername(), user.getRealName(), roleCodes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(String username, String password, String roleCode) {
        // 1. Check if user already exists
        SysUser existingUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (existingUser != null) {
            throw new BizException("用户名已存在");
        }

        // 2. Find role
        SysRole role = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, roleCode));
        if (role == null) {
            throw new BizException("角色不存在: " + roleCode);
        }

        // 3. Create user
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setRealName(username); // Set username as realName by default
        user.setStatus(1); // Enable by default
        user.setCreateTime(LocalDateTime.now());
        sysUserMapper.insert(user);

        // 4. Link role
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        sysUserRoleMapper.insert(userRole);

        // 5. If role is Teacher or Student, create the business entity and binding
        if ("TEACHER".equals(roleCode)) {
            Teacher teacher = new Teacher();
            teacher.setTeacherName(username);
            teacher.setGender(1); // default
            teacher.setSubject("待定");
            teacherMapper.insert(teacher);

            SysUserTeacher ut = new SysUserTeacher();
            ut.setUserId(user.getId());
            ut.setTeacherId(teacher.getId());
            sysUserTeacherMapper.insert(ut);
        } else if ("STUDENT".equals(roleCode)) {
            Student student = new Student();
            student.setStudentName(username);
            student.setGender(1); // default
            student.setGrade("待定");
            student.setEnrollTime(LocalDate.now());
            studentMapper.insert(student);

            SysUserStudent us = new SysUserStudent();
            us.setUserId(user.getId());
            us.setStudentId(student.getId());
            sysUserStudentMapper.insert(us);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long userId, String username, String realName, String password) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) throw new BizException("用户不存在");

        if (username != null && !username.equals(user.getUsername())) {
            SysUser exist = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
            if (exist != null) throw new BizException("账号已存在");
            user.setUsername(username);
        }

        if (realName != null) {
            user.setRealName(realName);
        }

        if (password != null && !password.isEmpty()) {
            user.setPassword(encoder.encode(password));
        }

        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(user);
    }
}
