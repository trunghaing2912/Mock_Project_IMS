package fa.training.fjb04.ims.util.dto.candidates;

import fa.training.fjb04.ims.entity.candidates.CandidateStatus;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CandidatesListDTO {

    private Integer id;

    private String fullName;

    private String phone;

    private String email;

    private CandidateStatus candidateStatus;

}
