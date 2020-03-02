package com.oguz.waes.scalableweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackageClasses = {ScalableWebApplication.class})
public class ScalableWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScalableWebApplication.class, args);
    }

}
