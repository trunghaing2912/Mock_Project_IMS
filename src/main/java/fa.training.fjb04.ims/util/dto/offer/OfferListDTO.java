package fa.training.fjb04.ims.util.dto.offer;

import fa.training.fjb04.ims.enums.offer.OfferStatus;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferListDTO {
    private Integer id;

    private String note;

    private OfferStatus offerStatus;


}
