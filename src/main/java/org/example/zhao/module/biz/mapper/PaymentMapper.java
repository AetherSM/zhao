package org.example.zhao.module.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.zhao.module.biz.dto.ArrearsItem;
import org.example.zhao.module.biz.dto.RevenueItem;
import org.example.zhao.module.biz.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentMapper extends BaseMapper<Payment> {

    @Select("""
            SELECT
              s.id AS studentId,
              s.student_name AS studentName,
              c.id AS courseId,
              c.course_name AS courseName,
              (c.price * c.total_hours) AS totalFee,
              IFNULL(SUM(p.amount), 0) AS paidFee,
              ((c.price * c.total_hours) - IFNULL(SUM(p.amount), 0)) AS arrearsFee
            FROM student_course sc
            JOIN student s ON sc.student_id = s.id AND s.deleted = 0
            JOIN course c ON sc.course_id = c.id AND c.deleted = 0
            LEFT JOIN payment p ON p.student_id = sc.student_id AND p.course_id = sc.course_id
            WHERE (#{studentName} IS NULL OR s.student_name LIKE CONCAT('%', #{studentName}, '%'))
            GROUP BY s.id, c.id
            HAVING arrearsFee > 0
            ORDER BY arrearsFee DESC
            """)
    IPage<ArrearsItem> selectArrearsPage(IPage<ArrearsItem> page, @Param("studentName") String studentName);

    @Select("""
            SELECT DATE_FORMAT(pay_time, '%Y-%m-%d') AS name, IFNULL(SUM(amount), 0) AS value
            FROM payment
            WHERE pay_time >= #{start} AND pay_time <= #{end}
            GROUP BY DATE_FORMAT(pay_time, '%Y-%m-%d')
            ORDER BY name
            """)
    List<RevenueItem> statRevenueByDay(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("""
            SELECT DATE_FORMAT(pay_time, '%Y-%m') AS name, IFNULL(SUM(amount), 0) AS value
            FROM payment
            WHERE pay_time >= #{start} AND pay_time <= #{end}
            GROUP BY DATE_FORMAT(pay_time, '%Y-%m')
            ORDER BY name
            """)
    List<RevenueItem> statRevenueByMonth(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("""
            SELECT DATE_FORMAT(pay_time, '%Y') AS name, IFNULL(SUM(amount), 0) AS value
            FROM payment
            WHERE pay_time >= #{start} AND pay_time <= #{end}
            GROUP BY DATE_FORMAT(pay_time, '%Y')
            ORDER BY name
            """)
    List<RevenueItem> statRevenueByYear(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

