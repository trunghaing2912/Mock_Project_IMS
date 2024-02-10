package fa.training.fjb04.ims.controller.login;

import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.service.user.UserService;
import fa.training.fjb04.ims.util.dto.user.ResetPasswordDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Controller
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final JavaMailSender javaMailSender;

    private final UserService userService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "login/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = RandomString.make(30);

        try {
            userService.updateResetPasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset-password?token=" + token;
            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");
        } catch (UsernameNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Error while sending email");
        }
        return "login/forgot-password";
    }

    public void sendEmail(String recipientEmail, String resetPasswordLink) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("no-reply@imssystem.com", "FA Support");
        helper.setTo(recipientEmail);

        String subject = "Password Reset";

        String content =
                "<p>We have just received a password reset request for <user’s email address.</p>"
                        + "<p>Please click <a href=\"" + resetPasswordLink + "\">here</a> to reset your password</p>"
                        + "<p>For your security, the link will expire in 24 hours or immediately after you reset your password.</p>"
                        + "<p>Thanks & Regards!</p>"
                        + "<p>IMS Team.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        javaMailSender.send(message);
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        User user = userService.getByResetPasswordToken(token);

        if (user == null) {
            model.addAttribute("message", "The email address doesn’t exist. Please try again.");
            return "login/message";
        }

        model.addAttribute("token", token);
        model.addAttribute("resetPwDTO", new ResetPasswordDTO());

        if (user.getExpriedDate().isAfter(user.getExpriedDate())) {
            model.addAttribute("message", "This link has expired. Please go back to Homepage\n" +
                    "and try again.");
            return "login/message";
        }

        if (user.getIsResetSuccessfully().equalsIgnoreCase("SUCCESS")) {
            model.addAttribute("message", "Already changed account password. Please check.");
            user.setResetPasswordToken(null);
            return "login/message";
        }

        return "login/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                       @ModelAttribute("resetPwDTO") @Valid ResetPasswordDTO resetPasswordDTO,
                                       BindingResult bindingResult,
                                       Model model) {
        User user = userService.getByResetPasswordToken(token);

        model.addAttribute("title", "Reset your password");

        if (user == null) {
            model.addAttribute("message", "Invalid token");
        }

        if(!resetPasswordDTO.getConfirmPassword().equals(resetPasswordDTO.getNewPassword())) {
            bindingResult.rejectValue("confirmPassword", "ME006");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("token", token);
            return "login/reset-password";
        }

        userService.updatePassword(user, resetPasswordDTO.getNewPassword());
        model.addAttribute("message", "You have successfully changed your password");

        return "login/message";
    }

}
