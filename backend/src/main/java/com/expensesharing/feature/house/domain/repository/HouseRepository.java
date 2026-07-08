package com.expensesharing.feature.house.domain.repository;

import com.expensesharing.feature.house.domain.model.House;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HouseRepository {

    House save(House house);

    Optional<House> findById(UUID id);

    List<House> findAllByMemberId(UUID userId);

    void deleteById(UUID id);
}
