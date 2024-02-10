package fa.training.fjb04.ims.repository.job;


import fa.training.fjb04.ims.entity.jobs.Benefit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitRepository extends CrudRepository<Benefit,Integer> {

    Benefit findByBenefitName(String benefitName);
}
