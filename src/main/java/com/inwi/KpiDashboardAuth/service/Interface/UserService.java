package com.inwi.KpiDashboardAuth.service.Interface;

import com.inwi.KpiDashboardAuth.dtos.UpdateUserRequestDto;
import com.inwi.KpiDashboardAuth.dtos.UserDto;
import com.inwi.KpiDashboardAuth.exceptions.BadRequestException;
import com.inwi.KpiDashboardAuth.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User findByUsername(String email);
    List<UserDto> getAllUsers();

    void updateUserStatus(String email);

    void updateUser(UpdateUserRequestDto updateUserRequestDto, Integer userId) throws BadRequestException;
}
