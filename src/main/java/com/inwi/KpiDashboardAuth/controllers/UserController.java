package com.inwi.KpiDashboardAuth.controllers;

import com.inwi.KpiDashboardAuth.dtos.CreateUserRequestDto;
import com.inwi.KpiDashboardAuth.dtos.UpdateUserRequestDto;
import com.inwi.KpiDashboardAuth.dtos.UpdateUserStatusRequestDto;
import com.inwi.KpiDashboardAuth.exceptions.BadRequestException;
import com.inwi.KpiDashboardAuth.exceptions.UserNotFoundException;
import com.inwi.KpiDashboardAuth.responses.Response;
import com.inwi.KpiDashboardAuth.service.Implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

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
    private ResponseEntity<Response<?>> updateUser(@RequestParam Integer userId, @RequestBody UpdateUserRequestDto updateUserRequestDto){
        if (userId == null){
            return ResponseEntity.badRequest().body(new Response<>(400,"user id is invalid"));
        }
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


    @PostMapping("/create")
    private ResponseEntity<Response<?>> createUser(@RequestBody CreateUserRequestDto createUserRequest) {
        try{
            userService.createUser(createUserRequest);
        }
        catch (Exception e){
            if (e instanceof BadRequestException){
                return ResponseEntity.badRequest().body(new Response<>(400,"Request body is invalid"));

            }
            if (e instanceof DataIntegrityViolationException){
                return ResponseEntity.badRequest().body(new Response<>(409,"Email already exists"));
            }
            return ResponseEntity.badRequest().body(new Response<>(400,"Bad request"));

        }
        return ResponseEntity.ok().body(new Response<>(200, "User has been created successfully"));

    }

    @DeleteMapping("/delete")
    private ResponseEntity<Response<?>> deleteUser(@RequestParam Integer userId){
        try{
            userService.deleteUser(userId);
        }
        catch (Exception e){
            if (e instanceof BadRequestException){
                return ResponseEntity.badRequest().body(new Response<>(400,"Bad request"));
            }
            if (e instanceof UserNotFoundException){
                return ResponseEntity.badRequest().body(new Response<>(400, "User not found"));
            }
            return ResponseEntity.badRequest().build();

        }
        return ResponseEntity.ok().body(new Response<>(200, "User has been deleted successfully"));

    }
}
