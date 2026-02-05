package com.alessandro.backend.order_management.service;

import com.alessandro.backend.order_management.entity.User;
import com.alessandro.backend.order_management.exception.UserNotFoundException;
import com.alessandro.backend.order_management.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(String email, String name){
        User user = new User(email, name);
        return userRepository.save(user);
    }

    public User getById(Long id){
        return userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException(id));
    }
}
