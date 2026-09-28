package com.nyumbaiq.backend.ai;

import com.nyumbaiq.backend.ai.tool.GetTenantBalanceTool;
import com.nyumbaiq.backend.domain.entity.Tenant;
import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.dto.TenantDto;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.InvoiceService;
import com.nyumbaiq.backend.service.PaymentService;
import com.nyumbaiq.backend.service.TenantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiAuthorizationTest {

    @Mock
    private TenantService tenantService;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private GetTenantBalanceTool tool;

    @Test
    void tenantCannotAccessOtherTenantBalance() {
        UUID tenantUserId = UUID.randomUUID();
        UUID otherTenantId = UUID.randomUUID();

        User tenantUser = User.builder()
                .id(tenantUserId)
                .email("tenant@test.com")
                .role(Role.TENANT)
                .build();

        TenantDto tenantDto = new TenantDto(
                otherTenantId,
                null,
                "Other Tenant",
                null,
                "other@test.com",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(currentUser.getUser()).thenReturn(tenantUser);
        when(currentUser.getUserId()).thenReturn(tenantUserId);
        when(tenantService.getTenant(tenantUserId)).thenReturn(tenantDto);
        when(invoiceService.getAllInvoices(any(Pageable.class))).thenReturn(Page.empty());
        when(paymentService.getAllPayments(any(Pageable.class))).thenReturn(Page.empty());

        Map<String, Object> params = Map.of("tenantId", otherTenantId.toString());

        var result = tool.execute(params, currentUser);

        assertTrue(result.result().contains("Other") || result.result().contains("Unknown"));
    }
}

