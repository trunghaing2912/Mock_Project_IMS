package fa.training.fjb04.ims.service.candidates;

import fa.training.fjb04.ims.entity.candidates.CandidateStatus;
import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.util.dto.candidates.CandidatePositionDTO;
import fa.training.fjb04.ims.util.dto.candidates.CandidateRecruiterDTO;
import fa.training.fjb04.ims.util.dto.candidates.CandidatesListDTO;
import fa.training.fjb04.ims.util.page.Page;

import java.util.List;
import java.util.Optional;

public interface CandidatesService {

    Page<CandidatesListDTO> getPageCandidates(Integer pageIndex, Integer pageSize, String search, String field);

    CandidatePositionDTO getPagePosition(Integer id);

    CandidateRecruiterDTO getPageRecruiter(Integer id);
    Optional<Candidates> findByCandidateId(Integer id);
    Candidates save(Candidates candidates);
     void deleteCandidate(Integer id);


    Boolean existsByPhone(String phoneNumber);
    Boolean existsByEmail(String email);
    Boolean existsByPhoneAndId(String phoneNumber,Integer id);
    Boolean existsByEmailAndId(String email,Integer id);

    List<Candidates> findAll();
    Candidates findByFullName(String fullName);


    List<Candidates> getCandidatesHaveStatusOtherThanBan();

    void updateStatusCancelScheduleInterview(Integer id, CandidateStatus status);

 boolean existsByPhoneOrEmail(String phone, String email);

 Candidates findCandidatesByOfferId(Integer id);

 List<Candidates> getCandidatesHaveStatusPassedInterview();
}
