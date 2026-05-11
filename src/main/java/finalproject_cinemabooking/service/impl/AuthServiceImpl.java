package finalproject_cinemabooking.service.impl;

import finalproject_cinemabooking.model.dto.LoginRequest;
import finalproject_cinemabooking.model.dto.RegisterRequest;
import finalproject_cinemabooking.model.entity.User;
import finalproject_cinemabooking.model.entity.UserProfile;
import finalproject_cinemabooking.repository.UserProfileRepository;
import finalproject_cinemabooking.repository.UserRepository;
import finalproject_cinemabooking.service.AuthService;
import finalproject_cinemabooking.util.BCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        String normalizedEmail = request.getEmail() == null ? "" : request.getEmail().trim().toLowerCase();
        String normalizedPhone = request.getPhone() == null ? "" : request.getPhone().trim();
        String normalizedFullName = request.getFullName() == null ? "" : request.getFullName().trim();
        String normalizedPassword = request.getPassword() == null ? "" : request.getPassword().trim();
        String normalizedConfirmPassword = request.getConfirmPassword() == null ? "" : request.getConfirmPassword().trim();

        if (normalizedEmail.isEmpty()) {
            throw new RuntimeException("Vui lòng nhập email.");
        }
        if (!normalizedEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new RuntimeException("Email không đúng định dạng.");
        }
        if (normalizedPassword.isEmpty()) {
            throw new RuntimeException("Vui lòng nhập mật khẩu.");
        }
        if (normalizedConfirmPassword.isEmpty()) {
            throw new RuntimeException("Vui lòng xác nhận mật khẩu.");
        }
        if (!normalizedConfirmPassword.equals(normalizedPassword)) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp.");
        }
        if (normalizedFullName.isEmpty()) {
            throw new RuntimeException("Vui lòng nhập họ tên.");
        }
        if (!normalizedPhone.isEmpty() && !normalizedPhone.matches("\\d+")) {
            throw new RuntimeException("Số điện thoại chỉ được chứa chữ số.");
        }

        if (userProfileRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            throw new RuntimeException("Email đã tồn tại!");
        }
        if (!normalizedPhone.isEmpty() && userProfileRepository.findByPhone(normalizedPhone).isPresent()) {
            throw new RuntimeException("Số điện thoại đã tồn tại!");
        }
        if (userRepository.findByUsername(normalizedEmail).isPresent()) {
            throw new RuntimeException("Email đã tồn tại!");
        }

        User user = new User();
        user.setUsername(normalizedEmail);
        // Mã hóa mật khẩu
        String hash = BCryptUtil.hashPassword(normalizedPassword);
        user.setPassword(hash);
        user.setRole(User.Role.CUSTOMER);

        user = userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setFullName(normalizedFullName);
        profile.setEmail(normalizedEmail);
        profile.setPhone(normalizedPhone);
        userProfileRepository.save(profile);

        return user;
    }

    @Override
    public User login(LoginRequest request) {
        String normalizedEmail = request.getEmail() == null ? "" : request.getEmail().trim().toLowerCase();
        if (normalizedEmail.isEmpty()) {
            throw new RuntimeException("Vui lòng nhập email.");
        }

        UserProfile profile = userProfileRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại!"));
        User user = profile.getUser();

        // Kiểm tra mật khẩu băm
        if (!BCryptUtil.checkPassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Sai mật khẩu!");
        }

        return user;
    }
}
