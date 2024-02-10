package fa.training.fjb04.ims.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fa.training.fjb04.ims.entity.BaseEntity;
import fa.training.fjb04.ims.entity.candidates.Positions;
import fa.training.fjb04.ims.entity.offer.Offer;
import fa.training.fjb04.ims.enums.user.Gender;
import fa.training.fjb04.ims.enums.user.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Builder

@Entity
public class User extends BaseEntity<Integer> {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address", length = 1000)
    private String address;

    @Column(name = "phone_number", length = 10, unique = true)
    private String phoneNumber;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "expired_date")
    private LocalDateTime expriedDate;

    @Column(name = "is_reset_successfully")
    private String isResetSuccessfully ;

    @OneToMany (mappedBy = "approvedBy")
    private Set<Offer> offersSet = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonIgnore
    private Department department;

    @ManyToOne
    @JoinColumn(name = "position_id")
    @JsonIgnore
    private Positions position;

    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "role_id")})
    @JsonIgnore
    private Set<Roles> roles = new HashSet<>();

    @Column(name = "avatar")
    private String avatar;
}
