package fa.training.fjb04.ims.repository.job;


import fa.training.fjb04.ims.entity.jobs.Levels;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LevelRepository extends CrudRepository<Levels,Integer> {

    Levels findByLevelName(String levelName);
}
