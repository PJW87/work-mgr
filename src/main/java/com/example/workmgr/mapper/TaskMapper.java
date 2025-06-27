package com.example.workmgr.mapper;

import com.example.workmgr.model.Task;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TaskMapper {

       // 1) 전체 개수 조회
    @Select("SELECT COUNT(*) FROM tasks")
    int countAll();

//    @Select("""
//    SELECT
//         t.id
//       , t.title
//       , t.description
//       , t.assignee
//       , t.priority
//       , t.due_date   AS dueDate
//       , t.status
//       , t.project_id AS projectId
//       , p.name       AS projectName
//       , t.created_at AS createdAt
//       , t.updated_at AS updatedAt
//    FROM tasks t
//    LEFT JOIN projects p
//      ON p.id = t.project_id
//    ORDER BY
//       FIELD(t.priority, 'CRITICAL','HIGH','MEDIUM','LOW'),
//       CASE WHEN t.due_date IS NULL THEN 1 ELSE 0 END,
//       t.due_date
//    LIMIT #{offset}, #{size}
//""")
//    List<Task> findPage(@Param("offset") int offset,@Param("size") int size);
    @Select("""
    SELECT
         t.id
       , t.title
       , t.description
       , t.assignee
       , t.priority
       , t.due_date   AS dueDate
       , t.status
       , t.project_id AS projectId
       , p.name       AS projectName
       , t.created_at AS createdAt
       , t.updated_at AS updatedAt
    FROM tasks t
    LEFT JOIN projects p
      ON p.id = t.project_id
    ORDER BY
    CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END,
    FIELD(t.priority, 'CRITICAL','HIGH','MEDIUM','LOW'),
    CASE WHEN t.status <> 'DONE' 
         THEN (CASE WHEN t.due_date IS NULL THEN 1 ELSE 0 END) 
         ELSE NULL END,
    CASE WHEN t.status <> 'DONE' THEN t.due_date ELSE NULL END ASC,
    CASE WHEN t.status = 'DONE' THEN t.due_date ELSE NULL END DESC
  LIMIT #{offset}, #{size}
""")
    List<Task> findPage(@Param("offset") int offset,
                        @Param("size")   int size);
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
             t.id
           , t.title
           , t.description
           , t.assignee
           , t.priority
           , t.due_date   AS dueDate
           , t.status
           , t.project_id AS projectId
           , p.name       AS projectName
           , t.created_at AS createdAt
           , t.updated_at AS updatedAt
        FROM tasks t
        LEFT JOIN projects p
          ON p.id = t.project_id
        WHERE t.id = #{id}
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