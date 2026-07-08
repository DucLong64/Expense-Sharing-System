package com.expensesharing.feature.settlement.infrastructure.persistence;

import com.expensesharing.feature.settlement.domain.model.Settlement;
import com.expensesharing.feature.settlement.domain.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SettlementRepositoryImpl implements SettlementRepository {

    private final SettlementJpaRepository jpaRepository;

    @Override
    public Settlement save(Settlement settlement) {
        jpaRepository.save(toEntity(settlement));
        return settlement;
    }

    @Override
    public List<Settlement> findAllByHouseId(UUID houseId) {
        return jpaRepository.findAllByHouseIdOrderBySettledAtDesc(houseId)
                .stream().map(this::toDomain).toList();
    }

    private SettlementJpaEntity toEntity(Settlement s) {
        return SettlementJpaEntity.builder()
                .id(s.getId())
                .houseId(s.getHouseId())
                .fromUserId(s.getFromUserId())
                .toUserId(s.getToUserId())
                .amount(s.getAmount())
                .note(s.getNote())
                .settledAt(s.getSettledAt())
                .createdBy(s.getCreatedBy())
                .build();
    }

    private Settlement toDomain(SettlementJpaEntity e) {
        return Settlement.builder()
                .id(e.getId())
                .houseId(e.getHouseId())
                .fromUserId(e.getFromUserId())
                .toUserId(e.getToUserId())
                .amount(e.getAmount())
                .note(e.getNote())
                .settledAt(e.getSettledAt())
                .createdBy(e.getCreatedBy())
                .build();
    }
}
