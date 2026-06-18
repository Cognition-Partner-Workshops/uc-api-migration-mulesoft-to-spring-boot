package com.workshop.employee.repository;

import com.workshop.employee.model.ApiClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiClientRepository extends JpaRepository<ApiClient, Long> {
    Optional<ApiClient> findByClientIdAndClientSecret(String clientId, String clientSecret);
}
