package org.example.zhao.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.zhao.module.system.entity.SysUserTeacher;
import org.example.zhao.module.system.mapper.SysUserTeacherMapper;
import org.example.zhao.module.system.service.TeacherBindingService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherBindingServiceImpl implements TeacherBindingService {

    private final SysUserTeacherMapper sysUserTeacherMapper;

    @Override
    public Long getTeacherIdByUserId(Long userId) {
        if (userId == null) return null;
        SysUserTeacher bind = sysUserTeacherMapper.selectOne(new LambdaQueryWrapper<SysUserTeacher>()
                .eq(SysUserTeacher::getUserId, userId)
                .last("limit 1"));
        return bind == null ? null : bind.getTeacherId();
    }
}

