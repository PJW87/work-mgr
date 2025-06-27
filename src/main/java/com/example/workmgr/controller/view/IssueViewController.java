package com.example.workmgr.controller.view;

import com.example.workmgr.controller.rest.IssueRestController;
import com.example.workmgr.mapper.IssueMapper;
import com.example.workmgr.model.Issue;
import com.example.workmgr.model.PageResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/issues")
public class IssueViewController {

    private final IssueMapper            issueMapper;
    private final IssueRestController    issueRestController;

    /** 목록 페이지 */
    @GetMapping
    public String list(
            @RequestParam(defaultValue="") String type,
            @RequestParam(defaultValue="") String keyword,
            @RequestParam(required=false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
                     java.time.LocalDate from,
            @RequestParam(required=false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
                     java.time.LocalDate to,
            @RequestParam(defaultValue="0")  int page,
            @RequestParam(defaultValue="20") int size,
            Model model
    ) {
         int total = issueMapper.countAll(type, keyword, from, to);
         PageResultDto.PageResult<Issue> pr =
         new PageResultDto.PageResult<>(
                 issueMapper.findPage(type, keyword, from, to, page * size, size),
                 total, page, size
                                    );
        model.addAttribute("result", pr);
        model.addAttribute("type",    type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("from",    from != null ? from.toString() : "");
        model.addAttribute("to",      to   != null ? to.toString()   : "");
        return "issues";
    }

    /** 새 이슈 등록 폼 */
    @GetMapping("/new")
    public String newForm(Model m) {
        m.addAttribute("issue", new Issue());
        return "issue-form";
    }

    /** 이슈 수정 폼 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model m) {
        m.addAttribute("issue", issueMapper.findById(id));
        return "issue-form";
    }
    // 상세보기
    @GetMapping("/{id}")
    public String view(
                        @PathVariable Long id,
                       Model m) {
        m.addAttribute("detail", issueRestController.get(id).getBody());
        return "issue-view";
    }
}
