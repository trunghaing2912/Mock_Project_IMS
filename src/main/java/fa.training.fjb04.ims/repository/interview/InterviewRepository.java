package fa.training.fjb04.ims.repository.interview;

import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.enums.interview.InterviewResult;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends CrudRepository<Interview, Integer>, InterviewRepositoryCustom {

    List<Interview> findByInterviewDate(LocalDate interviewDate);

    @Query("select i from Interview i join i.candidates c where c.id=:id")
    List<Interview> findByCandidatesId(Integer id);

    @Query("select i from Interview i join i.candidates c where c.id=:id and i.result=:result")
    List<Interview> findInterviewByCandidatesAndResult(Integer id, InterviewResult result);

    @Query("select it from Interview it join it.jobs j where j.id=:id")
    List<Interview> findByJobId(Integer id);
}
