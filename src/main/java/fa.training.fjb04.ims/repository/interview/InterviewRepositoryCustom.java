package fa.training.fjb04.ims.repository.interview;

import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.entity.interview.Interviewer;
import fa.training.fjb04.ims.util.dto.interview.*;

import java.util.List;

public interface InterviewRepositoryCustom {

    void updateStatus(Integer id);

    Candidates getCandidateByInterviewId(Integer id);

    List<Interview> getInterviewsPass();

    List<InterviewListDTO> getAllInterview(String search, String interviewer, String status);

    List<InterviewListDTO> getInterviewPaging(Integer pageIndex, Integer pageSize, String search, String interviewer, String status);

    List<InterviewerListDTO> getInterviewerById(Integer id);

    InterviewCandidateDTO getCandidateById(Integer id);

    InterviewJobDTO getJobById(Integer id);

    List<Interview> getListInterviewByCandidateId(Integer id);

    List<Interviewer> findListInterviewerByInterviewId(Integer id);

}
