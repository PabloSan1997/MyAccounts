package com.myaccounts.service.myaccountsservice.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "the_init_capital")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitCapitalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 19, scale = 2)
    private BigDecimal initValue;

    private LocalDate created;

    @OneToOne(mappedBy = "initCapital")
    private UserEntity user;
}