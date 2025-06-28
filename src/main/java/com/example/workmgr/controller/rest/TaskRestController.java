package com.example.workmgr.controller.rest;
import com.example.workmgr.mapper.ProjectsMapper;
import com.example.workmgr.mapper.TaskMapper;
import com.example.workmgr.model.PageResultDto;
import com.example.workmgr.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskRestController {
    private final TaskMapper taskMapper;
    private final ProjectsMapper projectsMapper;

@GetMapping
public PageResultDto.PageResult<Task> list(
        @RequestParam(defaultValue="T")     String type,
        @RequestParam(defaultValue="")      String keyword,
        @RequestParam(required=false)       Long projectId,
        @RequestParam(required=false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
        LocalDate from,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
        LocalDate to,
        @RequestParam(defaultValue="0")     int page,
        @RequestParam(defaultValue="20")    int size
) {
    int total  = taskMapper.countAll(type, keyword, projectId, from, to);
    int offset = page * size;
    List<Task> data = taskMapper.findPage(
            type, keyword, projectId, from, to,
            offset, size
    );
    return new PageResultDto.PageResult<>(data, total, page, size);
}
    @GetMapping("/{id}")
    public ResponseEntity<Task> get(@PathVariable Long id) {
        Task t = taskMapper.findById(id);
        return t==null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(t);
    }

    @PostMapping
    public Task create(@RequestBody Task t) {
        taskMapper.insert(t);
        return t;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestBody Task t
    ) {
        t.setId(id);
        if (taskMapper.update(t)==0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskMapper.delete(id);
        return ResponseEntity.noContent().build();
    }
}
