package com.inwi.KpiDashboardAuth.controllers;

import com.inwi.KpiDashboardAuth.dtos.TokenValidationRequestDto;
import com.inwi.KpiDashboardAuth.exceptions.UserNotEnabledException;
import com.inwi.KpiDashboardAuth.exceptions.UserNotFoundException;
import com.inwi.KpiDashboardAuth.responses.Response;
import com.inwi.KpiDashboardAuth.service.Implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.inwi.KpiDashboardAuth.responses.LoginResponse;
import com.inwi.KpiDashboardAuth.dtos.LoginUserDto;
import com.inwi.KpiDashboardAuth.dtos.RegisterUserDto;
import com.inwi.KpiDashboardAuth.model.User;
import com.inwi.KpiDashboardAuth.service.AuthenticationService;
import com.inwi.KpiDashboardAuth.service.JwtService;

@RequestMapping("api/v1/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    @Autowired
    private UserServiceImpl userService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Response<String>> register(@RequestBody RegisterUserDto registerUserDto) {
        Response<String> response = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto) {
        LoginResponse loginResponse = new LoginResponse();
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);

            if (authenticatedUser == null) {
                return ResponseEntity.notFound().build();
            }

            String jwtToken = jwtService.generateToken(authenticatedUser, authenticatedUser.getRole().name());

            loginResponse.setToken(jwtToken);
            loginResponse.setExpiresIn(jwtService.getExpirationTime());

            return ResponseEntity.ok(loginResponse);


        } catch (Exception e) {
            if (e instanceof UserNotFoundException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            } else if (e instanceof UserNotEnabledException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<>(403, "ACCOUNT NOT ENABLED"));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    @PostMapping("/validate-token")
    public ResponseEntity<Response<Boolean>> validateToken(@RequestBody TokenValidationRequestDto tokenValidationRequestDto) {
        String userEmail = jwtService.extractUsername(tokenValidationRequestDto.getToken());
        UserDetails userDetails = userService.findByUsername(userEmail);
        return ResponseEntity.ok().body(new Response<>(200, jwtService.isTokenValid(tokenValidationRequestDto.getToken(), userDetails)));
    }
}
