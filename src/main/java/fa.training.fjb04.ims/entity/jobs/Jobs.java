package fa.training.fjb04.ims.entity.jobs;


import com.fasterxml.jackson.annotation.JsonIgnore;
import fa.training.fjb04.ims.entity.BaseEntity;
import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.enums.job.JobStatus;
import jakarta.persistence.*;
import lombok.*;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted=false")
public class Jobs extends BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Integer id;

    @Column(name = "title",nullable = false)
    private String title;

    @Column(name = "start_date",nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;

    @Column(name="min_salary",nullable = false)
    private BigDecimal minSalary;

    @Column(name="max_salary",nullable = false)
    private BigDecimal maxSalary;

    @Column(name="working_address",length = 500)
    private String workingAddress;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(name = "end_date",nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;

    @Column(name="description",length = 500)
    private String description;

    private boolean deleted=false;

    @Override
    public String toString() {
        return "Jobs{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startDate=" + startDate +
                ", minSalary=" + minSalary +
                ", maxSalary=" + maxSalary +
                ", workingAddress='" + workingAddress + '\'' +
                ", status=" + status +
                ", endDate=" + endDate +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "job_skill",
            joinColumns = { @JoinColumn(name = "job_id") },
            inverseJoinColumns = {@JoinColumn(name = "skill_id") })
    @JsonIgnore
    private Set<Skills> skillsSet = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "job_benefit",
            joinColumns = { @JoinColumn(name = "job_id") },
            inverseJoinColumns = {@JoinColumn(name = "benefit_id") })
    @JsonIgnore
    private Set<Benefit> benefitSet = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "job_level",
            joinColumns = { @JoinColumn(name = "job_id") },
            inverseJoinColumns = {@JoinColumn(name = "level_id") })
    @JsonIgnore
    private Set<Levels> levelsSet= new HashSet<>();

    @OneToMany(mappedBy = "jobs")
    @JsonIgnore
    private List<Interview> interviewList;

}
