package fa.training.fjb04.ims.util.validation.checkRepasswordUser;

import fa.training.fjb04.ims.util.dto.user.ChangePasswordDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class FieldMatchChangePasswordValidator implements ConstraintValidator<FieldMatchChangePassword, ChangePasswordDTO> {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    private String[] fields;


    @Override
    public void initialize(FieldMatchChangePassword constraintAnnotation) {
        fields = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(ChangePasswordDTO changePasswordDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (changePasswordDTO == null) {
            return true;
        }
        final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(changePasswordDTO);
        String newPassword = (String) beanWrapper.getPropertyValue(fields[0]);
        String rePassword = (String) beanWrapper.getPropertyValue(fields[1]);

        if (newPassword == null || rePassword == null || rePassword.equals(newPassword)) {
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

