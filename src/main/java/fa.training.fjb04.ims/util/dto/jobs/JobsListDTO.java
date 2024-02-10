package fa.training.fjb04.ims.util.dto.jobs;


import fa.training.fjb04.ims.enums.job.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class JobsListDTO {

    private Integer id;

    private String title;

    private LocalDate startDate;

    private JobStatus status;

    private LocalDate endDate;
}
