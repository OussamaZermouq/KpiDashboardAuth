package com.inwi.KpiDashboardAuth.repository;

import com.inwi.KpiDashboardAuth.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
