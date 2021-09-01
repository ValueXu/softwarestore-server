package cn.jxufe.valuexu.softwarestoreserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SoftwarestoreServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(SoftwarestoreServerApplication.class, args);
    }

}
