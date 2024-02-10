package fa.training.fjb04.ims.util.dto.jobs;



import fa.training.fjb04.ims.enums.job.JobStatus;
import fa.training.fjb04.ims.util.validation.addNewJob.DateOrder;
import fa.training.fjb04.ims.util.validation.addNewJob.MaxSalary;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

@DateOrder({"startDate","endDate"})
@MaxSalary({"minSalary","maxSalary"})
public class JobsDTO {

    private Integer id;

    @NotBlank(message = "{ME034}")
    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "{ME017}")
    @NotNull(message = "{ME035}" )
    private LocalDate startDate;

    @Min(value = 0, message = "{ME028}")
    private BigDecimal minSalary;

    @NotNull(message = "{ME036}")
    @Min(value = 0, message = "{ME028}")
    private BigDecimal maxSalary;

    @NotBlank(message = "{ME037}")
    private String workingAddress;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{ME038}")
    private LocalDate endDate;

    private String description;

    @NotEmpty(message = "{ME039}")
    private Set<String> skillsSet = new HashSet<>();

    @NotEmpty(message = "{ME040}")
    private Set<String> benefitSet = new HashSet<>();

    @NotEmpty(message = "{ME041}")
    private Set<String> levelsSet = new HashSet<>();

    private JobStatus status;
}
