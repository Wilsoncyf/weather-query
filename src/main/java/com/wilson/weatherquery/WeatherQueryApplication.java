package com.wilson.weatherquery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WeatherQueryApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherQueryApplication.class, args);
    }

}
