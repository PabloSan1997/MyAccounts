package com.myaccounts.service.myaccountsservice.services.imp;

import com.myaccounts.service.myaccountsservice.exceptions.MyBadRequestException;
import com.myaccounts.service.myaccountsservice.models.dtos.ItemDto;
import com.myaccounts.service.myaccountsservice.models.dtos.ItemRequestDto;
import com.myaccounts.service.myaccountsservice.models.dtos.PeriodDetailDto;
import com.myaccounts.service.myaccountsservice.models.dtos.PeriodSummaryDto;
import com.myaccounts.service.myaccountsservice.models.dtos.PeriodsResponseDto;
import com.myaccounts.service.myaccountsservice.models.entities.*;
import com.myaccounts.service.myaccountsservice.repositories.*;
import com.myaccounts.service.myaccountsservice.services.PeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PeriodServiceImp implements PeriodService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PeriodRepository periodRepository;

    @Autowired
    private FixedCostRepository fixedCostRepository;

    @Autowired
    private FixedIncomeRepository fixedIncomeRepository;

    @Autowired
    private VariableCostRepository variableCostRepository;

    @Autowired
    private VariableIncomeRepository variableIncomeRepository;

    @Override
    public PeriodsResponseDto getPeriods() {
        UserEntity user = getUserEntity();
        List<PeriodEntity> periods = periodRepository.findByUserId(user.getId());

        final BigDecimal initCapital;
        if (user.getInitCapital() != null && user.getInitCapital().getInitValue() != null) {
            initCapital = user.getInitCapital().getInitValue();
        } else {
            initCapital = BigDecimal.ZERO;
        }

        final List<PeriodEntity> periodList = periods;
        List<PeriodSummaryDto> periodSummaries = periods.stream()
                .map(p -> calculatePeriodSummary(p, periodList, initCapital))
                .collect(Collectors.toList());

        return PeriodsResponseDto.builder()
                .periods(periodSummaries)
                .build();
    }

    @Override
    public PeriodSummaryDto createPeriod() {
        UserEntity user = getUserEntity();
        List<PeriodEntity> existingPeriods = periodRepository.findByUserId(user.getId());

        String created = calculateCreated(existingPeriods);

        PeriodEntity newPeriod = PeriodEntity.builder()
                .user(user)
                .created(created)
                .build();
        newPeriod = periodRepository.save(newPeriod);

        if (!existingPeriods.isEmpty()) {
            PeriodEntity lastPeriod = existingPeriods.get(existingPeriods.size() - 1);
            copyFixedItemsFromPreviousPeriod(lastPeriod, newPeriod);
        }

        BigDecimal initCapital = BigDecimal.ZERO;
        if (user.getInitCapital() != null && user.getInitCapital().getInitValue() != null) {
            initCapital = user.getInitCapital().getInitValue();
        }

        return calculatePeriodSummary(newPeriod, existingPeriods, initCapital);
    }

    @Override
    public void deletePeriod(Long id) {
        UserEntity user = getUserEntity();
        List<PeriodEntity> periods = periodRepository.findByUserId(user.getId());

        if (periods.isEmpty()) {
            throw new MyBadRequestException("No periods to delete");
        }

        PeriodEntity lastPeriod = periods.get(periods.size() - 1);
        if (!lastPeriod.getId().equals(id)) {
            throw new MyBadRequestException("Can only delete the most recent period");
        }

        periodRepository.delete(lastPeriod);
    }

    @Override
    public PeriodDetailDto getPeriodById(Long id) {
        UserEntity user = getUserEntity();
        PeriodEntity period = periodRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new MyBadRequestException("Period not found"));

        List<ItemDto> fixedCosts = fixedCostRepository.findByPeriodId(period.getId()).stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());

        List<ItemDto> fixedIncomes = fixedIncomeRepository.findByPeriodId(period.getId()).stream()
                .map(this::toItemDtoIncome)
                .collect(Collectors.toList());

        List<ItemDto> variableCosts = variableCostRepository.findByPeriodId(period.getId()).stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());

        List<ItemDto> variableIncomes = variableIncomeRepository.findByPeriodId(period.getId()).stream()
                .map(this::toItemDtoIncome)
                .collect(Collectors.toList());

        return PeriodDetailDto.builder()
                .id(period.getId())
                .created(period.getCreated())
                .fixedCosts(fixedCosts)
                .fixedIncomes(fixedIncomes)
                .variableCosts(variableCosts)
                .variableIncomes(variableIncomes)
                .build();
    }

    @Override
    public ItemDto createFixedCost(Long periodId, ItemRequestDto dto) {
        PeriodEntity period = getPeriodEntity(periodId);
        FixedCostEntity entity = FixedCostEntity.builder()
                .period(period)
                .date(Instant.now())
                .value(dto.getValue())
                .title(dto.getTitle())
                .build();
        entity = fixedCostRepository.save(entity);
        return toItemDto(entity);
    }

    @Override
    public ItemDto updateFixedCost(Long periodId, Long fixedCostId, ItemRequestDto dto) {
        getPeriodEntity(periodId);
        FixedCostEntity entity = fixedCostRepository.findById(fixedCostId)
                .orElseThrow(() -> new MyBadRequestException("Fixed cost not found"));
        if (dto.getValue() != null) entity.setValue(dto.getValue());
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        entity = fixedCostRepository.save(entity);
        return toItemDto(entity);
    }

    @Override
    public void deleteFixedCost(Long periodId, Long fixedCostId) {
        getPeriodEntity(periodId);
        FixedCostEntity entity = fixedCostRepository.findById(fixedCostId)
                .orElseThrow(() -> new MyBadRequestException("Fixed cost not found"));
        fixedCostRepository.delete(entity);
    }

    @Override
    public ItemDto createFixedIncome(Long periodId, ItemRequestDto dto) {
        PeriodEntity period = getPeriodEntity(periodId);
        FixedIncomeEntity entity = FixedIncomeEntity.builder()
                .period(period)
                .date(Instant.now())
                .value(dto.getValue())
                .title(dto.getTitle())
                .build();
        entity = fixedIncomeRepository.save(entity);
        return toItemDtoIncome(entity);
    }

    @Override
    public ItemDto updateFixedIncome(Long periodId, Long fixedIncomeId, ItemRequestDto dto) {
        getPeriodEntity(periodId);
        FixedIncomeEntity entity = fixedIncomeRepository.findById(fixedIncomeId)
                .orElseThrow(() -> new MyBadRequestException("Fixed income not found"));
        if (dto.getValue() != null) entity.setValue(dto.getValue());
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        entity = fixedIncomeRepository.save(entity);
        return toItemDtoIncome(entity);
    }

    @Override
    public void deleteFixedIncome(Long periodId, Long fixedIncomeId) {
        getPeriodEntity(periodId);
        FixedIncomeEntity entity = fixedIncomeRepository.findById(fixedIncomeId)
                .orElseThrow(() -> new MyBadRequestException("Fixed income not found"));
        fixedIncomeRepository.delete(entity);
    }

    @Override
    public ItemDto createVariableCost(Long periodId, ItemRequestDto dto) {
        PeriodEntity period = getPeriodEntity(periodId);
        VariableCostEntity entity = VariableCostEntity.builder()
                .period(period)
                .date(Instant.now())
                .value(dto.getValue())
                .title(dto.getTitle())
                .build();
        entity = variableCostRepository.save(entity);
        return toItemDto(entity);
    }

    @Override
    public ItemDto updateVariableCost(Long periodId, Long variableCostId, ItemRequestDto dto) {
        getPeriodEntity(periodId);
        VariableCostEntity entity = variableCostRepository.findById(variableCostId)
                .orElseThrow(() -> new MyBadRequestException("Variable cost not found"));
        if (dto.getValue() != null) entity.setValue(dto.getValue());
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        entity = variableCostRepository.save(entity);
        return toItemDto(entity);
    }

    @Override
    public void deleteVariableCost(Long periodId, Long variableCostId) {
        getPeriodEntity(periodId);
        VariableCostEntity entity = variableCostRepository.findById(variableCostId)
                .orElseThrow(() -> new MyBadRequestException("Variable cost not found"));
        variableCostRepository.delete(entity);
    }

    @Override
    public ItemDto createVariableIncome(Long periodId, ItemRequestDto dto) {
        PeriodEntity period = getPeriodEntity(periodId);
        VariableIncomeEntity entity = VariableIncomeEntity.builder()
                .period(period)
                .date(Instant.now())
                .value(dto.getValue())
                .title(dto.getTitle())
                .build();
        entity = variableIncomeRepository.save(entity);
        return toItemDtoIncome(entity);
    }

    @Override
    public ItemDto updateVariableIncome(Long periodId, Long variableIncomeId, ItemRequestDto dto) {
        getPeriodEntity(periodId);
        VariableIncomeEntity entity = variableIncomeRepository.findById(variableIncomeId)
                .orElseThrow(() -> new MyBadRequestException("Variable income not found"));
        if (dto.getValue() != null) entity.setValue(dto.getValue());
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        entity = variableIncomeRepository.save(entity);
        return toItemDtoIncome(entity);
    }

    @Override
    public void deleteVariableIncome(Long periodId, Long variableIncomeId) {
        getPeriodEntity(periodId);
        VariableIncomeEntity entity = variableIncomeRepository.findById(variableIncomeId)
                .orElseThrow(() -> new MyBadRequestException("Variable income not found"));
        variableIncomeRepository.delete(entity);
    }

    private PeriodEntity getPeriodEntity(Long periodId) {
        UserEntity user = getUserEntity();
        return periodRepository.findByIdAndUserId(periodId, user.getId())
                .orElseThrow(() -> new MyBadRequestException("Period not found"));
    }

    private ItemDto toItemDto(FixedCostEntity entity) {
        return ItemDto.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .value(entity.getValue())
                .title(entity.getTitle())
                .build();
    }

    private ItemDto toItemDtoIncome(FixedIncomeEntity entity) {
        return ItemDto.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .value(entity.getValue())
                .title(entity.getTitle())
                .build();
    }

    private ItemDto toItemDto(VariableCostEntity entity) {
        return ItemDto.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .value(entity.getValue())
                .title(entity.getTitle())
                .build();
    }

    private ItemDto toItemDtoIncome(VariableIncomeEntity entity) {
        return ItemDto.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .value(entity.getValue())
                .title(entity.getTitle())
                .build();
    }

    private UserEntity getUserEntity() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String calculateCreated(List<PeriodEntity> existingPeriods) {
        LocalDate startDate;
        if (existingPeriods.isEmpty()) {
            startDate = LocalDate.now();
        } else {
            startDate = LocalDate.now().plusDays(30L * existingPeriods.size());
        }

        LocalDate endDate = startDate.plusDays(29);

        String startMonthYear = formatMonthYear(startDate);
        String endMonthYear = formatMonthYear(endDate);

        return startMonthYear + " - " + endMonthYear;
    }

    private String formatMonthYear(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM (yyyy)");
        return date.format(formatter);
    }

    private void copyFixedItemsFromPreviousPeriod(PeriodEntity previousPeriod, PeriodEntity newPeriod) {
        List<FixedCostEntity> fixedCosts = fixedCostRepository.findByPeriodId(previousPeriod.getId());
        for (FixedCostEntity cost : fixedCosts) {
            FixedCostEntity newCost = FixedCostEntity.builder()
                    .period(newPeriod)
                    .date(Instant.now())
                    .value(cost.getValue())
                    .title(cost.getTitle())
                    .build();
            fixedCostRepository.save(newCost);
        }

        List<FixedIncomeEntity> fixedIncomes = fixedIncomeRepository.findByPeriodId(previousPeriod.getId());
        for (FixedIncomeEntity income : fixedIncomes) {
            FixedIncomeEntity newIncome = FixedIncomeEntity.builder()
                    .period(newPeriod)
                    .date(Instant.now())
                    .value(income.getValue())
                    .title(income.getTitle())
                    .build();
            fixedIncomeRepository.save(newIncome);
        }
    }

    private PeriodSummaryDto calculatePeriodSummary(PeriodEntity period, List<PeriodEntity> allPeriods, BigDecimal initCapital) {
        BigDecimal totalIncomes = calculateTotalIncomes(period);
        BigDecimal totalCost = calculateTotalCost(period);

        int periodIndex = allPeriods.indexOf(period);

        BigDecimal previousIncomes = BigDecimal.ZERO;
        BigDecimal previousCosts = BigDecimal.ZERO;

        for (int i = 0; i < periodIndex; i++) {
            previousIncomes = previousIncomes.add(calculateTotalIncomes(allPeriods.get(i)));
            previousCosts = previousCosts.add(calculateTotalCost(allPeriods.get(i)));
        }

        BigDecimal total = initCapital
                .add(totalIncomes)
                .add(previousIncomes)
                .subtract(totalCost)
                .subtract(previousCosts);

        return PeriodSummaryDto.builder()
                .id(period.getId())
                .created(period.getCreated())
                .totalIncomes(totalIncomes)
                .totalCost(totalCost)
                .total(total)
                .build();
    }

    private BigDecimal calculateTotalIncomes(PeriodEntity period) {
        BigDecimal total = BigDecimal.ZERO;

        List<FixedIncomeEntity> fixedIncomes = fixedIncomeRepository.findByPeriodId(period.getId());
        for (FixedIncomeEntity income : fixedIncomes) {
            if (income.getValue() != null) {
                total = total.add(income.getValue());
            }
        }

        List<VariableIncomeEntity> variableIncomes = variableIncomeRepository.findByPeriodId(period.getId());
        for (VariableIncomeEntity income : variableIncomes) {
            if (income.getValue() != null) {
                total = total.add(income.getValue());
            }
        }

        return total;
    }

    private BigDecimal calculateTotalCost(PeriodEntity period) {
        BigDecimal total = BigDecimal.ZERO;

        List<FixedCostEntity> fixedCosts = fixedCostRepository.findByPeriodId(period.getId());
        for (FixedCostEntity cost : fixedCosts) {
            if (cost.getValue() != null) {
                total = total.add(cost.getValue());
            }
        }

        List<VariableCostEntity> variableCosts = variableCostRepository.findByPeriodId(period.getId());
        for (VariableCostEntity cost : variableCosts) {
            if (cost.getValue() != null) {
                total = total.add(cost.getValue());
            }
        }

        return total;
    }
}