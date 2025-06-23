package com.example.workmgr.mapper;

import com.example.workmgr.model.Attachment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AttachmentMapper {
    @Insert("""
    INSERT INTO attachments(post_id, filename, storage_path, content_type, file_size)
    VALUES(#{postId}, #{filename}, #{storagePath}, #{contentType}, #{fileSize})
  """)
    @Options(useGeneratedKeys=true, keyProperty="id")
    void insert(Attachment a);

    @Select("SELECT * FROM attachments WHERE post_id=#{postId}")
    List<Attachment> findByPost(Long postId);
}