package com.winmart.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MyApplicationRunner.class);

    @Override
    public void run(ApplicationArguments args) {
        log.info("🚀 ApplicationRunner chạy sau khi Spring Boot start!");
        log.info("Option names: {}", args.getOptionNames());
    }
}

    