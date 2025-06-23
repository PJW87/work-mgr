// src/main/java/com/example/workmgr/controller/View/BoardViewController.java
package com.example.workmgr.controller.view;

import com.example.workmgr.mapper.BoardMapper;
import com.example.workmgr.mapper.ProjectsMapper;
import com.example.workmgr.model.Board;
import com.example.workmgr.model.Project;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BoardViewController {

    private final ProjectsMapper projectMapper;
    private final BoardMapper boardMapper;

    public BoardViewController(ProjectsMapper projectMapper, BoardMapper boardMapper) {
        this.projectMapper = projectMapper;
        this.boardMapper = boardMapper;
    }

    @GetMapping("/projects/{projectId}/boards")
    public String boards(@PathVariable Long projectId, Model model) {
        // 1) 프로젝트 정보 조회
        Project project = projectMapper.findById(projectId);
        if (project == null) {
            // 존재하지 않는 프로젝트면 리스트로 리다이렉트
            return "redirect:/projects";
        }

        // 2) 해당 프로젝트의 게시판 목록 조회
        List<Board> boards = boardMapper.findByProject(projectId);

        // 3) 뷰에 프로젝트와 게시판 리스트를 담아서 전달
        model.addAttribute("project", project);
        model.addAttribute("boards", boards);

        return "boards";  // src/main/resources/templates/boards.html
    }
}
