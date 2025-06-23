package com.example.workmgr.mapper;

import com.example.workmgr.model.Attachment;
import com.example.workmgr.model.Board;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {
    @Select("SELECT id, project_id AS projectId, name, description FROM boards WHERE project_id = #{projectId} ORDER BY id")
    List<Board> findByProject(Long projectId);
    // 개별 게시판 조회
    @Select("SELECT id, project_id AS projectId, name, description, created_at AS createdAt, updated_at AS updatedAt FROM boards WHERE id = #{id}")
    Board findById(Long id);

    // 신규 게시판 생성
    @Insert("INSERT INTO boards(project_id, name, description) VALUES(#{projectId}, #{name}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Board board);

    // 게시판 수정
    @Update("UPDATE boards SET name = #{name}, description = #{description}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int update(Board board);

    // 게시판 삭제
    @Delete("DELETE FROM boards WHERE id = #{id}")
    int delete(Long id);
}