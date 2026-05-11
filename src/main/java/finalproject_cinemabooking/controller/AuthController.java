package finalproject_cinemabooking.controller;

import finalproject_cinemabooking.model.dto.LoginRequest;
import finalproject_cinemabooking.model.dto.RegisterRequest;
import finalproject_cinemabooking.model.entity.User;
import finalproject_cinemabooking.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request);
            return ResponseEntity.ok("Đăng ký thành công! User ID: " + user.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            User user = authService.login(request);

            // Lưu thông tin đăng nhập vào Session
            session.setAttribute("USER_ID", user.getId());
            session.setAttribute("ROLE", user.getRole());

            return ResponseEntity.ok("Đăng nhập thành công với vai trò: " + user.getRole());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Đã đăng xuất!");
    }
}

