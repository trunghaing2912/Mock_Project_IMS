package fa.training.fjb04.ims.service.jobs.Impl;


import fa.training.fjb04.ims.entity.jobs.Levels;
import fa.training.fjb04.ims.repository.job.LevelRepository;
import fa.training.fjb04.ims.service.jobs.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class LevelServiceImpl implements LevelService {

    private final LevelRepository levelRepository;
    @Override
    public List<Levels> findAll() {
        return (List<Levels>) levelRepository.findAll();
    }

    @Override
    public Levels findByLevelName(String levelName) {
        return levelRepository.findByLevelName(levelName);
    }
}
