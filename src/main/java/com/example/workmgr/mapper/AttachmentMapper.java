package com.example.workmgr.mapper;

import com.example.workmgr.model.Attachment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AttachmentMapper {
    @Select("SELECT * FROM attachments WHERE post_id = #{postId}")
    List<Attachment> findByPost(Long postId);

    @Insert("INSERT INTO attachments(post_id, filename, url) VALUES(#{postId},#{filename},#{url})")
    void insert(Attachment att);
}