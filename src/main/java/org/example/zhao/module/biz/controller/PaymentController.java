package org.example.zhao.module.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.module.biz.dto.ArrearsItem;
import org.example.zhao.module.biz.dto.PaymentCreateReq;
import org.example.zhao.module.biz.entity.Payment;
import org.example.zhao.module.biz.mapper.PaymentMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentMapper paymentMapper;

    @GetMapping("/list")
    public R<Page<Payment>> list(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size,
                                 @RequestParam(required = false) Long studentId,
                                 @RequestParam(required = false) Long courseId,
                                 @RequestParam(required = false) LocalDateTime payTimeStart,
                                 @RequestParam(required = false) LocalDateTime payTimeEnd) {
        LambdaQueryWrapper<Payment> qw = new LambdaQueryWrapper<>();
        if (studentId != null) qw.eq(Payment::getStudentId, studentId);
        if (courseId != null) qw.eq(Payment::getCourseId, courseId);
        if (payTimeStart != null) qw.ge(Payment::getPayTime, payTimeStart);
        if (payTimeEnd != null) qw.le(Payment::getPayTime, payTimeEnd);
        qw.orderByDesc(Payment::getPayTime);
        return R.ok(paymentMapper.selectPage(new Page<>(page, size), qw));
    }

    @PostMapping
    public R<Long> create(@Validated @RequestBody PaymentCreateReq req) {
        Payment p = new Payment();
        p.setStudentId(req.getStudentId());
        p.setCourseId(req.getCourseId());
        p.setAmount(req.getAmount());
        p.setPayTime(req.getPayTime());
        p.setPayType(req.getPayType());
        p.setRemark(req.getRemark());
        paymentMapper.insert(p);
        return R.ok(p.getId());
    }

    @GetMapping("/arrears")
    public R<Page<ArrearsItem>> arrears(@RequestParam(defaultValue = "1") long page,
                                        @RequestParam(defaultValue = "10") long size,
                                        @RequestParam(required = false) String studentName) {
        Page<ArrearsItem> p = new Page<>(page, size);
        return R.ok((Page<ArrearsItem>) paymentMapper.selectArrearsPage(p, studentName));
    }
}

