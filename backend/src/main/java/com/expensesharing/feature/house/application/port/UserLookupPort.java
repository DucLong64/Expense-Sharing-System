package com.expensesharing.feature.house.application.port;

import java.util.UUID;

public interface UserLookupPort {

    UUID findUserIdByIdentifier(String identifier);
}
