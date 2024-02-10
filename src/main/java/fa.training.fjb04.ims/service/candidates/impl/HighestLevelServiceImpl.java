package fa.training.fjb04.ims.service.candidates.impl;

import fa.training.fjb04.ims.entity.candidates.HighestLevel;
import fa.training.fjb04.ims.repository.candidates.HighestLevelRepository;
import fa.training.fjb04.ims.service.candidates.HighestLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class HighestLevelServiceImpl implements HighestLevelService {
    private final HighestLevelRepository highestLevelRepository;
    @Override
    public List<HighestLevel> findAll() {
        return (List<HighestLevel>) highestLevelRepository.findAll();
    }

    @Override
    public HighestLevel findByHighestLevelName(String highestLevelName) {
        return highestLevelRepository.findByHighestLevelName(highestLevelName);
    }
}
