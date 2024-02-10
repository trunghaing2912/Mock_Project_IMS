package fa.training.fjb04.ims.util.dto.interview;


import fa.training.fjb04.ims.enums.interview.InterviewResult;
import fa.training.fjb04.ims.enums.interview.ScheduleStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

public class InterviewListDTO {

    private Integer id;

    private String scheduleTitle;

    private LocalDate interviewDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private InterviewResult result;

    private ScheduleStatus status;

}

