package com.workshop.employee.repository;

import com.workshop.employee.model.ApiClient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiClientRepository extends JpaRepository<ApiClient, Integer> {

    /**
     * {@code SELECT client_id, client_secret, user_id FROM api_clients
     * WHERE client_id = :clientId AND client_secret = :clientSecret}
     */
    Optional<ApiClient> findByClientIdAndClientSecret(String clientId, String clientSecret);
}
