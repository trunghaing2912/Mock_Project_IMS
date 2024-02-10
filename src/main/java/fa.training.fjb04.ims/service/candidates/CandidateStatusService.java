package fa.training.fjb04.ims.service.candidates;

import fa.training.fjb04.ims.entity.candidates.CandidateStatus;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface CandidateStatusService {
    List<CandidateStatus> findAll();

    CandidateStatus findByStatusName(String statusName);

    CandidateStatus findByCandidateStatusName(String candidateStatusName);
}
