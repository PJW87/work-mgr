package com.example.workmgr.mapper;


import com.example.workmgr.model.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMapper {
    @Select("SELECT * FROM posts WHERE board_id = #{boardId} ORDER BY created_at DESC")
    List<Post> findByBoard(Long boardId);

    @Select("SELECT * FROM posts WHERE id = #{id}")
    Post findById(Long id);

    @Insert("INSERT INTO posts(board_id, author, title, content) " +
            "VALUES(#{boardId},#{author},#{title},#{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Post post);

    @Update("UPDATE posts SET title=#{title}, content=#{content}, updated_at=CURRENT_TIMESTAMP() WHERE id=#{id}")
    int update(Post post);

    @Delete("DELETE FROM posts WHERE id=#{id}")
    void delete(Long id);
}