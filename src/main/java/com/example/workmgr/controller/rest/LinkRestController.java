package com.example.workmgr.controller.rest;

import com.example.workmgr.mapper.LinkMapper;
import com.example.workmgr.model.QuickLink;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
public class LinkRestController {
    private final LinkMapper linkMapper;

    @GetMapping
    public List<QuickLink> list() {
        return linkMapper.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuickLink> get(@PathVariable Long id) {
        QuickLink link = linkMapper.findById(id);
        return link == null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(link);
    }

    @PostMapping
    public QuickLink create(@RequestBody QuickLink link) {
        linkMapper.insert(link);
        return link;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody QuickLink link) {
        link.setId(id);
        if (linkMapper.update(link) == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        linkMapper.delete(id);
        return ResponseEntity.noContent().build();
    }
}
