package org.example.zhao.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user_teacher")
public class SysUserTeacher {
    @TableId
    private Long id;
    private Long userId;
    private Long teacherId;
}

