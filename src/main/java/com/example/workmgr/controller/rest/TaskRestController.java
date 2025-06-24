package com.example.workmgr.controller.rest;
import com.example.workmgr.mapper.TaskMapper;
import com.example.workmgr.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskRestController {
    private final TaskMapper taskMapper;

    @GetMapping
    public List<Task> list() {
        return taskMapper.findAll();
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
