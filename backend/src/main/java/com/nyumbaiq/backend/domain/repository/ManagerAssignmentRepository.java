package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.ManagerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface ManagerAssignmentRepository extends JpaRepository<ManagerAssignment, UUID> {
    List<ManagerAssignment> findByManagerId(UUID managerId);
    List<ManagerAssignment> findByPropertyId(UUID propertyId);
    boolean existsByManagerIdAndPropertyId(UUID managerId, UUID propertyId);
}
