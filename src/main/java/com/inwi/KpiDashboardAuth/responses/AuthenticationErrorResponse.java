package com.inwi.KpiDashboardAuth.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationErrorResponse {
    private int status;
    private String message;
    private String error;
} 