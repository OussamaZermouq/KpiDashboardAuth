package com.inwi.KpiDashboardAuth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inwi.KpiDashboardAuth.exceptions.BadRequestException;
import com.inwi.KpiDashboardAuth.exceptions.InvalidTokenException;
import com.inwi.KpiDashboardAuth.exceptions.UserNotEnabledException;
import com.inwi.KpiDashboardAuth.exceptions.UserNotFoundException;
import com.inwi.KpiDashboardAuth.User.Role;
import com.inwi.KpiDashboardAuth.responses.AuthenticationResponse;
import com.inwi.KpiDashboardAuth.responses.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.inwi.KpiDashboardAuth.model.User;
import com.inwi.KpiDashboardAuth.dtos.LoginUserDto;
import com.inwi.KpiDashboardAuth.dtos.RegisterUserDto;
import com.inwi.KpiDashboardAuth.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.beans.MethodInvocationException.ERROR_CODE;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Response<String> signup(RegisterUserDto input) {
        var user = User.builder().
                firstName(input.getFirstName()).
                lastName(input.getLastName()).
                email(input.getEmail()).
                password(passwordEncoder.encode(input.getPassword())).
                role(Role.USER).
                enabled(false).
                build();
        userRepository.save(user);
        return new Response<>(200, "User has been created successfully");
    }


    public AuthenticationResponse authenticate(LoginUserDto input) throws UserNotFoundException, UserNotEnabledException {
        Optional<User> userOptional = userRepository.findByEmail(input.getEmail());
        if (userOptional.isPresent()) {
            if (userOptional.get().isEnabled()) {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                input.getEmail(),
                                input.getPassword()
                        )
                );
                var jwtToken = jwtService.generateTokenAccessToken(userOptional.get());
                var refreshToken = jwtService.generateRefreshToken(userOptional.get());
                return AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build();
            }
            throw new UserNotEnabledException("USER NOT ALLOWED");
        }
        throw new UserNotFoundException("USER NOT FOUND");
    }

    public AuthenticationResponse refreshToken(String refreshToken) throws BadRequestException, InvalidTokenException{
        if (refreshToken == null) {
            throw new BadRequestException("Refresh token is missing");
        }

        String userEmail;
        try {
            userEmail = jwtService.extractUsername(refreshToken);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token format");
        }

        if (userEmail == null) {
            throw new InvalidTokenException("Token does not contain a valid subject");
        }

        var userOptional = this.userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found: " + userEmail);
        }
        var user = userOptional.get();

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Token is not a refresh token");
        }

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }

        var newAccessToken = jwtService.generateTokenAccessToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
