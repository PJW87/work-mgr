package com.example.workmgr.controller.rest;

import com.example.workmgr.mapper.IssueMapper;
import com.example.workmgr.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/issues")
public class IssueRestController {

    private final IssueMapper mapper;

    /** 페이징 + 검색 */
    @GetMapping
    public PageResultDto.PageResult<Issue> list(
            @RequestParam(defaultValue="") String type,
            @RequestParam(defaultValue="") String keyword,
            @RequestParam(required=false) LocalDate from,
            @RequestParam(required=false) LocalDate to,
            @RequestParam(defaultValue="0")  int page,
            @RequestParam(defaultValue="20") int size
    ) {
        int total  = mapper.countAll(type, keyword, from, to);
        var data   = mapper.findPage(type, keyword, from, to, page*size, size);
        return new PageResultDto.PageResult<>(data, total, page, size);
    }

    /** 단건조회 */
    @GetMapping("/{id}")
    public ResponseEntity<IssueDetail> get(@PathVariable Long id) {
        Issue i = mapper.findById(id);
        if (i == null) {
            return ResponseEntity.notFound().build();
        }
//        List<Attachment> files = attMapper.findByPost(id);
//        return ResponseEntity.ok(new PostDetail(p, files));
        return ResponseEntity.ok(new IssueDetail(i));
    }

    /** 생성 */
    @PostMapping
    public ResponseEntity<Issue> create(@RequestBody Issue i) {
        mapper.insert(i);
        return ResponseEntity.ok(i);
    }

    /** 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestBody Issue i
    ) {
        i.setId(id);
        mapper.update(i);
        return ResponseEntity.noContent().build();
    }

    /** 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Issue issue = mapper.findById(id);
        if (issue == null) {
            return ResponseEntity.notFound().build();
        }
        // 2) 본문에 남아 있는 <img> 태그의 URL 추출
        //    간단하게 정규표현식으로 src 에 들어있는 /uploads/파일명 을 뽑아냅니다.
        String content = issue.getContent();
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

//        // 4) 기존에 작성하신 첨부파일(attachments)도 삭제
//        List<Attachment> atts = attMapper.findByIssue(id);
//        atts.forEach(a -> {
//            String stored = a.getStoragePath().replaceFirst("^/uploads/", "");
//            try {
//                Files.deleteIfExists(Paths.get("D:/files", stored));
//            } catch (IOException ignored) {}
//        });
        mapper.delete(id);        return ResponseEntity.noContent().build();
    }

    /**
     * 본문에 드래그&드롭된 이미지 업로드용 API
     * @param file 업로드된 이미지 파일 (form-data key: file)
     * @return 저장된 이미지 URL을 JSON으로 반환
     */
    @PostMapping("/images")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        // 파일 저장
        String folder = "D:\\files\\";
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(folder, filename));

        // 저장된 URL 경로
        String url = "/uploads/" + filename;

        // JSON 형태로 URL 반환
        return ResponseEntity.ok().body(new PostRestController.ImageUploadResponse(url));
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
