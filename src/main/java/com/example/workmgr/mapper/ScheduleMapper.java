package com.example.workmgr.mapper;


import com.example.workmgr.model.Schedule;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;



@Mapper
public interface ScheduleMapper {

    @Select("""
    SELECT id, title, content,
           start_date   AS startDate,
           end_date     AS endDate
      FROM schedules
   """)
    List<Schedule> findAll();

    @Select("""
    SELECT id, title, content,
           start_date   AS startDate,
           end_date     AS endDate
      FROM schedules
     WHERE id = #{id}
  """)
    Schedule findById(Long id);

    @Insert("""
    INSERT INTO schedules(title, content, start_date, end_date)
    VALUES(#{title}, #{content}, #{startDate}, #{endDate})
  """)
    @Options(useGeneratedKeys=true, keyProperty="id")
    void insert(Schedule s);

    @Update("""
    UPDATE schedules
       SET title      = #{title},
           content    = #{content},
           start_date = #{startDate},
           end_date   = #{endDate}
     WHERE id = #{id}
  """)
    void update(Schedule s);

    @Select("""
      SELECT id,
             title,
             content,
             start_date   AS startDate,
             end_date     AS endDate
        FROM schedules
       WHERE start_date BETWEEN #{from} AND #{to}
       ORDER BY start_date
    """)
    List<Schedule> findByDateRange(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to
    );
    @Delete("DELETE FROM schedules WHERE id = #{id}")
    void delete(Long id);

}