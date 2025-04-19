package com.inwi.KpiDashboardAuth.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response<T> {
    public int code;
    private T data;
}
