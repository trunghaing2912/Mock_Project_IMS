package fa.training.fjb04.ims.util.validation.offer;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckDateFromToValidator.class)
public @interface CheckDateFromTo {

    String[] value();
    String message() default "From date must be earlier than To date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
