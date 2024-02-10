package fa.training.fjb04.ims.repository.interview;

import fa.training.fjb04.ims.entity.interview.Interviewer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface InterviewerRepository extends CrudRepository<Interviewer,Integer> {
    Interviewer findByInterviewerName(String interviewerName);


}
