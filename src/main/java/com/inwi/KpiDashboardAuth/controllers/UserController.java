package com.inwi.KpiDashboardAuth.controllers;

import com.inwi.KpiDashboardAuth.dtos.UpdateUserStatusRequestDto;
import com.inwi.KpiDashboardAuth.responses.Response;
import com.inwi.KpiDashboardAuth.service.Implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/all")
    private ResponseEntity<Response<?>> getAllUsers(){
        return ResponseEntity.ok().body(new Response<>(200, userService.getAllUsers()));
    }

    @PostMapping("/update/status")
    private ResponseEntity<Response<?>> updateUserStatus(@RequestBody UpdateUserStatusRequestDto updateUserStatusRequestDto){
        userService.updateUserStatus(updateUserStatusRequestDto.getUserId());
        return ResponseEntity.ok().body(new Response<>(200, "User has been updated successfully"));

    }


}
