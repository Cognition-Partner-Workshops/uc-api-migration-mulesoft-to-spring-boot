package com.workshop.employee.repository;

import com.workshop.employee.model.PtoRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PtoRequestRepository extends JpaRepository<PtoRequest, Integer> {
}
