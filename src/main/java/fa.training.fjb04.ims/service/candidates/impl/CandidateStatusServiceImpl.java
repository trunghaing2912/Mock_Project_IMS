package fa.training.fjb04.ims.service.candidates.impl;

import fa.training.fjb04.ims.entity.candidates.CandidateStatus;
import fa.training.fjb04.ims.repository.candidates.CandidateStatusRepository;
import fa.training.fjb04.ims.service.candidates.CandidateStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateStatusServiceImpl implements CandidateStatusService {
    private final CandidateStatusRepository candidateStatusRepository;
    @Override
    public List<CandidateStatus> findAll() {
        return (List<CandidateStatus>) candidateStatusRepository.findAll();
    }

    @Override
    public CandidateStatus findByStatusName(String statusName) {
        return candidateStatusRepository.findByStatusName(statusName);
    }

    @Override
    public CandidateStatus findByCandidateStatusName(String candidateStatusName) {
        return candidateStatusRepository.findByStatusName(candidateStatusName);
    }


}
