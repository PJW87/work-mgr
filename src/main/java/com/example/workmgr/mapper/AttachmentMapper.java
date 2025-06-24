package com.example.workmgr.mapper;

import com.example.workmgr.model.Attachment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AttachmentMapper {
    @Select("""
      SELECT
        id,
        post_id      AS postId,
        filename,
        storage_path AS storagePath,
        content_type AS contentType,
        file_size    AS fileSize,
        uploaded_at  AS uploadedAt
      FROM attachments
      WHERE id = #{id}
    """)
    Attachment findById(Long id);

    @Select("""
      SELECT
        id,
        post_id      AS postId,
        filename,
        storage_path AS storagePath,
        content_type AS contentType,
        file_size    AS fileSize,
        uploaded_at  AS uploadedAt
      FROM attachments
      WHERE post_id = #{postId}
    """)
    List<Attachment> findByPost(Long postId);

    @Insert("""
      INSERT INTO attachments(post_id, filename, storage_path, content_type, file_size)
      VALUES(#{postId}, #{filename}, #{storagePath}, #{contentType}, #{fileSize})
    """)
    @Options(useGeneratedKeys=true, keyProperty="id")
    void insert(Attachment a);
    /**
     * 단일 첨부파일 삭제
     * @param id attachments.id
     */
    @Delete("DELETE FROM attachments WHERE id = #{id}")
    void delete(Long id);
}