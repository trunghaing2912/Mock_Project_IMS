package fa.training.fjb04.ims.service.interview;

import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.entity.interview.Interviewer;
import fa.training.fjb04.ims.enums.interview.InterviewResult;
import fa.training.fjb04.ims.util.dto.interview.*;
import fa.training.fjb04.ims.util.page.Page;
import fa.training.fjb04.ims.util.page.PageInterviewer;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InterviewService {

    Optional<Interview> findById(Integer id);

    void save(Interview interview);

    void updateStatus(Integer id);

    List<Interview> findByInterviewDate(LocalDate interviewDate);

    void sendEmail(String recipientEmail, Interview interview, String link) throws MessagingException, UnsupportedEncodingException;

    Candidates getCandidateByInterviewId(Integer id);

    List<Interview> getInterviewsPass();

    Page<InterviewListDTO> getPageInterview(Integer pageIndex, Integer pageSize, String search, String interviewer, String status);

    InterviewCandidateDTO getPageCandidate(Integer id);

    PageInterviewer<InterviewerListDTO> getPageInterviewer(Integer id);

    InterviewJobDTO getPageJob(Integer id);

    List<Interview> findInterviewByCandidatesAndResult(Integer id, InterviewResult result);

    List<Interview> getListInterviewByCandidateId(Integer id);

    List<Interviewer> findListInterviewerByInterviewId(Integer id);

    List<Interview> findAll();
}
