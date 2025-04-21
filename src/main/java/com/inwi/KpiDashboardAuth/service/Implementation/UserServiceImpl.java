package com.inwi.KpiDashboardAuth.service.Implementation;

import com.inwi.KpiDashboardAuth.model.User;
import com.inwi.KpiDashboardAuth.repository.UserRepository;
import com.inwi.KpiDashboardAuth.service.Interface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public User findByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()){
            return user.get();
        }
        return null;
    }
}
