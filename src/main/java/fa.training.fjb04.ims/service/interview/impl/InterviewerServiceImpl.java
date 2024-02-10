package fa.training.fjb04.ims.service.interview.impl;

import fa.training.fjb04.ims.entity.interview.Interviewer;
import fa.training.fjb04.ims.repository.interview.InterviewerRepository;
import fa.training.fjb04.ims.service.interview.InterviewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewerServiceImpl implements InterviewerService {

    private final InterviewerRepository interviewerRepository;
    @Override
    public List<Interviewer> findAll() {
        return (List<Interviewer>) interviewerRepository.findAll();
    }

    @Override
    public Interviewer findByInterviewerName(String interviewerName) {
        return interviewerRepository.findByInterviewerName(interviewerName);
    }

    @Override
    public Interviewer save(Interviewer interviewer) {
        return interviewerRepository.save(interviewer);
    }

    @Override
    public Optional<Interviewer> findById(Integer id) {
        return interviewerRepository.findById(id);
    }


}
