package fa.training.fjb04.ims.service.jobs;


import fa.training.fjb04.ims.entity.jobs.Benefit;

import java.util.List;

public interface BenefitService {

    List<Benefit> findAll();

    Benefit findByBenefitName(String benefitName);


}
