package com.alessandro.backend.order_management.service;

import com.alessandro.backend.order_management.entity.User;
import com.alessandro.backend.order_management.exception.DuplicateEmailException;
import com.alessandro.backend.order_management.exception.UserNotFoundException;
import com.alessandro.backend.order_management.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(String email, String name){
        if (userRepository.existsByEmail(email)){
            throw new DuplicateEmailException(email);
        }
        User user = new User(email, name);
        return userRepository.save(user);
    }

    public User getById(Long id){
        return userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException(id));
    }

    public Page<User> list(Pageable pageable) {
        Pageable safe = clampPageable(pageable);
        return userRepository.findAll(safe);
    }

    private Pageable clampPageable(Pageable pageable) {
        int maxSize = 50;
        int size = Math.min(pageable.getPageSize(), maxSize);
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }
}
