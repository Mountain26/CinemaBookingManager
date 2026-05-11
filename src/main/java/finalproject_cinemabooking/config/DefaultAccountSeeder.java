package finalproject_cinemabooking.config;

import finalproject_cinemabooking.model.entity.User;
import finalproject_cinemabooking.model.entity.UserProfile;
import finalproject_cinemabooking.repository.UserProfileRepository;
import finalproject_cinemabooking.repository.UserRepository;
import finalproject_cinemabooking.util.BCryptUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultAccountSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public DefaultAccountSeeder(UserRepository userRepository, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public void run(String... args) {
        seedAccount("admin@gmail.com", "Admin", User.Role.ADMIN);
        seedAccount("nhanvien@gmail.com", "Nhan vien", User.Role.STAFF);
    }

    private void seedAccount(String email, String fullName, User.Role role) {
        String normalizedEmail = email.trim().toLowerCase();

        User user = userRepository.findByUsername(normalizedEmail).orElse(null);
        if (user == null) {
            user = new User();
            user.setUsername(normalizedEmail);
            user.setPassword(BCryptUtil.hashPassword("123456"));
            user.setRole(role);
            user = userRepository.save(user);
        } else if (user.getRole() != role) {
            user.setRole(role);
            userRepository.save(user);
        }

        UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
            profile.setFullName(fullName);
            profile.setEmail(normalizedEmail);
            profile.setPhone("");
            userProfileRepository.save(profile);
        } else {
            profile.setFullName(fullName);
            profile.setEmail(normalizedEmail);
            if (profile.getPhone() == null) {
                profile.setPhone("");
            }
            userProfileRepository.save(profile);
        }
    }
}

