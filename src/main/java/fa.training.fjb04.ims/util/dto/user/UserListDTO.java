package fa.training.fjb04.ims.util.dto.user;

import fa.training.fjb04.ims.entity.candidates.Positions;
import fa.training.fjb04.ims.entity.user.Roles;
import fa.training.fjb04.ims.enums.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserListDTO {

    private Integer id;

    private String username;

    private String email;

    private String phoneNumber;

    private Positions position;

    private Roles role;

    private UserStatus status;
}
