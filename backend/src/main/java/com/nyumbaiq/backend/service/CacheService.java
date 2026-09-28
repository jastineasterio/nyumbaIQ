package com.nyumbaiq.backend.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheService {
    @Cacheable(value = "dashboardStats", key = "'stats'")
    public Object getDashboardStats() {
        return "cached-stats";
    }

    @CacheEvict(value = "dashboardStats", key = "'stats'")
    public void evictDashboardStats() {
    }

    @Cacheable(value = "referenceData", key = "'roles'")
    public Object getRoles() {
        return "cached-roles";
    }

    @Cacheable(value = "referenceData", key = "'statuses'")
    public Object getStatuses() {
        return "cached-statuses";
    }

    @CacheEvict(value = "referenceData", allEntries = true)
    public void evictReferenceData() {
    }
}
