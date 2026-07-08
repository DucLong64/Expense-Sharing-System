package com.expensesharing.feature.house.domain.model;

public enum HouseRole {
    OWNER, ADMIN, MEMBER, VIEWER;

    public boolean isAtLeast(HouseRole required) {
        return this.ordinal() <= required.ordinal();
    }
}
