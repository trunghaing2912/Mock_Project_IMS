package fa.training.fjb04.ims.service.jobs;


import fa.training.fjb04.ims.entity.jobs.Levels;

import java.util.List;

public interface LevelService {

  List<Levels> findAll();

  Levels findByLevelName(String levelName);
}
