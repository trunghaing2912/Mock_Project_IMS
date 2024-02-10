package fa.training.fjb04.ims.util.validation.candidate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class MinAgeValidator implements ConstraintValidator<MinAge, LocalDate> {

    int value;
    @Override
    public void initialize(MinAge constraintAnnotation) {
        value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext constraintValidatorContext) {
        if (dob == null) {
            return true;
        }
        LocalDate minDateOfBirth = dob.plusYears(value);
        return !minDateOfBirth.isAfter(LocalDate.now());
    }
}

