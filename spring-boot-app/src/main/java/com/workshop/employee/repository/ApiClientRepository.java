package com.workshop.employee.repository;

import com.workshop.employee.model.ApiClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiClientRepository extends JpaRepository<ApiClient, Integer> {

    Optional<ApiClient> findByClientIdAndClientSecret(String clientId, String clientSecret);
}
