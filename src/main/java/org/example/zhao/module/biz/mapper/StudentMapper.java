package org.example.zhao.module.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.example.zhao.module.biz.dto.StatItem;
import org.example.zhao.module.biz.entity.Student;

import java.util.List;

public interface StudentMapper extends BaseMapper<Student> {

    @Select("""
            SELECT grade AS name, COUNT(1) AS value
            FROM student
            WHERE deleted = 0
            GROUP BY grade
            ORDER BY value DESC
            """)
    List<StatItem> statByGrade();

    @Select("""
            SELECT
              CASE gender WHEN 1 THEN '男' WHEN 2 THEN '女' ELSE '未知' END AS name,
              COUNT(1) AS value
            FROM student
            WHERE deleted = 0
            GROUP BY gender
            ORDER BY value DESC
            """)
    List<StatItem> statByGender();

    @Select("""
            SELECT DATE_FORMAT(enroll_time, '%Y-%m') AS name, COUNT(1) AS value
            FROM student
            WHERE deleted = 0
            GROUP BY DATE_FORMAT(enroll_time, '%Y-%m')
            ORDER BY name
            """)
    List<StatItem> statByEnrollMonth();
}

