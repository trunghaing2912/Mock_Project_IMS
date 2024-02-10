package fa.training.fjb04.ims.service.offer;

import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.entity.offer.Offer;
import fa.training.fjb04.ims.util.dto.offer.DepartmentListDTO;
import fa.training.fjb04.ims.util.dto.offer.OfferCandidateListDTO;
import fa.training.fjb04.ims.util.dto.offer.OfferListDTO;
import fa.training.fjb04.ims.util.dto.offer.OfferUserListDTO;
import fa.training.fjb04.ims.util.page.Page;
import fa.training.fjb04.ims.util.page.PageDepartment;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;

import java.util.Optional;

public interface OfferService {

    void save(Offer offer);

    List<Offer> findByDueDate(LocalDate dueDate);

    Optional<Offer> findById(Integer id);

    void sendEmail(String recipientEmail, Offer offer, String link) throws MessagingException, UnsupportedEncodingException;



    List<Offer> findAll();
    List<Offer> findAllByDate(LocalDate startDate, LocalDate endDate);

    Page<OfferListDTO> getPageOffer(Integer pageIndex, Integer pageSize, String search, String department, String status);

//    PageCandidate<OfferCandidateListDTO>getPageCandidate(Integer id);

    PageDepartment<DepartmentListDTO> getPageDepartment(Integer id);

    OfferUserListDTO getPageUser(Integer id);

    OfferCandidateListDTO getPageCandidate(Integer id);

    Offer findByOfferToken(String token);

}
