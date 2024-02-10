package fa.training.fjb04.ims.util.validation.checkUserPassword;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldMatchValidator.class)
@Documented
public @interface FieldMatch {
    String message() default "{ME006}}";

    Class<?>[] group() default {};

    Class<? extends Payload>[] payload() default {};

    String[] value();
}
