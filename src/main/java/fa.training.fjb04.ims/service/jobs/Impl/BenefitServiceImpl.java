package fa.training.fjb04.ims.service.jobs.Impl;


import fa.training.fjb04.ims.entity.jobs.Benefit;
import fa.training.fjb04.ims.repository.job.BenefitRepository;
import fa.training.fjb04.ims.service.jobs.BenefitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BenefitServiceImpl implements BenefitService {

    private final BenefitRepository benefitRepository;
    @Override
    public List<Benefit> findAll() {
        return (List<Benefit>) benefitRepository.findAll();
    }

    @Override
    public Benefit findByBenefitName(String benefitName) {
        return benefitRepository.findByBenefitName(benefitName);
    }
}
