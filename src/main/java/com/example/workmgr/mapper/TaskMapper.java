package com.example.workmgr.mapper;

import com.example.workmgr.model.Task;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TaskMapper {
    @Select("""
        SELECT
          id,
          title,
          description,
          assignee,
          priority,
          due_date   AS dueDate,
          status,
          created_at AS createdAt,
          updated_at AS updatedAt
        FROM tasks
    """)
    List<Task> findAll();

    @Select("""
        SELECT
          id,
          title,
          description,
          assignee,
          priority,
          due_date   AS dueDate,
          status,
          created_at AS createdAt,
          updated_at AS updatedAt
        FROM tasks
        WHERE id = #{id}
    """)
    Task findById(Long id);

    @Insert("""
    INSERT INTO tasks
      (project_id, title, description, assignee, status, priority, start_date, due_date)
    VALUES
      (#{projectId}, #{title}, #{description}, #{assignee},
       #{status}, #{priority}, #{startDate}, #{dueDate})
  """)
    @Options(useGeneratedKeys=true, keyProperty="id")
    void insert(Task t);

    @Update("""
    UPDATE tasks SET
      project_id = #{projectId},
      title       = #{title},
      description = #{description},
      assignee    = #{assignee},
      status      = #{status},
      priority    = #{priority},
      start_date  = #{startDate},
      due_date    = #{dueDate}
    WHERE id=#{id}
  """)
    int update(Task t);

    @Delete("DELETE FROM tasks WHERE id=#{id}")
    int delete(Long id);
}