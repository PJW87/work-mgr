package com.example.workmgr.controller.rest;

import com.example.workmgr.mapper.AttachmentMapper;
import com.example.workmgr.mapper.BoardMapper;
import com.example.workmgr.mapper.PostMapper;
import com.example.workmgr.model.Attachment;
import com.example.workmgr.model.PageResultDto;
import com.example.workmgr.model.Post;
import com.example.workmgr.model.PostDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;

// src/main/java/com/example/workmgr/controller/rest/PostRestController.java
@Slf4j
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
            @RequestParam(defaultValue = "") String type,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // 2) count + data
        int total = postMapper.countByBoardAndSearch(bid, type, keyword, from, to);
        List<Post> data = postMapper.findByBoardAndSearch(
                bid, type, keyword, from, to,
                page * size, size
        );
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
            @RequestPart(value="files", required=false) List<MultipartFile> files,
            @RequestPart(value="deleted", required=false) List<Long> deletedIds
    ) throws IOException {
        post.setId(id);
        // 1) 삭제 대기된 첨부 파일들 먼저 처리
        if (deletedIds != null) {
            for (Long aid : deletedIds) {
                // DB에서 경로 조회
                Attachment att = attMapper.findById(aid);
                if (att != null) {
                    // 실제 파일 삭제
                    Path file = Paths.get("D:/files", att.getStoragePath().substring("/uploads/".length()));
                    Files.deleteIfExists(file);
                }
                // DB 레코드 삭제
                attMapper.delete(aid);
            }
        }
        post.setBoardId(bid);
        if(postMapper.update(post)==0) return ResponseEntity.notFound().build();
        if(files!=null) for(var f: files){
            String stored = saveFile(f);
            Attachment a = new Attachment(post.getId(),
                    f.getOriginalFilename(),
                    stored,
                    f.getContentType(),
                    f.getSize());
            attMapper.insert(a);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
                        @PathVariable Long id
    ) {
        // 1) DB 에서 Post 객체(및 content) 가져오기
        Post post = postMapper.findById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        // 2) 본문에 남아 있는 <img> 태그의 URL 추출
        //    간단하게 정규표현식으로 src 에 들어있는 /uploads/파일명 을 뽑아냅니다.
        String content = post.getContent();
        Pattern imgTag = Pattern.compile("<img[^>]+src=\"([^\"]+)\"[^>]*>");
        Matcher matcher = imgTag.matcher(content);

        List<String> urls = new ArrayList<>();
        while (matcher.find()) {
            urls.add(matcher.group(1));  // e.g. "/uploads/abc.jpg"
        }

        // 3) URL → 실제 파일 경로 → 삭제
        for (String url : urls) {
            if (url.startsWith("/uploads/")) {
                String filename = url.substring("/uploads/".length());
                try {
                    Files.deleteIfExists(Paths.get("D:/files", filename));
                } catch (IOException e) {
                    // 로그만 남기고 넘어갑니다.
                    log.warn("Failed to delete inline image file: " + filename, e);
                }
            }
        }

        // 4) 기존에 작성하신 첨부파일(attachments)도 삭제
        List<Attachment> atts = attMapper.findByPost(id);
        atts.forEach(a -> {
            String stored = a.getStoragePath().replaceFirst("^/uploads/", "");
            try {
                Files.deleteIfExists(Paths.get("D:/files", stored));
            } catch (IOException ignored) {}
        });

        // 5) DB 상에서 post.delete(id) 호출
        //    (attachments 는 FK cascade 로 함께 삭제됩니다)
        postMapper.delete(id);

        return ResponseEntity.noContent().build();
    }
    /**
     * 본문에 드래그&드롭된 이미지 업로드용 API
     * @param pid 프로젝트 ID (경로 변수)
     * @param bid 게시판 ID (경로 변수)
     * @param file 업로드된 이미지 파일 (form-data key: file)
     * @return 저장된 이미지 URL을 JSON으로 반환
     */
    @PostMapping("/images")
    public ResponseEntity<?> uploadImage(
            @PathVariable Long pid,
            @PathVariable Long bid,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        // 게시판 존재 여부 확인
        if (boardMapper.findById(bid) == null) {
            return ResponseEntity.badRequest().body("게시판이 존재하지 않습니다.");
        }

        // 파일 저장
        String folder = "D:\\files\\";
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(folder, filename));

        // 저장된 URL 경로
        String url = "/uploads/" + filename;

        // JSON 형태로 URL 반환
        return ResponseEntity.ok().body(new ImageUploadResponse(url));
    }

    // 이미지 업로드 응답 DTO
    public static class ImageUploadResponse {
        private final String url;
        public ImageUploadResponse(String url) {
            this.url = url;
        }
        public String getUrl() {
            return url;
        }
    }
    /**
     * 본문 이미지 삭제용
     * @param path 파일 저장 경로(예: "/uploads/abc.png")
     */
    @DeleteMapping("/images")
    public ResponseEntity<Void> deleteImage(@RequestParam String path) {
        // path 는 "/uploads/..." 형태로 넘어온다고 가정
        String fileName = path.replaceFirst("^/uploads/", "");
        Path file = Paths.get("D:/files", fileName);
        try {
            Files.deleteIfExists(file);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String saveFile(MultipartFile f) throws IOException {
        String folder = "D:\\files\\";
        String name   = UUID.randomUUID()+"_"+f.getOriginalFilename();
        Files.copy(f.getInputStream(), Paths.get(folder,name));
        return "/uploads/"+name;
    }
}
