package fa.training.fjb04.ims.service.interview;

import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.entity.interview.Interviewer;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InterviewerService {
    List<Interviewer>findAll();
    Interviewer findByInterviewerName(String interviewerName);
    Interviewer save(Interviewer interviewer);

    Optional<Interviewer> findById(Integer id);


}
