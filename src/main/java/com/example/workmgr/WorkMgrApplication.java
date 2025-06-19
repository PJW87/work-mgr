package com.example.workmgr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.example.workmgr.mapper")
public class WorkMgrApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkMgrApplication.class, args);
    }
}
