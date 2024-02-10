package fa.training.fjb04.ims.repository.candidates;

import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.entity.candidates.Positions;
import org.springframework.data.repository.CrudRepository;

public interface PositionRepository extends CrudRepository<Positions,Integer> {
    Positions findByPositionName(String positionName);
}
