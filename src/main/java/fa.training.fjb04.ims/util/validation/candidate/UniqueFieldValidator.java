package fa.training.fjb04.ims.util.validation.candidate;

import fa.training.fjb04.ims.service.candidates.CandidatesService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueFieldValidator implements ConstraintValidator<UniqueField, String> {

    private final CandidatesService candidateService;


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        } if (candidateService.existsByPhoneOrEmail(value, value)) {
            return false;
        } else {
            return true;
        }
    }

}