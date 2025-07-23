package com.example.workmgr.mapper;

import com.example.workmgr.model.Task;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TaskMapper {

//       // 1) 전체 개수 조회
//    @Select("SELECT COUNT(*) FROM tasks")
//    int countAll();

    /** 검색 포함 전체 카운트 */
    @Select("""
      SELECT COUNT(*)
        FROM tasks t
        LEFT JOIN projects p ON p.id = t.project_id
       WHERE (#{type} = '' 
              OR (#{type} = 'T'  AND t.title       LIKE CONCAT('%',#{keyword},'%'))
              OR (#{type} = 'D'  AND t.description LIKE CONCAT('%',#{keyword},'%'))
              OR (#{type} = 'TD' AND (t.title LIKE CONCAT('%',#{keyword},'%')
                                  OR t.description LIKE CONCAT('%',#{keyword},'%'))))
         AND (#{projectId} IS NULL OR t.project_id = #{projectId})
         AND (#{from}      IS NULL OR t.due_date    >= #{from})
         AND (#{to}        IS NULL OR t.due_date <  DATE_ADD(#{to}, INTERVAL 1 DAY))
    """)
    int countAll(
            @Param("type")       String type,
            @Param("keyword")    String keyword,
            @Param("projectId")  Long projectId,
            @Param("from")       LocalDate from,
            @Param("to")         LocalDate to
    );

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
//    CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END,
//    FIELD(t.priority, 'CRITICAL','HIGH','MEDIUM','LOW'),
//    CASE WHEN t.status <> 'DONE'
//         THEN (CASE WHEN t.due_date IS NULL THEN 1 ELSE 0 END)
//         ELSE NULL END,
//    CASE WHEN t.status <> 'DONE' THEN t.due_date ELSE NULL END ASC,
//    CASE WHEN t.status = 'DONE' THEN t.due_date ELSE NULL END DESC
//  LIMIT #{offset}, #{size}
//""")
//    List<Task> findPage(@Param("offset") int offset,
//                        @Param("size")   int size);
    /** 페이징 + 검색된 페이지 */
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
      LEFT JOIN projects p ON p.id = t.project_id
     WHERE (#{type} = '' 
            OR (#{type} = 'T'  AND t.title       LIKE CONCAT('%',#{keyword},'%'))
            OR (#{type} = 'D'  AND t.description LIKE CONCAT('%',#{keyword},'%'))
            OR (#{type} = 'TD' AND (t.title LIKE CONCAT('%',#{keyword},'%')
                                  OR t.description LIKE CONCAT('%',#{keyword},'%'))))
       AND (#{projectId} IS NULL OR t.project_id = #{projectId})
       AND (#{from}      IS NULL OR t.due_date    >= #{from})
       AND (#{to}        IS NULL OR t.due_date <  DATE_ADD(#{to}, INTERVAL 1 DAY))
     ORDER BY
       CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END,
       CASE WHEN t.status = 'DONE' THEN t.due_date END DESC,
       FIELD(t.priority,'CRITICAL','HIGH','MEDIUM','LOW'),
       CASE WHEN t.due_date IS NULL THEN 1 ELSE 0 END,
       t.due_date ASC
     LIMIT #{offset}, #{size}
    """)
    List<Task> findPage(
            @Param("type")       String type,
            @Param("keyword")    String keyword,
            @Param("projectId")  Long projectId,
            @Param("from")       LocalDate from,
            @Param("to")         LocalDate to,
            @Param("offset")     int offset,
            @Param("size")       int size
    );
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