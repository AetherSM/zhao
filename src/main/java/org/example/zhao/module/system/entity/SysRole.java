package org.example.zhao.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.zhao.module.common.entity.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {
    @TableId
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
}

