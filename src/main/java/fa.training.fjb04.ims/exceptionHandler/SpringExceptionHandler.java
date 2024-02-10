package fa.training.fjb04.ims.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class SpringExceptionHandler  {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handlerAccessDeniedException() {
        return "error/403";
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handlerErrorException() {
        return "error/500";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlerNotFoundException() {
        return "error/404";
    }

//    @ExceptionHandler(UsernameNotFoundException.class)
//    public String handlerInactiveException(Model model) {
//        model.addAttribute("inactive", "User was disabled. Please try again");
//        return "login/message";
//    }

}
