package com.expensesharing.feature.auth.application.dto;

public record RegisterCommand(String username, String email, String password, String fullName) {}
