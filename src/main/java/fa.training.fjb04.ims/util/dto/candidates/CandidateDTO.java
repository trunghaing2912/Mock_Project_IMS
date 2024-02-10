package fa.training.fjb04.ims.util.dto.candidates;

import fa.training.fjb04.ims.entity.candidates.CandidateStatus;
import fa.training.fjb04.ims.entity.candidates.HighestLevel;
import fa.training.fjb04.ims.entity.candidates.Positions;
import fa.training.fjb04.ims.entity.candidates.Recruiters;
import fa.training.fjb04.ims.enums.Gender;
import fa.training.fjb04.ims.util.validation.candidate.MinAge;
import fa.training.fjb04.ims.util.validation.candidate.UniqueField;
import fa.training.fjb04.ims.util.validation.candidate.ValidFormatFile;
import jakarta.validation.constraints.*;
import lombok.*;
import org.apache.catalina.User;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class CandidateDTO {
    private Integer id;

    @NotBlank(message = "{ME042}")
    private String fullName;

    @MinAge(value = 18, message = "Age of candidate must be >= {value}")
    @NotNull(message = "{ME043}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "{ME010}")
    private LocalDate dob;

    @NotEmpty(message ="{ME044}")
    private String phone;

    @NotNull(message = "{ME045}")
    private String email;

    @NotBlank(message = "{ME046}")
    private String address;

    @NotNull(message = "{ME047}")
    private Gender gender;

    @ValidFormatFile(message = "Invalid file format.Allowed formats: pdf, doc, docx")
    private MultipartFile cvAttachment;

    @NotNull(message = "{ME048}")
    private Positions position;

    @NotEmpty(message = "{ME049}")
    private Set<String> skillsSet = new HashSet<>();

    @NotNull(message = "{ME050}")
    private Recruiters recruiter;

    private String note;

    @NotNull(message = "{ME051}")
    private CandidateStatus candidateStatus;

    @NotNull(message = " {ME052}")
    @Min(value=0,message = "Salary can not less than 0")
    private BigDecimal yearOfExperience;

    @NotNull(message = "{ME053}")
    private HighestLevel highestLevel;

    private User user;
}
