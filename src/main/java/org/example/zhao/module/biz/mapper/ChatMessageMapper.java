package org.example.zhao.module.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.zhao.module.biz.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
