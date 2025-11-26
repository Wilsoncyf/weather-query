package com.wilson.weatherquery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOperationLog implements Serializable {
    private String userId;
    private String operation; // 例如: "login", "query_weather"
    private String ip;
    private Long timestamp;
}