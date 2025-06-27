package com.example.workmgr.mapper;

import com.example.workmgr.model.Issue;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface IssueMapper {

    /** 검색 없이 전체 카운트(필터 포함) */
    @Select("""
      SELECT COUNT(*)
        FROM issues
       WHERE (#{type} = '' OR 
             (#{type} = 'T'  AND title   LIKE CONCAT('%',#{keyword},'%')) OR
             (#{type} = 'C'  AND content LIKE CONCAT('%',#{keyword},'%')) OR
             (#{type} = 'TC' AND (title LIKE CONCAT('%',#{keyword},'%')
                               OR content LIKE CONCAT('%',#{keyword},'%'))))
         AND (#{from} IS NULL OR created_at >= #{from})
         AND (#{to}   IS NULL OR created_at <= #{to})
    """)
    int countAll(@Param("type")    String type,
                 @Param("keyword") String keyword,
                 @Param("from")    LocalDate from,
                 @Param("to")      LocalDate to);

    /** 페이징 + 검색된 페이지 */
    @Select("""
      SELECT
           id,
           title,
           content,
           author,
           status,
           created_at AS createdAt,
           updated_at AS updatedAt
      FROM issues
     WHERE (#{type} = '' OR 
           (#{type} = 'T'  AND title   LIKE CONCAT('%',#{keyword},'%')) OR
           (#{type} = 'C'  AND content LIKE CONCAT('%',#{keyword},'%')) OR
           (#{type} = 'TC' AND (title LIKE CONCAT('%',#{keyword},'%')
                             OR content LIKE CONCAT('%',#{keyword},'%'))))
       AND (#{from} IS NULL OR created_at >= #{from})
       AND (#{to}   IS NULL OR created_at <= #{to})
     ORDER BY created_at DESC
     LIMIT #{limit} OFFSET #{offset}
    """)
    List<Issue> findPage(
            @Param("type")    String type,
            @Param("keyword") String keyword,
            @Param("from")    LocalDate from,
            @Param("to")      LocalDate to,
            @Param("offset")  int offset,
            @Param("limit")   int limit
    );

    /** 단건 */
    @Select("SELECT id, title, content, author, status, created_at AS createdAt, updated_at AS updatedAt FROM issues WHERE id=#{id}")
    Issue findById(Long id);

    /** 삽입 */
    @Insert("""
      INSERT INTO issues(title, content, author, status)
      VALUES(#{title},#{content},#{author},#{status})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Issue i);

    /** 수정 */
    @Update("""
      UPDATE issues
         SET title   = #{title},
             content = #{content},
             author  = #{author},
             status  = #{status}
       WHERE id = #{id}
    """)
    void update(Issue i);

    /** 삭제 */
    @Delete("DELETE FROM issues WHERE id=#{id}")
    void delete(Long id);
}
