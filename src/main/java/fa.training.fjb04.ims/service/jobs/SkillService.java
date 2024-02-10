package fa.training.fjb04.ims.service.jobs;


import fa.training.fjb04.ims.entity.jobs.Skills;

import java.util.List;

public interface SkillService {

    List<Skills> findAll();
    Skills findBySkillName(String skillName);
}
