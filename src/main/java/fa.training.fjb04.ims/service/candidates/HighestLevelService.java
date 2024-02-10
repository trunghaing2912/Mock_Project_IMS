package fa.training.fjb04.ims.service.candidates;

import fa.training.fjb04.ims.entity.candidates.CandidateStatus;
import fa.training.fjb04.ims.entity.candidates.HighestLevel;

import java.util.List;

public interface HighestLevelService {
    List<HighestLevel> findAll();
    HighestLevel findByHighestLevelName(String highestLevelName);
}
