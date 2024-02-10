package fa.training.fjb04.ims.repository.offer;

import fa.training.fjb04.ims.util.dto.offer.DepartmentListDTO;
import fa.training.fjb04.ims.util.dto.offer.OfferCandidateListDTO;
import fa.training.fjb04.ims.util.dto.offer.OfferListDTO;
import fa.training.fjb04.ims.util.dto.offer.OfferUserListDTO;

import java.util.List;

public interface OfferRepositoryCustom {

    List<OfferListDTO> getAllOffer(String search,String department,String status);
    List<OfferListDTO> getOfferPage(Integer pageIndex,Integer pageSize,String search,String department,String status);

    OfferCandidateListDTO getCandidateById(Integer id);

    List<DepartmentListDTO>getDepartmentById(Integer id);

   OfferUserListDTO getUserById(Integer id);
}
