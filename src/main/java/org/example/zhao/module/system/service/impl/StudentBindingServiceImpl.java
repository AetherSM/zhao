package org.example.zhao.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.zhao.module.system.entity.SysUserStudent;
import org.example.zhao.module.system.mapper.SysUserStudentMapper;
import org.example.zhao.module.system.service.StudentBindingService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentBindingServiceImpl implements StudentBindingService {

    private final SysUserStudentMapper sysUserStudentMapper;

    @Override
    public Long getStudentIdByUserId(Long userId) {
        SysUserStudent binding = sysUserStudentMapper.selectOne(new LambdaQueryWrapper<SysUserStudent>()
                .eq(SysUserStudent::getUserId, userId));
        return binding != null ? binding.getStudentId() : null;
    }
}
