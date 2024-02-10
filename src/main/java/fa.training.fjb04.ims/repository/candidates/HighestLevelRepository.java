package fa.training.fjb04.ims.repository.candidates;

import fa.training.fjb04.ims.entity.candidates.HighestLevel;
import org.springframework.data.repository.CrudRepository;

public interface HighestLevelRepository extends CrudRepository<HighestLevel,Integer> {
    HighestLevel findByHighestLevelName(String highestLevelName);
}
