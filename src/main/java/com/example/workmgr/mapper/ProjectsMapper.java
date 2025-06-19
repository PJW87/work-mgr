package com.example.workmgr.mapper;

import com.example.workmgr.model.Project;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProjectsMapper {

    @Select("SELECT id, name, description, start_date AS startDate, status, created_at, updated_at " +
            "FROM projects ORDER BY created_at DESC")
    List<Project> findAll();

    @Select("SELECT id, name, description, start_date AS startDate, status, created_at, updated_at " +
            "FROM projects WHERE id = #{id} ORDER BY created_at DESC")
    Project findById(@Param("id") Long id);

    @Insert("INSERT INTO projects (name, description, start_date) " +
            "VALUES (#{name}, #{description}, #{startDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Project project);

    @Update("UPDATE projects SET name=#{name}, description=#{description}, start_date=#{startDate} " +
            "WHERE id=#{id}")
    int update(Project project);

    @Delete("DELETE FROM projects WHERE id=#{id}")
    int delete(@Param("id") Long id);
}
