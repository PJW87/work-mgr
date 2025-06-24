package com.example.workmgr.controller.rest;

import com.example.workmgr.mapper.ScheduleMapper;
import com.example.workmgr.model.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ScheduleRestController {
    private final ScheduleMapper mapper;

    /** 달력용: 모든 일정 (과거·미래) **/
    @GetMapping
    public List<Schedule> all() {
        return mapper.findAll();
    }

    /** 상세조회 **/
    @GetMapping("/{id}")
    public ResponseEntity<Schedule> get(@PathVariable Long id) {
        Schedule s = mapper.findById(id);
        return (s==null)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(s);
    }

    /** 생성 **/
    @PostMapping
    public ResponseEntity<Schedule> create(@RequestBody Schedule s) {
        mapper.insert(s);
        return ResponseEntity.ok(s);
    }

    /** 수정 **/
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestBody Schedule s
    ) {
        s.setId(id);
        mapper.update(s);
        return ResponseEntity.noContent().build();
    }

    /** 삭제 **/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mapper.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** 최근 일정(업데이트): 오늘부터 30일 범위 **/
    @GetMapping("/upcoming")
    public List<Schedule> upcoming() {
        // 오늘 자정 기준
        LocalDate today    = LocalDate.now();
        LocalDateTime from = today.minusDays(30).atStartOfDay();
        LocalDateTime to   = today.plusDays(30).atStartOfDay();

        return mapper.findByDateRange(from, to);
    }
}
