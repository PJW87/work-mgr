package com.example.workmgr.controller.rest;

import com.example.workmgr.mapper.AttachmentMapper;
import com.example.workmgr.mapper.PostMapper;
import com.example.workmgr.model.Attachment;
import com.example.workmgr.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{pid}/boards/{bid}/posts")
@RequiredArgsConstructor
public class PostRestController {
    private final PostMapper postMapper;
    private final AttachmentMapper attMapper;

    @GetMapping
    public List<Post> list(@PathVariable Long bid) {
        return postMapper.findByBoard(bid);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> get(@PathVariable Long id) {
        Post p = postMapper.findById(id);
        return p!=null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Post> create(
            @PathVariable Long bid,
            @RequestPart("post") Post post,
            @RequestPart(value="files", required=false) List<MultipartFile> files
    ) throws IOException {
        post.setBoardId(bid);
        postMapper.insert(post);
        if(files!=null) {
            for(var f: files) {
                String url = saveFileToDisk(f);
                Attachment att = new Attachment(post.getId(), f.getOriginalFilename(), url);
                attMapper.insert(att);
            }
        }
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestBody Post post
    ) {
        post.setId(id);
        int cnt = postMapper.update(post);
        return cnt>0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postMapper.delete(id);
        return ResponseEntity.noContent().build();
    }

    // (파일 저장 구현 예시)
    private String saveFileToDisk(MultipartFile file) throws IOException {
        String folder = "/var/www/app/uploads/";
        String newName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(folder + newName));
        return "/uploads/" + newName;
    }
}
