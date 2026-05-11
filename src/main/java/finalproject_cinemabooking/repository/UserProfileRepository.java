package finalproject_cinemabooking.repository;

import finalproject_cinemabooking.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);
    Optional<UserProfile> findByEmailIgnoreCase(String email);
    Optional<UserProfile> findByPhone(String phone);
}
