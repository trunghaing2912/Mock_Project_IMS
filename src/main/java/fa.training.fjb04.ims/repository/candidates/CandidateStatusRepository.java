package fa.training.fjb04.ims.repository.candidates;

import fa.training.fjb04.ims.entity.candidates.CandidateStatus;
import org.springframework.data.repository.CrudRepository;

public interface CandidateStatusRepository extends CrudRepository<CandidateStatus,Integer> {

    CandidateStatus findByStatusName(String statusName);
}
