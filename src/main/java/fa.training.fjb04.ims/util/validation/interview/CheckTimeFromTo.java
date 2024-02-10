package fa.training.fjb04.ims.util.validation.interview;

import fa.training.fjb04.ims.util.validation.editJob.CheckEndDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckTimeFromToValidator.class)
public @interface CheckTimeFromTo {

    String[] value();
    String message() default "From hour must be earlier than To hour";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
