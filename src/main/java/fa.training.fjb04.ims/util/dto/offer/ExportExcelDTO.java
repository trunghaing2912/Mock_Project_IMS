package fa.training.fjb04.ims.util.dto.offer;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ExportExcelDTO {

    @NotNull(message = "Please choose your start date!")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "Please choose your start date!")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

}
