package com.workshop.employee.repository;

import com.workshop.employee.model.ApiClient;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiClientRepository extends JpaRepository<ApiClient, Integer> {

    Optional<ApiClient> findByClientIdAndClientSecret(String clientId, String clientSecret);

    Optional<ApiClient> findByAccessTokenAndExpiresAtAfter(String accessToken, LocalDateTime now);
}
