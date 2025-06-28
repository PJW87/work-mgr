package com.example.workmgr.controller.view;

import com.example.workmgr.controller.rest.PostRestController;
import com.example.workmgr.mapper.AttachmentMapper;
import com.example.workmgr.mapper.BoardMapper;
import com.example.workmgr.mapper.PostMapper;
import com.example.workmgr.mapper.ProjectsMapper;
import com.example.workmgr.model.Board;
import com.example.workmgr.model.Post;
import com.example.workmgr.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

// src/main/java/com/example/workmgr/controller/view/PostViewController.java
@Controller
@RequiredArgsConstructor
@RequestMapping("/projects/{pid}/boards/{bid}/posts")
public class PostViewController {
    private final BoardMapper boardMapper;
    private final PostMapper postMapper;
    private final ProjectsMapper projectMapper;
    private final AttachmentMapper attMapper;
    private final PostRestController postRestController;
    @GetMapping
    public String postsPage(
            @PathVariable Long pid,
            @PathVariable Long bid,
            @RequestParam(defaultValue = "")   String type,
            @RequestParam(defaultValue = "")   String keyword,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue="0")    int page,
            @RequestParam(defaultValue="10")   int size,
            Model model
    ) {
        Board   board   = boardMapper.findById(bid);
        Project project = projectMapper.findById(pid);
        int     total   = postMapper.countByBoardAndSearch(bid, type, keyword, from, to);

        model.addAttribute("projectId", pid);
        model.addAttribute("project",   project);
        model.addAttribute("board",     board);
        model.addAttribute("boardId",   bid);

        // 검색 폼 초기값
        model.addAttribute("type",     type);
        model.addAttribute("keyword",  keyword);
        model.addAttribute("from",     from  != null ? from.toString()  : "");
        model.addAttribute("to",       to    != null ? to.toString()    : "");

        // 페이징 정보
        model.addAttribute("page",     page);
        model.addAttribute("size",     size);
        model.addAttribute("total",    total);

        return "posts";
    }
    // 새 글쓰기 폼
    @GetMapping("/new")
    public String newForm(@PathVariable Long pid,
                          @PathVariable Long bid,
                          Model m) {
        m.addAttribute("project", projectMapper.findById(pid));
        m.addAttribute("board",   boardMapper.findById(bid));
        m.addAttribute("post",    new Post());  // 빈 모델
        return "post-form";
    }

    // 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long pid,
                           @PathVariable Long bid,
                           @PathVariable Long id,
                           Model m) {
        m.addAttribute("project", projectMapper.findById(pid));
        m.addAttribute("board",   boardMapper.findById(bid));
        m.addAttribute("post",    postMapper.findById(id));
        m.addAttribute("attachments", attMapper.findByPost(id));
        return "post-form";
    }

    // 상세보기
    @GetMapping("/{id}")
    public String view(@PathVariable Long pid,
                       @PathVariable Long bid,
                       @PathVariable Long id,
                       Model m) {
        m.addAttribute("project",     projectMapper.findById(pid));
        m.addAttribute("board",       boardMapper.findById(bid));
        m.addAttribute("detail",      postRestController.get(id).getBody());
        return "post-view";
    }
}
