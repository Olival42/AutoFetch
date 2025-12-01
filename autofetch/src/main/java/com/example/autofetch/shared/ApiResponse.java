package com.example.autofetch.shared;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private ErrorResponse error;
    @Default
    private Instant timestamp = Instant.now();
}
