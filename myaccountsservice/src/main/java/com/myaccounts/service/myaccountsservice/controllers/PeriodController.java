package com.myaccounts.service.myaccountsservice.controllers;

import com.myaccounts.service.myaccountsservice.models.dtos.ItemDto;
import com.myaccounts.service.myaccountsservice.models.dtos.ItemRequestDto;
import com.myaccounts.service.myaccountsservice.models.dtos.PeriodDetailDto;
import com.myaccounts.service.myaccountsservice.models.dtos.PeriodSummaryDto;
import com.myaccounts.service.myaccountsservice.models.dtos.PeriodsResponseDto;
import com.myaccounts.service.myaccountsservice.services.PeriodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/periods")
public class PeriodController {

    @Autowired
    private PeriodService periodService;

    @GetMapping
    public ResponseEntity<PeriodsResponseDto> getPeriods() {
        return ResponseEntity.ok(periodService.getPeriods());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeriodDetailDto> getPeriodById(@PathVariable Long id) {
        return ResponseEntity.ok(periodService.getPeriodById(id));
    }

    @PostMapping
    public ResponseEntity<PeriodSummaryDto> createPeriod() {
        return ResponseEntity.ok(periodService.createPeriod());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePeriod(@PathVariable Long id) {
        periodService.deletePeriod(id);
        return ResponseEntity.noContent().build();
    }

    // Fixed Costs
    @PostMapping("/{id}/costfixed")
    public ResponseEntity<ItemDto> createFixedCost(@PathVariable Long id, @Valid @RequestBody ItemRequestDto dto) {
        return ResponseEntity.ok(periodService.createFixedCost(id, dto));
    }

    @PatchMapping("/{id}/costfixed/{costId}")
    public ResponseEntity<ItemDto> updateFixedCost(@PathVariable Long id, @PathVariable Long costId, @Valid @RequestBody ItemRequestDto dto) {
        return ResponseEntity.ok(periodService.updateFixedCost(id, costId, dto));
    }

    @DeleteMapping("/{id}/costfixed/{costId}")
    public ResponseEntity<Void> deleteFixedCost(@PathVariable Long id, @PathVariable Long costId) {
        periodService.deleteFixedCost(id, costId);
        return ResponseEntity.noContent().build();
    }

    // Fixed Incomes
    @PostMapping("/{id}/incomefixed")
    public ResponseEntity<ItemDto> createFixedIncome(@PathVariable Long id, @Valid @RequestBody ItemRequestDto dto) {
        return ResponseEntity.ok(periodService.createFixedIncome(id, dto));
    }

    @PatchMapping("/{id}/incomefixed/{incomeId}")
    public ResponseEntity<ItemDto> updateFixedIncome(@PathVariable Long id, @PathVariable Long incomeId, @Valid @RequestBody ItemRequestDto dto) {
        return ResponseEntity.ok(periodService.updateFixedIncome(id, incomeId, dto));
    }

    @DeleteMapping("/{id}/incomefixed/{incomeId}")
    public ResponseEntity<Void> deleteFixedIncome(@PathVariable Long id, @PathVariable Long incomeId) {
        periodService.deleteFixedIncome(id, incomeId);
        return ResponseEntity.noContent().build();
    }

    // Variable Costs
    @PostMapping("/{id}/costvariable")
    public ResponseEntity<ItemDto> createVariableCost(@PathVariable Long id, @Valid @RequestBody ItemRequestDto dto) {
        return ResponseEntity.ok(periodService.createVariableCost(id, dto));
    }

    @PatchMapping("/{id}/costvariable/{costId}")
    public ResponseEntity<ItemDto> updateVariableCost(@PathVariable Long id, @PathVariable Long costId, @Valid @RequestBody ItemRequestDto dto) {
        return ResponseEntity.ok(periodService.updateVariableCost(id, costId, dto));
    }

    @DeleteMapping("/{id}/costvariable/{costId}")
    public ResponseEntity<Void> deleteVariableCost(@PathVariable Long id, @PathVariable Long costId) {
        periodService.deleteVariableCost(id, costId);
        return ResponseEntity.noContent().build();
    }

    // Variable Incomes
    @PostMapping("/{id}/incomevariable")
    public ResponseEntity<ItemDto> createVariableIncome(@PathVariable Long id, @Valid @RequestBody ItemRequestDto dto) {
        return ResponseEntity.ok(periodService.createVariableIncome(id, dto));
    }

    @PatchMapping("/{id}/incomevariable/{incomeId}")
    public ResponseEntity<ItemDto> updateVariableIncome(@PathVariable Long id, @PathVariable Long incomeId, @Valid @RequestBody ItemRequestDto dto) {
        return ResponseEntity.ok(periodService.updateVariableIncome(id, incomeId, dto));
    }

    @DeleteMapping("/{id}/incomevariable/{incomeId}")
    public ResponseEntity<Void> deleteVariableIncome(@PathVariable Long id, @PathVariable Long incomeId) {
        periodService.deleteVariableIncome(id, incomeId);
        return ResponseEntity.noContent().build();
    }
}