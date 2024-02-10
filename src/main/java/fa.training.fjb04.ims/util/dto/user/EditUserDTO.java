package fa.training.fjb04.ims.util.dto.user;

import fa.training.fjb04.ims.entity.candidates.Positions;
import fa.training.fjb04.ims.entity.user.Department;
import fa.training.fjb04.ims.enums.user.Gender;
import fa.training.fjb04.ims.enums.user.UserStatus;
import fa.training.fjb04.ims.util.validation.candidate.MinAge;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class EditUserDTO {

    private Integer id;

    @NotBlank(message = "{ME042}")
    private String fullName;

    @NotBlank(message = "{ME045}")
    @Email(message = "{ME009}")
    private String email;

    @MinAge(value = 18, message = "Date Of Birth must be >= {value}")
    @Past(message = "{ME010}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String address;

    @Length(min = 10, max = 10, message = "Phone Number must be have 10 characters")
    private String phoneNumber;

    @NotNull(message = "{ME047}")
    private Gender gender;

    @NotNull(message = "{ME061}")
    private UserStatus status;

    private String note;

    @NotNull(message = "{ME059}")
    private Department department;

    @NotNull(message = "{ME002}")
    private Positions position;

    @NotNull(message = "{ME060}")
    private Set<String> rolesSet;
}
