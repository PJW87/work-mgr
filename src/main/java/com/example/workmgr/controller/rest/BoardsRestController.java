package com.example.workmgr.controller.rest;

import com.example.workmgr.mapper.BoardMapper;
import com.example.workmgr.model.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/boards")
@RequiredArgsConstructor
public class BoardsRestController {

    private final BoardMapper boardMapper;

    // 프로젝트의 모든 게시판 조회
    @GetMapping
    public List<Board> list(@PathVariable Long projectId) {
        return boardMapper.findByProject(projectId);
    }

    // 특정 게시판 조회
    @GetMapping("/{id}")
    public ResponseEntity<Board> get(
            @PathVariable Long projectId,
            @PathVariable Long id
    ) {
        Board b = boardMapper.findById(id);
        if (b == null || !b.getProjectId().equals(projectId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(b);
    }

    // 신규 게시판 생성
    @PostMapping
    public ResponseEntity<Board> create(
            @PathVariable Long projectId,
            @RequestBody Board board
    ) {
        board.setProjectId(projectId);
        boardMapper.insert(board);
        return ResponseEntity.ok(board);
    }

    // 게시판 수정
    @PutMapping("/{id}")
    public ResponseEntity<Board> update(
            @PathVariable Long projectId,
            @PathVariable Long id,
            @RequestBody Board board
    ) {
        board.setId(id);
        board.setProjectId(projectId);
        int updated = boardMapper.update(board);
        if (updated == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(board);
    }

    // 게시판 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long projectId,
            @PathVariable Long id
    ) {
        Board b = boardMapper.findById(id);
        if (b == null || !b.getProjectId().equals(projectId)) {
            return ResponseEntity.notFound().build();
        }
        boardMapper.delete(id);
        return ResponseEntity.noContent().build();
    }
}
