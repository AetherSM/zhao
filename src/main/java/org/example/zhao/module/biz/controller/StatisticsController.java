package org.example.zhao.module.biz.controller;

import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.common.exception.BizException;
import org.example.zhao.module.biz.dto.RevenueItem;
import org.example.zhao.module.biz.dto.StatItem;
import org.example.zhao.module.biz.mapper.CourseMapper;
import org.example.zhao.module.biz.mapper.PaymentMapper;
import org.example.zhao.module.biz.mapper.StudentMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StudentMapper studentMapper;
    private final CourseMapper courseMapper;
    private final PaymentMapper paymentMapper;

    @GetMapping("/student")
    public R<List<StatItem>> student(@RequestParam String type) {
        if ("grade".equalsIgnoreCase(type)) {
            return R.ok(studentMapper.statByGrade());
        }
        if ("gender".equalsIgnoreCase(type)) {
            return R.ok(studentMapper.statByGender());
        }
        if ("enrollTime".equalsIgnoreCase(type)) {
            return R.ok(studentMapper.statByEnrollMonth());
        }
        throw new BizException("未知统计类型");
    }

    @GetMapping("/course")
    public R<List<StatItem>> course(@RequestParam(defaultValue = "category") String type) {
        if ("category".equalsIgnoreCase(type)) {
            return R.ok(courseMapper.statEnrollCountByCategory());
        }
        throw new BizException("未知统计类型");
    }

    @GetMapping("/revenue")
    public R<List<RevenueItem>> revenue(@RequestParam String startTime,
                                        @RequestParam String endTime,
                                        @RequestParam(defaultValue = "month") String type) {
        LocalDate startLocalDate = LocalDate.parse(startTime);
        LocalDate endLocalDate = LocalDate.parse(endTime);
        LocalDateTime start = startLocalDate.atStartOfDay();
        LocalDateTime end = endLocalDate.plusDays(1).atStartOfDay().minusSeconds(1);

        if ("day".equalsIgnoreCase(type)) {
            return R.ok(paymentMapper.statRevenueByDay(start, end));
        }
        if ("month".equalsIgnoreCase(type)) {
            return R.ok(paymentMapper.statRevenueByMonth(start, end));
        }
        if ("year".equalsIgnoreCase(type)) {
            return R.ok(paymentMapper.statRevenueByYear(start, end));
        }
        throw new BizException("未知统计类型");
    }
}

