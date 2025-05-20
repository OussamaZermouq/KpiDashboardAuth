package com.inwi.KpiDashboardAuth.service.Implementation;

import com.inwi.KpiDashboardAuth.User.Role;
import com.inwi.KpiDashboardAuth.dtos.CreateUserRequestDto;
import com.inwi.KpiDashboardAuth.dtos.UpdateUserRequestDto;
import com.inwi.KpiDashboardAuth.dtos.UserDto;
import com.inwi.KpiDashboardAuth.exceptions.BadRequestException;
import com.inwi.KpiDashboardAuth.exceptions.UserNotFoundException;
import com.inwi.KpiDashboardAuth.model.User;
import com.inwi.KpiDashboardAuth.repository.UserRepository;
import com.inwi.KpiDashboardAuth.service.Interface.UserService;
import org.hibernate.query.spi.QueryOptionsAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> userDtoList = new ArrayList<>();
        userRepository.findAll().forEach((user -> {
            userDtoList.add(UserDto.builder().
                    userId(user.getId()).
                    firstName(user.getFirstName()).
                    lastName(user.getLastName()).
                    email(user.getEmail()).
                    role(user.getRole().name()).
                    createdAt(user.getCreatedAt()).
                    isEnabled(user.isEnabled()).
                    build()
            );
        }));
        return userDtoList;
    }

    @Override
    public void updateUserStatus(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("USERNAME NOT FOUND");
        }
        User updatedUser = userOptional.get();
        updatedUser.setIsEnabled(!userOptional.get().isEnabled());
        userRepository.save(updatedUser);
    }

    @Override
    public void updateUser(UpdateUserRequestDto updateUserRequestDto, Integer userId) throws BadRequestException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("USER NOT FOUND");
        }
        if (updateUserRequestDto.getEmail() == null ||
            updateUserRequestDto.getFirstName() == null ||
            updateUserRequestDto.getLastName() == null ||
            updateUserRequestDto.getRole() == null
        ) {
            throw new BadRequestException("NULL FIELDS IN REQUEST");

        }
        User user = userOptional.get();
        user.setEmail(updateUserRequestDto.getEmail());
        user.setFirstName(updateUserRequestDto.getFirstName());
        user.setLastName(updateUserRequestDto.getLastName());
        user.setRole(Role.valueOf(updateUserRequestDto.getRole()));
        //If the user is the one who is sending the update request
        if (updateUserRequestDto.getPassword()!=null)
            user.setPassword(passwordEncoder.encode(updateUserRequestDto.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void createUser(CreateUserRequestDto createUserRequestDto) throws BadRequestException {
        if (createUserRequestDto.getFirstName() ==null ||
                createUserRequestDto.getLastName() == null ||
                createUserRequestDto.getEmail() == null ||
                createUserRequestDto.getUserRole() == null ||
                createUserRequestDto.getPassword() == null) {
            throw new BadRequestException("INVALID USER DETAILS");
        }
        User user = User.builder().
                firstName(createUserRequestDto.getFirstName()).
                lastName(createUserRequestDto.getLastName()).
                email(createUserRequestDto.getEmail()).
                role(Role.valueOf(createUserRequestDto.getUserRole())).
                password(passwordEncoder.encode(createUserRequestDto.getPassword())).
                build();

        userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer userId) throws BadRequestException {
        if (userId==null){
            throw new BadRequestException("INVALID USER ID");
        }
        Optional<User> userToDelete = userRepository.findById(userId);
        if (!userToDelete.isPresent()){
            throw new UserNotFoundException("NO USER WITH ID" + userId);
        }
        userRepository.delete(userToDelete.get());
    }

}
