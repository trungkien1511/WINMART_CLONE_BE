package com.winmart.common;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class SqlLoader {

    public String load(String path) {
        try {
            var resource = new ClassPathResource(path);
            try (var in = resource.getInputStream()) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load SQL file from classpath: " + path, e);
        }
    }
}
