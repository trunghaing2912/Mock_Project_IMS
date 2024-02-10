package fa.training.fjb04.ims.util.validation.candidate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinAgeValidator.class)
public @interface MinAge {
    int value() default 18;

    String message() default "Min age invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


