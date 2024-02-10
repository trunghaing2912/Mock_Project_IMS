package fa.training.fjb04.ims.util.validation.offer;

import fa.training.fjb04.ims.util.dto.offer.AddOfferDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.time.LocalDate;

public class CheckDateFromToValidator implements ConstraintValidator<CheckDateFromTo, AddOfferDTO> {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    private String[] fields;


    @Override
    public void initialize(CheckDateFromTo constraintAnnotation) {
        fields = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(AddOfferDTO addOfferDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (addOfferDTO == null) {
            return true;
        }
        final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(addOfferDTO);
        LocalDate startTime = (LocalDate) beanWrapper.getPropertyValue(fields[0]);
        LocalDate endTime = (LocalDate) beanWrapper.getPropertyValue(fields[1]);

        if (startTime == null || endTime == null || endTime.isAfter(startTime)) {
            return true;
        } else {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                            constraintValidatorContext.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(fields[fields.length-1])
                    .addConstraintViolation();
            return false;
        }
    }
}
