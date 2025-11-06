package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import top.ezadmin.annotations.EnableEzadmin;

@SpringBootApplication(scanBasePackages = { "com.example"} )
@Configuration
@EnableEzadmin
public class AiClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiClientApplication.class, args);
    }
 
}