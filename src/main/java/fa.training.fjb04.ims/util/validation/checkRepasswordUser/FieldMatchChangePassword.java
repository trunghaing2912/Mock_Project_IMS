package fa.training.fjb04.ims.util.validation.checkRepasswordUser;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldMatchChangePasswordValidator.class)
public @interface FieldMatchChangePassword {
    String[] value();
    String message() default "{ME006}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
