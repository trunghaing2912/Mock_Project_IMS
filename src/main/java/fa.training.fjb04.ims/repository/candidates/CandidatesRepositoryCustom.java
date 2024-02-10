package fa.training.fjb04.ims.repository.candidates;

import fa.training.fjb04.ims.entity.candidates.CandidateStatus;
import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.util.dto.candidates.CandidatePositionDTO;
import fa.training.fjb04.ims.util.dto.candidates.CandidateRecruiterDTO;
import fa.training.fjb04.ims.util.dto.candidates.CandidatesListDTO;

import java.util.List;

public interface CandidatesRepositoryCustom  {
    public List<CandidatesListDTO> getCandidatesPaging(Integer pageIndex, Integer pageSize, String search, String status);
    List<CandidatesListDTO> getCandidatesSearch(String search, String status);
    CandidatePositionDTO getPositionById(Integer id);
    CandidateRecruiterDTO getRecruiterById(Integer id);
    List<Candidates> getCandidatesHaveStatusOtherThanBan();
    void updateStatusCancelScheduleInterview(Integer id, CandidateStatus status);
    List<Candidates> getCandidatesHaveStatusPassedInterview();
}
