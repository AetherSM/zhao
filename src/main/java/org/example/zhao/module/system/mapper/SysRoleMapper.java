package org.example.zhao.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.zhao.module.system.entity.SysRole;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("""
            SELECT r.role_code
            FROM sys_user_role ur
            JOIN sys_role r ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
            """)
    List<String> selectRoleCodesByUserId(Long userId);
}

