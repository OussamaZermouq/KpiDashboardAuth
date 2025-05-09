package com.inwi.KpiDashboardAuth.controllers;

import com.inwi.KpiDashboardAuth.dtos.UpdateUserRequestDto;
import com.inwi.KpiDashboardAuth.dtos.UpdateUserStatusRequestDto;
import com.inwi.KpiDashboardAuth.exceptions.BadRequestException;
import com.inwi.KpiDashboardAuth.responses.Response;
import com.inwi.KpiDashboardAuth.service.Implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @PutMapping("/update/status")
    private ResponseEntity<Response<?>> updateUserStatus(@RequestBody UpdateUserStatusRequestDto updateUserStatusRequestDto){
        userService.updateUserStatus(updateUserStatusRequestDto.getEmail());
        return ResponseEntity.ok().body(new Response<>(200, "User has been updated successfully"));

    }

    @PutMapping("/update")
    private ResponseEntity<Response<?>> updateUser(@RequestParam int userId, @RequestBody UpdateUserRequestDto updateUserRequestDto){
        try{
            userService.updateUser(updateUserRequestDto, userId);
        }
        catch (Exception e){
            if (e instanceof BadRequestException){
                return ResponseEntity.badRequest().body(new Response<>(400,"Request body is invalid"));
            }
            else if (e instanceof UsernameNotFoundException){
                return ResponseEntity.badRequest().body(new Response<>(400, "User not found"));
            }
        }
        return ResponseEntity.ok().body(new Response<>(200, "User has been updated successfully"));
    }


}
