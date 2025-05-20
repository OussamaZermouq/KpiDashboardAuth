package com.inwi.KpiDashboardAuth.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class RefreshTokenRequestDto {
    @JsonProperty("refresh-token")
    private String refreshToken;
}
