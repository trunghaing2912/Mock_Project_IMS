package fa.training.fjb04.ims.util.validation.resetPassword;

import fa.training.fjb04.ims.util.dto.user.ResetPasswordDTO;
import fa.training.fjb04.ims.util.dto.user.UserDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.time.LocalDate;

public class FieldMatchResetPasswordValidator implements ConstraintValidator<FieldMatchResetPassword, ResetPasswordDTO> {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    private String[] fields;

    @Override
    public void initialize(FieldMatchResetPassword constraintAnnotation) {
        fields = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(ResetPasswordDTO userDTO, ConstraintValidatorContext context) {
        if (userDTO == null) {
            return true;
        }
        final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(userDTO);

        String newPassword = (String) beanWrapper.getPropertyValue(fields[0]);
        String confirmPassword = (String) beanWrapper.getPropertyValue(fields[1]);

        if( newPassword == null || confirmPassword == null || confirmPassword.equals(newPassword) ){
            return true;
        }else{
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(fields[fields.length-1])
                    .addConstraintViolation();
            return false;
        }
    }

}

