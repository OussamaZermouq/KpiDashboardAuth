package com.inwi.KpiDashboardAuth.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFoundException extends UsernameNotFoundException {

    public UserNotFoundException(String msg) {
        super(msg);
    }
}
