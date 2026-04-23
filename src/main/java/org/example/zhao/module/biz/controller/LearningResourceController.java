package org.example.zhao.module.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.common.exception.BizException;
import org.example.zhao.module.biz.entity.LearningResource;
import org.example.zhao.module.biz.mapper.LearningResourceMapper;
import org.example.zhao.module.system.service.TeacherBindingService;
import org.example.zhao.security.SecurityUtil;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/learningResource")
@RequiredArgsConstructor
public class LearningResourceController {

    private final LearningResourceMapper learningResourceMapper;
    private final TeacherBindingService teacherBindingService;

    @GetMapping("/list")
    public R<Page<LearningResource>> list(@RequestParam(defaultValue = "1") long page,
                                          @RequestParam(defaultValue = "10") long size,
                                          @RequestParam(required = false) Long courseId,
                                          @RequestParam(required = false) String type) {
        LambdaQueryWrapper<LearningResource> qw = new LambdaQueryWrapper<>();
        if (courseId != null) qw.eq(LearningResource::getCourseId, courseId);
        if (type != null) qw.eq(LearningResource::getType, type);
        qw.orderByDesc(LearningResource::getCreateTime);
        return R.ok(learningResourceMapper.selectPage(new Page<>(page, size), qw));
    }

    @PostMapping
    public R<Long> create(@RequestBody LearningResource req) {
        if (SecurityUtil.hasRole("TEACHER")) {
            Long uid = SecurityUtil.currentUserId();
            Long teacherId = teacherBindingService.getTeacherIdByUserId(uid);
            if (teacherId == null) throw new BizException("教师账号未绑定教师信息");
            req.setTeacherId(teacherId);
        } else if (SecurityUtil.hasRole("ADMIN")) {
            // Admin can publish without teacherId, or we could let them specify one.
            // For now, allow null as we updated the DB schema.
        } else {
            throw new BizException("无权发布资源");
        }
        req.setCreateTime(LocalDateTime.now());
        learningResourceMapper.insert(req);
        return R.ok(req.getId());
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        LearningResource res = learningResourceMapper.selectById(id);
        if (res == null) return R.ok();

        if (SecurityUtil.hasRole("TEACHER")) {
            Long uid = SecurityUtil.currentUserId();
            Long teacherId = teacherBindingService.getTeacherIdByUserId(uid);
            if (teacherId == null || !teacherId.equals(res.getTeacherId())) {
                throw new BizException("只能删除自己发布的资源");
            }
        } else if (!SecurityUtil.hasRole("ADMIN")) {
            throw new BizException("无权执行删除操作");
        }
        learningResourceMapper.deleteById(id);
        return R.ok();
    }
}
