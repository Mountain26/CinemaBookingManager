package finalproject_cinemabooking.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Mã ID tự động tăng

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user; // Bản đồ quan hệ 1-1 với User (Mỗi profile thuộc về 1 user)

    private String fullName; // Họ và tên người dùng

    @Column(unique = true)
    private String email;    // Địa chỉ email

    private String phone;    // Số điện thoại
}

