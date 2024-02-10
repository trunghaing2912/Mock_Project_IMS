package fa.training.fjb04.ims.util.validation.addNewJob;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxSalaryValidator.class)
@Documented
public @interface MaxSalary {
    String[] value();
    String message() default "Max salary must be greater than min salary";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
