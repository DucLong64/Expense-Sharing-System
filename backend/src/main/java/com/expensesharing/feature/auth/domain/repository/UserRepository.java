package com.expensesharing.feature.auth.domain.repository;

import com.expensesharing.feature.auth.domain.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

    Map<UUID, String> findUsernamesByIds(Collection<UUID> ids);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
