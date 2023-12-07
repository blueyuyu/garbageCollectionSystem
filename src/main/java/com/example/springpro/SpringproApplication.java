package com.example.springpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.oas.annotations.EnableOpenApi;

@RestController
@SpringBootApplication
@EnableOpenApi
public class SpringproApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringproApplication.class, args);
    }
}
