package org.example.zhao.module.biz.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chat_message")
public class ChatMessage {
    @TableId
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}
