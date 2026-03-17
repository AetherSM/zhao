package org.example.zhao.module.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.example.zhao.module.biz.dto.StatItem;
import org.example.zhao.module.biz.entity.Course;

import java.util.List;

public interface CourseMapper extends BaseMapper<Course> {

    @Select("""
            SELECT cc.category_name AS name, COUNT(1) AS value
            FROM student_course sc
            JOIN course c ON sc.course_id = c.id AND c.deleted = 0
            JOIN course_category cc ON c.category_id = cc.id AND cc.deleted = 0
            GROUP BY cc.id
            ORDER BY value DESC
            """)
    List<StatItem> statEnrollCountByCategory();
}

