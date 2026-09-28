package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.*;
import com.nyumbaiq.backend.domain.enums.*;
import com.nyumbaiq.backend.domain.repository.*;
import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.exception.*;
import com.nyumbaiq.backend.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public ReceiptService(ReceiptRepository receiptRepository, UserRepository userRepository, CurrentUser currentUser) {
        this.receiptRepository = receiptRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public Page<ReceiptDto> getAllReceipts(Pageable pageable) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getRole() == Role.TENANT) {
            return receiptRepository.findByTenantId(userId, pageable).map(this::toDto);
        }

        return receiptRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public ReceiptDto getReceipt(UUID id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Receipt not found"));
        return toDto(receipt);
    }

    @Transactional(readOnly = true)
    public ReceiptDto getReceiptByNumber(String receiptNumber) {
        Receipt receipt = receiptRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new NotFoundException("Receipt not found"));
        return toDto(receipt);
    }

    @Transactional(readOnly = true)
    public byte[] downloadReceipt(UUID id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Receipt not found"));

        String content = "RECEIPT\n\n" +
                "Receipt Number: " + receipt.getReceiptNumber() + "\n" +
                "Tenant: " + receipt.getTenant().getUser().getFirstName() + " " + receipt.getTenant().getUser().getLastName() + "\n" +
                "Amount: " + receipt.getAmount() + "\n" +
                "Payment Method: " + receipt.getPaymentMethod() + "\n" +
                "Issued At: " + receipt.getIssuedAt() + "\n";

        return content.getBytes();
    }

    private ReceiptDto toDto(Receipt receipt) {
        return new ReceiptDto(
                receipt.getId(),
                receipt.getReceiptNumber(),
                new UserSummary(receipt.getTenant().getUser().getId(), receipt.getTenant().getUser().getFirstName(),
                        receipt.getTenant().getUser().getLastName(), receipt.getTenant().getUser().getEmail(),
                        receipt.getTenant().getUser().getRole(), receipt.getTenant().getUser().getStatus()),
                new PropertySummary(receipt.getProperty().getId(), receipt.getProperty().getName(), receipt.getProperty().getPropertyCode()),
                new UnitSummary(receipt.getUnit().getId(), receipt.getUnit().getUnitNumber()),
                receipt.getAmount(),
                receipt.getPreviousBalance(),
                receipt.getRemainingBalance(),
                receipt.getPaymentMethod(),
                receipt.getProvider(),
                receipt.getTransactionReference(),
                receipt.getIssuedAt(),
                receipt.getCreatedAt()
        );
    }
}
