package fa.training.fjb04.ims.repository.job;


import fa.training.fjb04.ims.entity.jobs.Skills;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends CrudRepository<Skills,Integer> {

    Skills findBySkillName(String skillName);
}
