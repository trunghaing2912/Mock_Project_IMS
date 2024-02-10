package fa.training.fjb04.ims.service.candidates.impl;

import fa.training.fjb04.ims.config.security.SecurityUtils;
import fa.training.fjb04.ims.entity.candidates.CandidateStatus;
import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.entity.offer.Offer;
import fa.training.fjb04.ims.repository.candidates.CandidatesRepository;
import fa.training.fjb04.ims.repository.interview.InterviewRepository;
import fa.training.fjb04.ims.repository.offer.OfferRepository;
import fa.training.fjb04.ims.service.candidates.CandidateStatusService;
import fa.training.fjb04.ims.service.candidates.CandidatesService;
import fa.training.fjb04.ims.util.dto.candidates.CandidatePositionDTO;
import fa.training.fjb04.ims.util.dto.candidates.CandidateRecruiterDTO;
import fa.training.fjb04.ims.util.dto.candidates.CandidatesListDTO;
import fa.training.fjb04.ims.util.page.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidatesServiceImpl implements CandidatesService {

    private final CandidatesRepository candidatesRepository;
    private  final CandidateStatusService candidateStatusService;
    private final InterviewRepository interviewRepository;
    private final OfferRepository offerRepository;

    @Override
    public Page<CandidatesListDTO> getPageCandidates(Integer pageIndex, Integer pageSize, String search, String field) {
        List<CandidatesListDTO> candidatesList = candidatesRepository.getCandidatesPaging(pageIndex,pageSize,search,field);
        int pageTotal = (int) Math.ceil(candidatesRepository.getCandidatesSearch(search,field).size()*1.0/pageSize);
        String role = SecurityUtils.getCurrentRole().toString();
        Page<CandidatesListDTO> page = new Page<>(pageTotal,pageIndex,candidatesList,pageSize,role);
        return page;
    }

    @Override
    public CandidatePositionDTO getPagePosition(Integer id) {

        return candidatesRepository.getPositionById(id);
    }

    @Override
    public CandidateRecruiterDTO getPageRecruiter(Integer id) {

        return candidatesRepository.getRecruiterById(id);
    }

    @Override
    public Optional<Candidates> findByCandidateId(Integer id) {
        return candidatesRepository.findById(id);
    }

    @Override
    public Candidates save(Candidates candidates) {
        return candidatesRepository.save(candidates);
    }

    @Override
    public void deleteCandidate(Integer id ) {
        Candidates candidate= candidatesRepository.findById(id).orElseThrow(RuntimeException::new);
        candidate.setDeleted(Boolean.TRUE);
        List<Interview> interviewList=interviewRepository.findByCandidatesId(id);
        for (Interview it: interviewList){
            it.setDeleted(Boolean.TRUE);
        }
        List<Offer> offerList = offerRepository.findByCandidateId(id);
        for(Offer of: offerList){
            of.setDeleted(Boolean.TRUE);
        }
        candidatesRepository.save(candidate);
    }


    @Override
    public Boolean existsByPhone(String phoneNumber) {
        return candidatesRepository.existsByPhone(phoneNumber);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return candidatesRepository.existsByEmail(email);
    }

    @Override
    public Boolean existsByPhoneAndId(String phoneNumber, Integer id) {
        return candidatesRepository.existsByPhoneAndId(phoneNumber,id);
    }

    @Override
    public Boolean existsByEmailAndId(String email, Integer id) {
        return candidatesRepository.existsByEmailAndId(email,id);
    }

    @Override
    public List<Candidates> findAll() {
        return (List<Candidates>) candidatesRepository.findAll();
    }

    @Override
    public Candidates findByFullName(String fullName) {
        return candidatesRepository.findCandidatesByFullName(fullName);
    }
    @Override
    public List<Candidates> getCandidatesHaveStatusOtherThanBan() {
        return candidatesRepository.getCandidatesHaveStatusOtherThanBan();
    }

    @Override
    public void updateStatusCancelScheduleInterview(Integer id, CandidateStatus status) {
        candidatesRepository.updateStatusCancelScheduleInterview(id, status);
    }

    @Override
    public boolean existsByPhoneOrEmail(String phone, String email) {
        return candidatesRepository.existsByPhoneOrEmail(phone, email);
    }

    @Override
    public Candidates findCandidatesByOfferId(Integer id) {
        return candidatesRepository.findCandidatesByOfferId(id);
    }

    @Override
    public List<Candidates> getCandidatesHaveStatusPassedInterview() {
        return candidatesRepository.getCandidatesHaveStatusPassedInterview();
    }
}
