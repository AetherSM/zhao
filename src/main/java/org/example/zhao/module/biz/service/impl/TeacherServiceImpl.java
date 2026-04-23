package org.example.zhao.module.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.exception.BizException;
import org.example.zhao.module.biz.dto.TeacherCreateReq;
import org.example.zhao.module.biz.entity.Teacher;
import org.example.zhao.module.biz.mapper.TeacherMapper;
import org.example.zhao.module.biz.service.TeacherService;
import org.example.zhao.module.system.entity.SysRole;
import org.example.zhao.module.system.entity.SysUser;
import org.example.zhao.module.system.entity.SysUserRole;
import org.example.zhao.module.system.entity.SysUserTeacher;
import org.example.zhao.module.system.mapper.SysRoleMapper;
import org.example.zhao.module.system.mapper.SysUserMapper;
import org.example.zhao.module.system.mapper.SysUserRoleMapper;
import org.example.zhao.module.system.mapper.SysUserTeacherMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherMapper teacherMapper;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysUserTeacherMapper sysUserTeacherMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTeacher(TeacherCreateReq req) {
        // 1. Validate Account Info
        String username = req.getUsername();
        if (!StringUtils.hasText(username)) {
            // Fallback to phone if username is missing
            if (StringUtils.hasText(req.getPhone())) {
                username = req.getPhone();
            } else {
                throw new BizException("创建教师必须提供用户名或手机号以生成系统账号");
            }
        }

        // 2. Check if user exists
        SysUser existingUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (existingUser != null) {
            throw new BizException("账号已存在: " + username);
        }

        // 3. Insert Teacher
        teacherMapper.insert(req);
        Long teacherId = req.getId();

        // 4. Create SysUser
        SysUser user = new SysUser();
        user.setUsername(username);
        String pwd = StringUtils.hasText(req.getPassword()) ? req.getPassword() : "123456";
        user.setPassword(encoder.encode(pwd));
        user.setRealName(req.getTeacherName());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        sysUserMapper.insert(user);

        // 5. Link to TEACHER role
        SysRole role = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, "TEACHER"));
        if (role != null) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(role.getId());
            sysUserRoleMapper.insert(userRole);
        }

        // 6. Bind User and Teacher
        SysUserTeacher ut = new SysUserTeacher();
        ut.setUserId(user.getId());
        ut.setTeacherId(teacherId);
        sysUserTeacherMapper.insert(ut);

        return teacherId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTeacher(Long id, Teacher teacher) {
        teacher.setId(id);
        teacherMapper.updateById(teacher);
    }
}
