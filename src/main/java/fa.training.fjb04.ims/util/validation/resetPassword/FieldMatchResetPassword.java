package fa.training.fjb04.ims.util.validation.resetPassword;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldMatchResetPasswordValidator.class)
public @interface FieldMatchResetPassword {
    String message() default "{ME006}}";
    Class<?>[] group() default {};

    Class<? extends Payload>[] payload() default {};

    String[] value();
}
