package org.example.zhao.module.biz.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("learning_resource")
public class LearningResource {
    @TableId
    private Long id;
    private String title;
    private String type; // HOMEWORK or MATERIAL
    private Long courseId;
    private Long teacherId;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
