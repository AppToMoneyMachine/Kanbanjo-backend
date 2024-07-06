package com.bliutvikler.bliutvikler.user.repository;

import com.bliutvikler.bliutvikler.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
