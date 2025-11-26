package com.wilson.weatherquery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSession implements Serializable {
    private String userId;
    private String username;
    private String role; // e.g., "ADMIN", "USER"
}