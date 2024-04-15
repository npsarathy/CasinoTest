package org.casino.aggregator.service;

import org.casino.aggregator.entity.User;
import org.casino.aggregator.repository.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRespository userRespository;

    public String getLoginToken(String username, String password) {
        User userEntity = userRespository.findByUsernameAndPassword(username, password);
        if (userEntity == null) {
            return null;
        }
        return userEntity.getToken();
    }

    public Boolean isTokenValid(String token) {
        return userRespository.findByToken(token) != null;

    }
}
