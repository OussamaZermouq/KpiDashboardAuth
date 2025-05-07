package com.inwi.KpiDashboardAuth.service;
import com.inwi.KpiDashboardAuth.exceptions.UserNotEnabledException;
import com.inwi.KpiDashboardAuth.exceptions.UserNotFoundException;
import com.inwi.KpiDashboardAuth.User.Role;
import com.inwi.KpiDashboardAuth.responses.Response;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.inwi.KpiDashboardAuth.model.User;
import com.inwi.KpiDashboardAuth.dtos.LoginUserDto;
import com.inwi.KpiDashboardAuth.dtos.RegisterUserDto;
import com.inwi.KpiDashboardAuth.repository.UserRepository;

import java.util.Optional;

import static org.springframework.beans.MethodInvocationException.ERROR_CODE;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
        UserRepository userRepository,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    public User authenticate(LoginUserDto input) throws UserNotFoundException, UserNotEnabledException {
        Optional<User> userOptional = userRepository.findByEmail(input.getEmail());
        if(userOptional.isPresent()){
            if (userOptional.get().isEnabled()){

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );

            return userRepository.findByEmail(input.getEmail())
                    .orElseThrow();
            }
            throw new UserNotEnabledException("USER NOT ALLOWED");
        }
        throw new UserNotFoundException("USER NOT FOUND");
    }
}
