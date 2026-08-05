package com.workshop.employee.repository;

import com.workshop.employee.model.ApiClient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiClientRepository extends JpaRepository<ApiClient, Integer> {
    Optional<ApiClient> findByClientIdAndClientSecret(String clientId, String clientSecret);
}
