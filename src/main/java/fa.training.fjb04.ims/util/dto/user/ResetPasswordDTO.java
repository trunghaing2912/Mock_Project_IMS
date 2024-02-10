package fa.training.fjb04.ims.util.dto.user;

import fa.training.fjb04.ims.util.validation.checkRepasswordUser.FieldMatchChangePassword;
import fa.training.fjb04.ims.util.validation.checkUserPassword.FieldMatch;
import fa.training.fjb04.ims.util.validation.resetPassword.FieldMatchResetPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

//@FieldMatchResetPassword({"newPassword", "confirmPassword"})
public class ResetPasswordDTO {

    @NotBlank(message = "Can not find user")
    private String token;

    @NotBlank(message = "New Password can not be blank")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "{ME007}")
    private String newPassword;

    @NotBlank(message = "Re-Password can not be blank")
    private String confirmPassword;

}
