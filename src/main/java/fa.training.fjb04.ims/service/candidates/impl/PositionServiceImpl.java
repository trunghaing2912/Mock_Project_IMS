package fa.training.fjb04.ims.service.candidates.impl;

import fa.training.fjb04.ims.entity.candidates.Positions;
import fa.training.fjb04.ims.repository.candidates.PositionRepository;
import fa.training.fjb04.ims.service.candidates.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    @Override
    public List<Positions>  findAll() {
       return (List<Positions>) positionRepository.findAll();
    }

    @Override
    public Positions findByPositionName(String positionName) {
        return positionRepository.findByPositionName(positionName);
    }
}
