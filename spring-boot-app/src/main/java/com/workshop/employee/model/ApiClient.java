package com.workshop.employee.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_clients")
public class ApiClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;
    @Column(name = "client_secret", nullable = false)
    private String clientSecret;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected ApiClient() {
    }

    public Integer getId() { return id; }
    public String getClientId() { return clientId; }
    public String getClientSecret() { return clientSecret; }
    public String getUserId() { return userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
