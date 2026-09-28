package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.*;
import com.nyumbaiq.backend.domain.enums.PropertyStatus;
import com.nyumbaiq.backend.domain.enums.UnitStatus;
import com.nyumbaiq.backend.domain.enums.UserStatus;
import com.nyumbaiq.backend.domain.repository.*;
import com.nyumbaiq.backend.dto.DashboardStatsDto;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DashboardService {
    private final PropertyRepository propertyRepository;
    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final ManagerAssignmentRepository managerAssignmentRepository;
    private final TenantRepository tenantRepository;
    private final CurrentUser currentUser;

    public DashboardService(PropertyRepository propertyRepository, BuildingRepository buildingRepository,
                            FloorRepository floorRepository, UnitRepository unitRepository,
                            UserRepository userRepository, ManagerAssignmentRepository managerAssignmentRepository,
                            TenantRepository tenantRepository, CurrentUser currentUser) {
        this.propertyRepository = propertyRepository;
        this.buildingRepository = buildingRepository;
        this.floorRepository = floorRepository;
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
        this.managerAssignmentRepository = managerAssignmentRepository;
        this.tenantRepository = tenantRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getStats() {
        long totalProperties = propertyRepository.count();
        long totalBuildings = buildingRepository.count();
        long totalFloors = floorRepository.count();
        long totalUnits = unitRepository.count();
        long occupiedUnits = unitRepository.countByStatus(UnitStatus.OCCUPIED);
        long vacantUnits = unitRepository.countByStatus(UnitStatus.VACANT);

        long totalManagers = userRepository.countByRole(com.nyumbaiq.backend.domain.enums.Role.MANAGER);
        long totalTenants = tenantRepository.count();

        return new DashboardStatsDto(
                totalProperties,
                totalBuildings,
                totalFloors,
                totalUnits,
                occupiedUnits,
                vacantUnits,
                totalManagers,
                totalTenants
        );
    }
}
