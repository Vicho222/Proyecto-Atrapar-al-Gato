package com.atraparalagato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.atraparalagato")
public class AtraparAlGatoApplication {
    public static void main(String[] args) {
        SpringApplication.run(AtraparAlGatoApplication.class, args);
    }
} 