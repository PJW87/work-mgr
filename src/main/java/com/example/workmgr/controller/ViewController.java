package com.example.workmgr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/projects")
    public String projects() {
        return "forward:/projects.html";
    }
    @GetMapping("/schedule")
    public String schedule() {
        return "forward:/schedule.html";
    }
    @GetMapping("/tasks")
    public String tasks() {
        return "forward:/tasks.html";
    }
    @GetMapping("/issues")
    public String issues() {
        return "forward:/issues.html";
    }
}
