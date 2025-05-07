package com.inwi.KpiDashboardAuth.service.Interface;

import com.inwi.KpiDashboardAuth.dtos.UserDto;
import com.inwi.KpiDashboardAuth.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User findByUsername(String email);
    List<UserDto> getAllUsers();

    void updateUserStatus(int userId);
}
