package com.workshop.employee.exception;

import java.time.Instant;

public record ApiError(String message, String errorCode, Instant timestamp) {}
