package com.inwi.KpiDashboardAuth.service.Interface;

import com.inwi.KpiDashboardAuth.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User findByUsername(String email);

}
