package fa.training.fjb04.ims.util.validation.addNewJob;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateOrderValidator.class)
@Documented
public @interface DateOrder {
    String[] value();
    String message() default "End date must be later than start date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}