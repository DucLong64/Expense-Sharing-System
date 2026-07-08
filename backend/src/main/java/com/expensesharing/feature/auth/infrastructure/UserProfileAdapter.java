package com.expensesharing.feature.auth.infrastructure;

import com.expensesharing.common.port.UserProfilePort;
import com.expensesharing.feature.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserProfileAdapter implements UserProfilePort {

    private final UserRepository userRepository;

    @Override
    public Map<UUID, String> findUsernamesByUserIds(Collection<UUID> userIds) {
        return userRepository.findUsernamesByIds(userIds);
    }
}
