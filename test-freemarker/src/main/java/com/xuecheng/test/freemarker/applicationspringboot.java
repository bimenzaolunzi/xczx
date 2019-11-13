package com.xuecheng.test.freemarker;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class applicationspringboot {
    public static void main(String[] args) {
        SpringApplication.run(applicationspringboot.class,args);
    }
}
