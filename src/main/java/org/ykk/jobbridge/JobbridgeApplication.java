package org.ykk.jobbridge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.ykk.jobbridge.mapper")
public class JobbridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobbridgeApplication.class, args);
    }

}