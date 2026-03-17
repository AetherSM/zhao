package org.example.zhao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.zhao.module.**.mapper")
public class ZhaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhaoApplication.class, args);
    }

}
