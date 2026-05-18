package com.myaccounts.service.myaccountsservice.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "variable_income")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariableIncomeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_period")
    private PeriodEntity period;

    private Instant date;

    @Column(precision = 19, scale = 2)
    private BigDecimal value;

    @Column(length = 60)
    private String title;
}