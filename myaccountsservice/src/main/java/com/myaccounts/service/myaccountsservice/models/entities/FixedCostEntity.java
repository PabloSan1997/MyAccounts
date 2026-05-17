package com.myaccounts.service.myaccountsservice.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fixed_costs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixedCostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_period")
    private PeriodEntity period;

    private LocalDate date;

    @Column(precision = 19, scale = 2)
    private BigDecimal value;
}