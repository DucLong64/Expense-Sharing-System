package com.expensesharing.feature.house.infrastructure.persistence;

import com.expensesharing.feature.house.domain.model.HouseMember;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HouseMemberRepositoryImpl implements HouseMemberRepository {

    private final HouseMemberJpaRepository jpaRepository;

    @Override
    public HouseMember save(HouseMember member) {
        jpaRepository.save(toEntity(member));
        return member;
    }

    @Override
    public Optional<HouseMember> findByHouseIdAndUserId(UUID houseId, UUID userId) {
        return jpaRepository.findByHouseIdAndUserId(houseId, userId).map(this::toDomain);
    }

    @Override
    public List<HouseMember> findAllByHouseId(UUID houseId) {
        return jpaRepository.findAllByHouseId(houseId).stream().map(this::toDomain).toList();
    }

    @Override
    public void delete(HouseMember member) {
        jpaRepository.deleteById(member.getId());
    }

    @Override
    public boolean existsByHouseIdAndUserId(UUID houseId, UUID userId) {
        return jpaRepository.existsByHouseIdAndUserId(houseId, userId);
    }

    private HouseMemberJpaEntity toEntity(HouseMember member) {
        return HouseMemberJpaEntity.builder()
                .id(member.getId())
                .houseId(member.getHouseId())
                .userId(member.getUserId())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }

    private HouseMember toDomain(HouseMemberJpaEntity entity) {
        return HouseMember.builder()
                .id(entity.getId())
                .houseId(entity.getHouseId())
                .userId(entity.getUserId())
                .role(entity.getRole())
                .joinedAt(entity.getJoinedAt())
                .build();
    }
}
