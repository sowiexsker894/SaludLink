package com.saludlink.service;

import com.saludlink.model.dto.RegisterRequestDTO;
import com.saludlink.model.entity.User;
import java.util.Optional;

public interface UserService {

    User registerUser(RegisterRequestDTO dto);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
