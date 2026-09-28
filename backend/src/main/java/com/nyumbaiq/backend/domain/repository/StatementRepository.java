package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.Statement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StatementRepository extends JpaRepository<Statement, UUID> {
    Page<Statement> findByTenantId(UUID tenantId, Pageable pageable);
    Page<Statement> findByPropertyId(UUID propertyId, Pageable pageable);
    Optional<Statement> findByStatementNumber(String statementNumber);
    boolean existsByStatementNumber(String statementNumber);
}
