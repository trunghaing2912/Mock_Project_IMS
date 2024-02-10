package fa.training.fjb04.ims.util.validation.interview;

import fa.training.fjb04.ims.util.dto.interview.EditInterviewDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.time.LocalTime;

public class CheckTimeFromToValidator implements ConstraintValidator<CheckTimeFromTo, EditInterviewDTO> {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    private String[] fields;
    @Override
    public void initialize(CheckTimeFromTo constraintAnnotation) {
        fields = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(EditInterviewDTO editInterviewDTO, ConstraintValidatorContext constraintValidatorContext) {

        if (editInterviewDTO == null) {
            return true;
        }
        final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(editInterviewDTO);
        LocalTime startTime = (LocalTime) beanWrapper.getPropertyValue(fields[0]);
        LocalTime endTime = (LocalTime) beanWrapper.getPropertyValue(fields[1]);

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
