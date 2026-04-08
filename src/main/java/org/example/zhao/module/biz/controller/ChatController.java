package org.example.zhao.module.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.module.biz.entity.ChatMessage;
import org.example.zhao.module.biz.mapper.ChatMessageMapper;
import org.example.zhao.module.system.entity.SysUser;
import org.example.zhao.module.system.mapper.SysUserMapper;
import org.example.zhao.security.SecurityUtil;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageMapper chatMessageMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 发送消息
     */
    @PostMapping("/send")
    public R<Long> sendMessage(@RequestBody ChatMessage msg) {
        Long currentUserId = SecurityUtil.currentUserId();
        if (currentUserId == null) return R.fail("未登录");

        msg.setFromUserId(currentUserId);
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        chatMessageMapper.insert(msg);
        return R.ok(msg.getId());
    }

    /**
     * 获取聊天记录
     */
    @GetMapping("/history")
    public R<List<ChatMessage>> getHistory(@RequestParam Long withUserId) {
        Long currentUserId = SecurityUtil.currentUserId();
        if (currentUserId == null) return R.fail("未登录");

        LambdaQueryWrapper<ChatMessage> qw = new LambdaQueryWrapper<>();
        qw.and(wrapper -> wrapper
                .eq(ChatMessage::getFromUserId, currentUserId)
                .eq(ChatMessage::getToUserId, withUserId)
                .or(w -> w.eq(ChatMessage::getFromUserId, withUserId).eq(ChatMessage::getToUserId, currentUserId))
        );
        qw.orderByAsc(ChatMessage::getCreateTime);
        return R.ok(chatMessageMapper.selectList(qw));
    }

    /**
     * 获取可聊天的联系人列表（支持模糊搜索）
     */
    @GetMapping("/contacts")
    public R<List<SysUser>> getContacts(@RequestParam(required = false) String keyword) {
        Long currentUserId = SecurityUtil.currentUserId();
        if (currentUserId == null) return R.fail("未登录");

        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<SysUser>()
                .ne(SysUser::getId, currentUserId)
                .eq(SysUser::getStatus, 1)
                .eq(SysUser::getDeleted, 0);

        if (keyword != null && !keyword.trim().isEmpty()) {
            qw.and(w -> w.like(SysUser::getRealName, keyword.trim())
                    .or().like(SysUser::getUsername, keyword.trim()));
        }

        List<SysUser> allUsers = sysUserMapper.selectList(qw);
        return R.ok(allUsers);
    }
}
