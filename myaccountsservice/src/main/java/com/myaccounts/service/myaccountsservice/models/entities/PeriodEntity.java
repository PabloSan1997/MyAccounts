package com.myaccounts.service.myaccountsservice.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "the_periods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserEntity user;

    private String created;

    @OneToMany(mappedBy = "period", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FixedCostEntity> fixedCosts = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "period", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FixedIncomeEntity> fixedIncomes = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "period", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VariableCostEntity> variableCosts = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "period", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VariableIncomeEntity> variableIncomes = new java.util.ArrayList<>();
}