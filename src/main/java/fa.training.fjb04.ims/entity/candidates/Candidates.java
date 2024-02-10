package fa.training.fjb04.ims.entity.candidates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fa.training.fjb04.ims.entity.BaseEntity;
import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.entity.jobs.Skills;
import fa.training.fjb04.ims.entity.offer.Offer;
import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted=False")
public class Candidates extends BaseEntity<Integer> {

    @Id
    @Column(name = "candidate_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name",nullable = false)
    @NotEmpty(message = "{ME002}")
    private String fullName;

    @Column(name = "date_of_birth",nullable = false)
    @NotNull(message = "{ME002}")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dob;

    @Column(name = "phone",nullable = false, unique = true)
    @NotEmpty(message = "{ME002}")
    private String phone;

    @Column(name = "email",nullable = false, unique = true)
    @Email(message = " {ME009}", regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    @NotEmpty(message = "{ME002}")
    private String email;

    @Column(name = "address", nullable = false)
    @NotEmpty(message = "{ME002}")
    private String address;

    @Column(name = "gender")
    @NotNull(message = "{ME002}")
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @Column(name = "cv_attachment")
    private String cvAttachment;

    @ManyToOne( cascade = CascadeType.ALL)
    @JoinColumn(name = "position_id", nullable = false)
    @NotNull(message = "{ME002}")
    @JsonIgnore
    private Positions position;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinTable(name = "candidate_skill",
            joinColumns = {@JoinColumn(name = "candidate_id")},
            inverseJoinColumns = {@JoinColumn(name = "skill_id")})
    private Set<Skills> skillsSet = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "recruiter_id", nullable = false)
    @NotNull(message = "{ME002}")
    @JsonIgnore
    private Recruiters recruiter;

    @Column(name = "note")
    private String note;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "status_id", nullable = false)
    @NotNull(message = "{ME002}")
    @JsonIgnore
    private CandidateStatus candidateStatus;

    @Column(name="year_of_experience")
    private BigDecimal yearOfExperience;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "highest_level_id", nullable = false)
    @NotNull(message = "{ME002}")
    @JsonIgnore
    private HighestLevel highestLevel;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "candidates")
    @JsonIgnore
    private List<Interview> interviewList;

    @OneToMany(mappedBy = "candidate")
    @JsonIgnore
    private Set<Offer> offers;

    @Column(name = "deleted")
    private Boolean deleted=false;

}
