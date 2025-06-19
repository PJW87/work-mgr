package com.example.workmgr.controller.rest;

import com.example.workmgr.mapper.ProjectsMapper;
import com.example.workmgr.model.Project;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectsController {

    private final ProjectsMapper mapper;

    public ProjectsController(ProjectsMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public List<Project> list() {
        return mapper.findAll();
    }

    @PostMapping("/create")
    public ResponseEntity<Project> create(@RequestBody Project project) {
        // description, startDate 등 null 체크는 서비스 레이어에서 처리해도 됩니다
        mapper.insert(project);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getOne(@PathVariable Long id) {
        Project project = mapper.findById(id);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> update(
            @PathVariable Long id,
            @RequestBody Project project
    ) {
        project.setId(id);
        int updated = mapper.update(project);
        if (updated == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mapper.delete(id);
        return ResponseEntity.noContent().build();
    }
}
