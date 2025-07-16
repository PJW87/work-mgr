package com.example.workmgr.mapper;

import com.example.workmgr.model.QuickLink;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LinkMapper {

    @Select("""
      SELECT
        id, title, url, description,
        click_count AS clickCount,
        created_at AS createdAt,
        updated_at AS updatedAt
      FROM quick_link
      ORDER BY click_count DESC, created_at DESC
      """)
    List<QuickLink> findAllOrderByCount();

    @Select("""
        SELECT 
          id, 
          title, 
          url, 
          description, 
          created_at AS createdAt, 
          updated_at AS updatedAt 
        FROM quick_link 
        WHERE id = #{id}
        """)
    QuickLink findById(@Param("id") Long id);

    @Insert("""
        INSERT INTO quick_link (title, url, description)
        VALUES (#{title}, #{url}, #{description})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(QuickLink link);

    @Update("""
        UPDATE quick_link
           SET title       = #{title},
               url         = #{url},
               description = #{description},
               updated_at  = NOW()
         WHERE id = #{id}
        """)
    int update(QuickLink link);

    @Delete("DELETE FROM quick_link WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Update("UPDATE quick_link SET click_count = click_count + 1 WHERE id = #{id}")
    int incrementCount(@Param("id") Long id);

}
