package com.expensesharing.feature.auth.infrastructure.house;

import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.auth.domain.model.User;
import com.expensesharing.feature.auth.domain.repository.UserRepository;
import com.expensesharing.feature.auth.domain.service.UsernamePolicy;
import com.expensesharing.feature.house.application.port.UserLookupPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserLookupAdapter implements UserLookupPort {

    private final UserRepository userRepository;

    @Override
    public UUID findUserIdByIdentifier(String identifier) {
        String trimmed = identifier == null ? "" : identifier.trim();
        if (trimmed.isEmpty()) {
            throw new NotFoundException("USER_NOT_FOUND", "User not found.");
        }

        User user = trimmed.contains("@")
                ? userRepository.findByEmail(trimmed.toLowerCase())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found."))
                : userRepository.findByUsername(UsernamePolicy.normalize(trimmed))
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found."));

        return user.getId();
    }
}
