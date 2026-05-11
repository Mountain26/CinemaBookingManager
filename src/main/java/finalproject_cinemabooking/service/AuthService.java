package finalproject_cinemabooking.service;

import finalproject_cinemabooking.model.dto.LoginRequest;
import finalproject_cinemabooking.model.dto.RegisterRequest;
import finalproject_cinemabooking.model.entity.User;

public interface AuthService {
    User register(RegisterRequest request);
    User login(LoginRequest request);
}

