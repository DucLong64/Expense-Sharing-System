package com.expensesharing.common.port;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface UserProfilePort {

    Map<UUID, String> findUsernamesByUserIds(Collection<UUID> userIds);
}
