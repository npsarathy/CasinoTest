package org.casino.aggregator.repository;

import org.casino.aggregator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRespository extends JpaRepository<User, Integer> {
    User findByUsernameAndPassword(String username, String password);
    User findByToken(String token);
}
