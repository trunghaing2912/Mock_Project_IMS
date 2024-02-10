package fa.training.fjb04.ims.util.dto.interview;

import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.entity.candidates.Recruiters;
import fa.training.fjb04.ims.entity.interview.Interviewer;
import fa.training.fjb04.ims.entity.jobs.Jobs;
import fa.training.fjb04.ims.enums.interview.InterviewResult;
import fa.training.fjb04.ims.enums.interview.ScheduleStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InterviewDTO {
    private Integer id;

    @NotEmpty(message = "{ME055}")
    private String scheduleTitle;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "{ME058}")
    @NotNull(message = "{ME056}")
    private LocalDate interviewDate;

    private LocalTime startTime;

    private LocalTime endTime;


    private String notes;

    private String Location;


    private String meetingId;

    private InterviewResult result;

    private ScheduleStatus status;

    @NotNull(message = "{ME042}")
    private String candidates;

    @NotNull(message = "{ME034}")
    private String jobs;

    @NotNull(message = "{ME050}")
    private String recruiters;

    @NotEmpty(message = "{ME057}")
    private Set<String> interviewers = new HashSet<>();
}
