package com.example.workmgr.mapper;


import com.example.workmgr.model.Post;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

// src/main/java/com/example/workmgr/mapper/PostMapper.java
@Mapper
public interface PostMapper {
    @Select("SELECT COUNT(*) FROM posts WHERE board_id=#{boardId}")
    int countByBoard(Long boardId);

    /** 검색 포함 레코드 수 */
    @Select("""
      SELECT COUNT(*)
        FROM posts
       WHERE board_id = #{boardId}
         AND (#{type} = '' 
              OR (#{type} = 'T'  AND title   LIKE CONCAT('%',#{keyword},'%'))
              OR (#{type} = 'C'  AND content LIKE CONCAT('%',#{keyword},'%'))
              OR (#{type} = 'TC' AND (title LIKE CONCAT('%',#{keyword},'%')
                                 OR content LIKE CONCAT('%',#{keyword},'%'))))
         AND (#{from} IS NULL OR created_at >= #{from})
         AND (#{to}   IS NULL OR created_at <  DATE_ADD(#{to}, INTERVAL 1 DAY))
    """)
    int countByBoardAndSearch(
            @Param("boardId") Long boardId,
            @Param("type")    String type,
            @Param("keyword") String keyword,
            @Param("from")    LocalDate from,
            @Param("to")      LocalDate to
    );
    /** 검색 + 페이징된 리스트 */
    @Select("""
      SELECT
           id,
           board_id      AS boardId,
           title,
           content,
           author,
           status,
           created_at    AS createdAt,
           updated_at    AS updatedAt
      FROM posts
      WHERE board_id = #{boardId}
        AND (#{type} = '' 
             OR (#{type} = 'T'  AND title   LIKE CONCAT('%',#{keyword},'%'))
             OR (#{type} = 'C'  AND content LIKE CONCAT('%',#{keyword},'%'))
             OR (#{type} = 'TC' AND (title LIKE CONCAT('%',#{keyword},'%')
                                OR content LIKE CONCAT('%',#{keyword},'%'))))
        AND (#{from} IS NULL OR created_at >= #{from})
        AND (#{to}   IS NULL OR created_at <  DATE_ADD(#{to}, INTERVAL 1 DAY))
      ORDER BY created_at DESC
      LIMIT #{limit} OFFSET #{offset}
    """)
    List<Post> findByBoardAndSearch(
            @Param("boardId") Long boardId,
            @Param("type")    String type,
            @Param("keyword") String keyword,
            @Param("from")    LocalDate from,
            @Param("to")      LocalDate to,
            @Param("offset")  int offset,
            @Param("limit")   int limit
    );

    @Select("""
    SELECT id, board_id AS boardId, title, content, author,
           status, created_at AS createdAt, updated_at AS updatedAt
      FROM posts
     WHERE board_id=#{boardId}
     ORDER BY created_at DESC
     LIMIT #{limit} OFFSET #{offset}
  """)
    List<Post> findByBoard(@Param("boardId") Long boardId,
                           @Param("offset")  int offset,
                           @Param("limit")   int limit);

    @Select("""
      SELECT
        id,
        board_id      AS boardId,
        title,
        content,
        author,
        status,
        created_at    AS createdAt,
        updated_at    AS updatedAt
      FROM posts
      WHERE id = #{id}
    """)
    Post findById(Long id);

    @Insert("""
    INSERT INTO posts(board_id,title,content,author,status)
    VALUES(#{boardId},#{title},#{content},#{author},#{status})
  """)
    @Options(useGeneratedKeys=true, keyProperty="id")
    void insert(Post p);

    @Update("""
    UPDATE posts
       SET title=#{title}, content=#{content}, author=#{author}, status=#{status}
     WHERE id=#{id}
  """)
    int update(Post p);

    @Delete("DELETE FROM posts WHERE id=#{id}")
    void delete(Long id);
}
