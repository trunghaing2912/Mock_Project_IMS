package fa.training.fjb04.ims.util.validation.offer.editOffer;

import fa.training.fjb04.ims.util.dto.offer.EditOfferDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.time.LocalDate;

public class CheckDateFromToEditValidator implements ConstraintValidator<CheckDateFromToEdit, EditOfferDTO> {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    private String[] fields;


    @Override
    public void initialize(CheckDateFromToEdit constraintAnnotation) {
        fields = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(EditOfferDTO editOfferDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (editOfferDTO == null) {
            return true;
        }
        final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(editOfferDTO);
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
