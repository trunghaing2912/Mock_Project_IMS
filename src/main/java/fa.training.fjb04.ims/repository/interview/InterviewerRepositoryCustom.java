package fa.training.fjb04.ims.repository.interview;

import fa.training.fjb04.ims.entity.interview.Interviewer;

import java.util.Optional;

public interface InterviewerRepositoryCustom {
    Interviewer findByInterviewerName(String interviewerName);

}
