package com.expensesharing.common.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
public abstract class SoftDeletableEntity {

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
