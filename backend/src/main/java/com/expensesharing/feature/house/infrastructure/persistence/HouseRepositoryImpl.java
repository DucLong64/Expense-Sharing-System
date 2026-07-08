package com.expensesharing.feature.house.infrastructure.persistence;

import com.expensesharing.feature.house.domain.model.House;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HouseRepositoryImpl implements HouseRepository {

    private final HouseJpaRepository jpaRepository;

    @Override
    public House save(House house) {
        jpaRepository.save(toEntity(house));
        return house;
    }

    @Override
    public Optional<House> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<House> findAllByMemberId(UUID userId) {
        return jpaRepository.findAllByMemberId(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private HouseJpaEntity toEntity(House house) {
        return HouseJpaEntity.builder()
                .id(house.getId())
                .name(house.getName())
                .description(house.getDescription())
                .createdBy(house.getCreatedBy())
                .createdAt(house.getCreatedAt())
                .build();
    }

    private House toDomain(HouseJpaEntity entity) {
        return House.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
