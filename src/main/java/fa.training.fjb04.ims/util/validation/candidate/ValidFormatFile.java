package fa.training.fjb04.ims.util.validation.candidate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidFormatFileValidator.class)
public @interface ValidFormatFile{

    String message() default "Invalid file format. Allowed formats: pdf, doc, docx";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}