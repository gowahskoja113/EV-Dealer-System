package com.swp391.evdealersystem.entity;

import com.swp391.evdealersystem.enums.InstallmentStatus;
import com.swp391.evdealersystem.enums.InstallmentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "installment",
        uniqueConstraints = @UniqueConstraint(name = "uq_order_sequence", columnNames = {"order_id", "sequence"}),
        indexes = @Index(name = "idx_installment_due", columnList = "due_date"))
public class Installment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "installment_id")
    private Long installmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_installment_order"))
    private Order order;

    @Column(name = "sequence", nullable = false)
    private Integer sequence;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 16)
    private InstallmentType type;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "amount_due", precision = 18, scale = 2, nullable = false)
    private BigDecimal amountDue;

    @Column(name = "amount_paid", precision = 18, scale = 2, nullable = false)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "late_fee", precision = 18, scale = 2, nullable = false)
    private BigDecimal lateFee = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private InstallmentStatus status = InstallmentStatus.PENDING;
}
