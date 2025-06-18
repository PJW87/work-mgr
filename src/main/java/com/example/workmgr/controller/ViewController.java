package com.example.workmgr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping({"/", "/projects", "/schedule", "/tasks", "/issues"})
    public String index() {
        return "forward:/index.html";
    }
}
