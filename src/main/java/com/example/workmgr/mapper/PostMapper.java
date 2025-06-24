package com.example.workmgr.mapper;


import com.example.workmgr.model.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

// src/main/java/com/example/workmgr/mapper/PostMapper.java
@Mapper
public interface PostMapper {
    @Select("SELECT COUNT(*) FROM posts WHERE board_id=#{boardId}")
    int countByBoard(Long boardId);

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
