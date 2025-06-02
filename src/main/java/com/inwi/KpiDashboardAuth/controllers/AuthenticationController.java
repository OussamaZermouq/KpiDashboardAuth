package com.inwi.KpiDashboardAuth.controllers;

import com.inwi.KpiDashboardAuth.Dto.RefreshTokenRequestDto;
import com.inwi.KpiDashboardAuth.dtos.TokenValidationRequestDto;
import com.inwi.KpiDashboardAuth.exceptions.BadRequestException;
import com.inwi.KpiDashboardAuth.exceptions.InvalidTokenException;
import com.inwi.KpiDashboardAuth.exceptions.UserNotEnabledException;
import com.inwi.KpiDashboardAuth.exceptions.UserNotFoundException;
import com.inwi.KpiDashboardAuth.responses.AuthenticationResponse;
import com.inwi.KpiDashboardAuth.responses.Response;
import com.inwi.KpiDashboardAuth.service.Implementation.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        try {
            AuthenticationResponse authenticationResponse = authenticationService.authenticate(loginUserDto);

            if (authenticationResponse == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(authenticationResponse);


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
    public ResponseEntity<Response<?>> validateToken(@RequestBody TokenValidationRequestDto tokenValidationRequestDto) {
        System.out.println(tokenValidationRequestDto.getToken());

        try{
            String userEmail = jwtService.extractUsername(tokenValidationRequestDto.getToken().substring(7));
            UserDetails userDetails = userService.findByUsername(userEmail);
            Boolean isTokenValid = jwtService.isTokenValid(tokenValidationRequestDto.getToken().substring(7), userDetails);
            return ResponseEntity.ok().body(new Response<>(200, isTokenValid));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response<>(401,"Token is invalid or expired"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        try {
            AuthenticationResponse authResponse = authenticationService.refreshToken(refreshTokenRequestDto.getRefreshToken().substring(7));
            return ResponseEntity.ok(authResponse);
        }
        catch (UserNotFoundException | InvalidTokenException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response<>(401, "Invalid token"));
        }
        catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(400, "Bad request"));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(500, "An error has occurred"));
        }
    }
}
