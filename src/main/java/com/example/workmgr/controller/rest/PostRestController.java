package com.example.workmgr.controller.rest;

import com.example.workmgr.mapper.AttachmentMapper;
import com.example.workmgr.mapper.BoardMapper;
import com.example.workmgr.mapper.PostMapper;
import com.example.workmgr.model.Attachment;
import com.example.workmgr.model.PageResultDto;
import com.example.workmgr.model.Post;
import com.example.workmgr.model.PostDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

// src/main/java/com/example/workmgr/controller/rest/PostRestController.java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{pid}/boards/{bid}/posts")
public class PostRestController {
    private final PostMapper       postMapper;
    private final AttachmentMapper attMapper;
    private final BoardMapper boardMapper;  // 게시판 매퍼 추가

    @GetMapping
    public PageResultDto.PageResult<Post> list(
            @PathVariable Long bid,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size
    ) {
        int total = postMapper.countByBoard(bid);
        List<Post> data = postMapper.findByBoard(bid, page*size, size);
        return new PageResultDto.PageResult<>(data, total, page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDetail> get(@PathVariable Long id) {
        Post p = postMapper.findById(id);
        if (p==null) return ResponseEntity.notFound().build();
        List<Attachment> files = attMapper.findByPost(id);
        return ResponseEntity.ok(new PostDetail(p, files));
    }

    @PostMapping()
    public ResponseEntity<Post> create(
            @PathVariable("bid") Long bid,
            @RequestPart("post") Post post,
            @RequestPart(value="files", required=false) List<MultipartFile> files
    ) throws IOException {

        // 게시판 존재 여부 확인
        if (boardMapper.findById(bid) == null) {
            return ResponseEntity.badRequest().body(null); // 게시판이 없으면 400 Bad Request 반환
        }
        post.setBoardId(bid);
        postMapper.insert(post);
        if(files!=null) for(var f: files){
            String stored = saveFile(f);
            Attachment a = new Attachment(post.getId(),
                    f.getOriginalFilename(),
                    stored,
                    f.getContentType(),
                    f.getSize());
            attMapper.insert(a);
        }
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long bid,
            @PathVariable Long id,
            @RequestPart("post") Post post,
            @RequestPart(value="files", required=false) List<MultipartFile> files
    ) throws IOException {
        post.setId(id);
        post.setBoardId(bid);
        if(postMapper.update(post)==0) return ResponseEntity.notFound().build();
        // (첨부파일 추가 로직 생략 가능)
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postMapper.delete(id);
        return ResponseEntity.noContent().build();
    }

    private String saveFile(MultipartFile f) throws IOException {
        String folder = "/var/www/app/uploads/";
        String name   = UUID.randomUUID()+"_"+f.getOriginalFilename();
        Files.copy(f.getInputStream(), Paths.get(folder,name));
        return "/uploads/"+name;
    }
}
