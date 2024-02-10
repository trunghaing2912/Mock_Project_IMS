package fa.training.fjb04.ims.service.jobs.Impl;

import fa.training.fjb04.ims.entity.jobs.Skills;
import fa.training.fjb04.ims.repository.job.SkillRepository;
import fa.training.fjb04.ims.service.jobs.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    @Override
    public List<Skills> findAll() {
        return (List<Skills>) skillRepository.findAll();
    }

    @Override
    public Skills findBySkillName(String skillName) {
        return skillRepository.findBySkillName(skillName);
    }
}
