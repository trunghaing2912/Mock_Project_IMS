package fa.training.fjb04.ims.controller;

import fa.training.fjb04.ims.config.security.SecurityUtils;
import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes({"imgAvatar", "imgAvatarNull"})
@RequiredArgsConstructor
public class HomepageController {

    private final UserService userService;

    @GetMapping("/")
    public String getHomepage(HttpSession session, Model model) {
        String username = SecurityUtils.getCurrentUserLogin().orElse("null");
        User user = userService.findByUserName(username);

        if (user.getAvatar() != null) {
            model.addAttribute("imgAvatar", user.getAvatar());
            model.addAttribute("imgAvatarNull", "off");
        } else {
            model.addAttribute("imgAvatar", "off");
            model.addAttribute("imgAvatarNull", "on");
        }

        return "homepage";
    }
}
