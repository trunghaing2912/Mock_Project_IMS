package fa.training.fjb04.ims.entity.interview;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fa.training.fjb04.ims.entity.BaseEntity;
import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.entity.candidates.Recruiters;
import fa.training.fjb04.ims.entity.jobs.Jobs;
import fa.training.fjb04.ims.entity.offer.Offer;
import fa.training.fjb04.ims.enums.interview.InterviewResult;
import fa.training.fjb04.ims.enums.interview.ScheduleStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Where(clause = "deleted=False")
public class Interview extends BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "schedule_title")
    private String scheduleTitle;

    @Column(name = "interview_date")
    private LocalDate interviewDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "location")
    private String Location;

    @Column(name = "meeting_id")
    private String meetingId;

    private boolean deleted=false;

    @Column(name = "result",nullable = false)
    @Enumerated(EnumType.STRING)
    private InterviewResult result;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    @NotNull(message = "{ME002}")
    @JsonIgnore
    private Candidates candidates;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    @NotNull(message = "{ME002}")
    @JsonIgnore
    private Jobs jobs;

    @ManyToOne
    @JoinColumn(name = "recruiter_id", nullable = false)
    @NotNull(message = "{ME002}")
    @JsonIgnore
    private Recruiters recruiters;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "interview_interviewer",
            joinColumns = @JoinColumn(name = "interview_id"),
            inverseJoinColumns = @JoinColumn(name = "interviewer_id")
    )
    @NotNull(message = "{ME002}")
    @JsonIgnore
    private Set<Interviewer> interviewers = new HashSet<>();

    @OneToOne(mappedBy = "interviewInfo", cascade = CascadeType.ALL)
    private Offer offer;

}
