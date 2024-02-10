package fa.training.fjb04.ims.service.candidates;

import fa.training.fjb04.ims.entity.candidates.HighestLevel;
import fa.training.fjb04.ims.entity.candidates.Positions;

import java.util.List;

public interface PositionService {
    List<Positions> findAll();
    Positions findByPositionName(String positionName);
}
