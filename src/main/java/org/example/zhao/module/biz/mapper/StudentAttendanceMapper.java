package org.example.zhao.module.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.zhao.module.biz.entity.StudentAttendance;

public interface StudentAttendanceMapper extends BaseMapper<StudentAttendance> {

    @Select("""
            SELECT a.*
            FROM student_attendance a
            JOIN course_schedule s ON a.schedule_id = s.id AND s.deleted = 0
            WHERE s.teacher_id = #{teacherId}
            ORDER BY a.attendance_time DESC
            """)
    IPage<StudentAttendance> selectPageByTeacherId(IPage<StudentAttendance> page, @Param("teacherId") Long teacherId);
}

