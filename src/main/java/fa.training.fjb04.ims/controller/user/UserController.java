package fa.training.fjb04.ims.controller.user;

import fa.training.fjb04.ims.config.security.SecurityUtils;

import fa.training.fjb04.ims.entity.candidates.Positions;
import fa.training.fjb04.ims.entity.candidates.Recruiters;
import fa.training.fjb04.ims.entity.interview.Interviewer;
import fa.training.fjb04.ims.entity.user.Department;
import fa.training.fjb04.ims.entity.user.Roles;
import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.enums.user.UserStatus;
import fa.training.fjb04.ims.service.breadcrumb.BreadcrumbService;
import fa.training.fjb04.ims.service.candidates.PositionService;
import fa.training.fjb04.ims.service.candidates.RecruiterService;
import fa.training.fjb04.ims.service.interview.InterviewerService;
import fa.training.fjb04.ims.service.user.DepartmentService;
import fa.training.fjb04.ims.service.user.RolesService;
import fa.training.fjb04.ims.service.user.UserService;
import fa.training.fjb04.ims.util.dto.user.AddUserDTO;
import fa.training.fjb04.ims.util.dto.user.ChangePasswordDTO;
import fa.training.fjb04.ims.util.dto.user.EditUserDTO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RolesService rolesService;
    private final DepartmentService departmentService;
    private final MessageSource messageSource;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final RecruiterService recruiterService;
    private final InterviewerService interviewerService;
    private final BreadcrumbService breadcrumbService;
    private final PositionService positionService;

    public static final String ALLOWED_SPL_CHARACTERS = "!@#$%^&*()_+";

    public static final String ERROR_CODE = "ERRONEOUS_SPECIAL_CHARS";

    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{7,}$";

    @GetMapping("/list")
    public String getUserList(Model model) {
        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbUserList());

        return "user/view-user-list";
    }

    @GetMapping("/add")
    public String showFormAdd(Model model) {

        List<Roles> rolesList = rolesService.findAllRoles();
        List<Department> departmentList = departmentService.findAllDepartment();
        List<Positions> positionsList = positionService.findAll();

        model.addAttribute("addNewUser", new AddUserDTO());
        model.addAttribute("addRolesList", rolesList);
        model.addAttribute("addDepartmentList", departmentList);
        model.addAttribute("addPositionList", positionsList);

        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbUserList());
        model.addAttribute("breadCrumbAdd", breadcrumbService.getBreadcrumbUserAdd());

        return "user/add-new-user";
    }

    @PostMapping("/add")
    @Transactional
    public String postFormAdd(@ModelAttribute("addNewUser") @Valid AddUserDTO addUserDTO,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) throws MessagingException, UnsupportedEncodingException {

        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbUserList());
        model.addAttribute("breadCrumbAdd", breadcrumbService.getBreadcrumbUserAdd());

        if (addUserDTO.getPhoneNumber() != null && userService.existByPhoneNumber(addUserDTO.getPhoneNumber())) {
            bindingResult.rejectValue("phoneNumber", "ME031");
        }

        if (userService.existByEmail(addUserDTO.getEmail())) {
            bindingResult.rejectValue("email", "ME032");
        }

        if (bindingResult.hasErrors()) {

            Set<String> roles = addUserDTO.getRolesSet();
            Set<Roles> rolesSet = new HashSet<>();

            for (String r : roles) {
                Roles findRole = rolesService.findByRoleName(r);
                rolesSet.add(findRole);
            }

            List<Roles> rolesList = rolesService.findAllRoles();
            List<Department> departmentList = departmentService.findAllDepartment();
            List<Positions> positionsList = positionService.findAll();

            model.addAttribute("addRolesList", rolesList);
            model.addAttribute("addDepartmentList", departmentList);
            model.addAttribute("addPositionList", positionsList);
            model.addAttribute("createFailed", "Failed to create user");

            return "user/add-new-user";
        }

        User newUser = new User();
        BeanUtils.copyProperties(addUserDTO, newUser);

        Set<String> roles = addUserDTO.getRolesSet();
        Set<Roles> rolesSet = new HashSet<>();

        for (String r : roles) {
            Roles findRole = rolesService.findByRoleName(r);
            rolesSet.add(findRole);
        }

        //generate User Name
        String username = createFormattedName(addUserDTO.getFullName());

        int suffix = 1;
        while (userService.existByUserName(username + suffix)) {
            suffix++;
        }

        String newUserName = (username + suffix).toLowerCase();
        newUser.setUsername(newUserName);

        //generate new Password
        String rawPassword = generatePassword();
        newUser.setNote("Raw Password: " + rawPassword);

        String newPassword = passwordEncoder.encode(rawPassword);
        newUser.setPassword(newPassword);

        newUser.setRoles(rolesSet);
        newUser.setCreatedBy(SecurityUtils.getCurrentUserLogin().orElse("System"));

        for (Roles r : newUser.getRoles()) {
            if (r.getRoleName().equalsIgnoreCase("RECRUITER") || r.getRoleName().equalsIgnoreCase("INTERVIEWER")) {
                String name = newUser.getFullName();
                String userName = newUser.getUsername();
                String email = newUser.getEmail();
                String createBy = SecurityUtils.getCurrentUserLogin().orElse("system");

                if (r.getRoleName().equalsIgnoreCase("RECRUITER")) {
                    Recruiters newRecruiter = new Recruiters();
                    newRecruiter.setRecruiterName(name);
                    newRecruiter.setCreatedBy(createBy);
                    newRecruiter.setRecruiterUserName(userName);
                    recruiterService.save(newRecruiter);
                } else {
                    Interviewer interviewer = new Interviewer();
                    interviewer.setInterviewerName(userName);
                    interviewer.setEmail(email);
                    interviewer.setCreatedBy(createBy);
                    interviewerService.save(interviewer);
                }

            }
        }


        userService.save(newUser);

        sendNewUserEmail(newUserName, rawPassword, newUser.getEmail());

        return "redirect:/user/list";
    }

    @GetMapping("/edit/{id}")
    public String showFormEdit(@PathVariable Integer id, Model model,
                               @RequestParam(required = false, value = "prevPage") String prevPage) {

        User user = userService.findByUserId(id).orElse(null);

        if (Objects.isNull(user)) {
            return "error/404";
        }

        EditUserDTO editUserDTO = new EditUserDTO();
        BeanUtils.copyProperties(user, editUserDTO);

        List<Roles> rolesList = rolesService.findAllRoles();
        Set<String> roleSet = new HashSet<>();
        for (Roles r : rolesList) {
            roleSet.add(r.getRoleName());
        }

        List<Department> departmentList = departmentService.findAllDepartment();
        List<Positions> positionsList = positionService.findAll();

        String roleName = "";
        for (Roles r : user.getRoles()) {
            roleName = r.getRoleName();
        }

        model.addAttribute("roleName", roleName);
        model.addAttribute("getEditUser", editUserDTO);
        model.addAttribute("getEditRolesSet", roleSet);
        model.addAttribute("getEditRolesList", rolesList);
        model.addAttribute("getEditDepartmentList", departmentList);
        model.addAttribute("getPositionList", positionsList);

        if (prevPage != null) {
            if (prevPage.equalsIgnoreCase("list")) {
                model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbUserList());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbUserEdit());
                model.addAttribute("prevPage", "list");
            } else {
                model.addAttribute("breadCrumbDetail", breadcrumbService.getBreadcrumbUserDetail());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbUserEdit());
                model.addAttribute("prevPage", "detail");
            }
        }

        return "user/edit-user-details";
    }

    @PostMapping("/edit/{id}")
    @Transactional
    public String postFormEdit(@PathVariable Integer id,
                               @ModelAttribute("getEditUser") @Valid EditUserDTO editUserDTO,
                               BindingResult bindingResult,
                               @RequestParam("prevPage") String prevPage,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        User existingUser = userService.findByUserId(id).orElse(null);

        if (existingUser == null) {
            return "error/404";
        }

        if (prevPage != null) {
            if (prevPage.equalsIgnoreCase("list")) {
                model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbUserList());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbUserEdit());
                model.addAttribute("prevPage", "list");
            } else {
                model.addAttribute("breadCrumbDetail", breadcrumbService.getBreadcrumbUserDetail());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbUserEdit());
                model.addAttribute("prevPage", "detail");
            }
        }

        if (userService.existByEmail(editUserDTO.getEmail()) && !userService.existByIdAndEmail(existingUser.getId(), existingUser.getEmail())) {
            bindingResult.rejectValue("email", "ME032");
        }

        if (userService.existByPhoneNumber(editUserDTO.getPhoneNumber()) && !userService.existByIdAndPhoneNumber(existingUser.getId(), existingUser.getPhoneNumber())) {
            bindingResult.rejectValue("phoneNumber", "ME031");
        }

        if (bindingResult.hasErrors()) {

            BeanUtils.copyProperties(existingUser, editUserDTO);

            List<Roles> rolesList = rolesService.findAllRoles();
            Set<String> roleSet = new HashSet<>();
            for (Roles r : rolesList) {
                roleSet.add(r.getRoleName());
            }

            List<Department> departmentList = departmentService.findAllDepartment();
            List<Positions> positionsList = positionService.findAll();

            String roleName = "";
            for (Roles r : existingUser.getRoles()) {
                roleName = r.getRoleName();
            }

            model.addAttribute("roleName", roleName);
            model.addAttribute("getEditUser", editUserDTO);
            model.addAttribute("getEditRolesSet", roleSet);
            model.addAttribute("getEditRolesList", rolesList);
            model.addAttribute("getEditDepartmentList", departmentList);
            model.addAttribute("getPositionList", positionsList);

            model.addAttribute("createFailed", "Failed to update user");
            return "user/edit-user-details";
        }

        Set<String> roles = editUserDTO.getRolesSet();
        Set<Roles> rolesSet = new HashSet<>();

        for (String r : roles) {
            Roles findRole = rolesService.findByRoleName(r);
            rolesSet.add(findRole);
        }


        BeanUtils.copyProperties(editUserDTO, existingUser);
        existingUser.setLastModifiedBy(SecurityUtils.getCurrentUserLogin().orElse("System"));
        existingUser.setRoles(rolesSet);

        userService.save(existingUser);

        List<Roles> rolesList = rolesService.findAllRoles();
        Set<String> roleSet = new HashSet<>();
        for (Roles r : rolesList) {
            roleSet.add(r.getRoleName());
        }

        List<Department> departmentList = departmentService.findAllDepartment();
        List<Positions> positionsList = positionService.findAll();

        String roleName = "";
        for (Roles r : existingUser.getRoles()) {
            roleName = r.getRoleName();
        }

        model.addAttribute("roleName", roleName);
        model.addAttribute("getEditUser", editUserDTO);
        model.addAttribute("getEditRolesSet", roleSet);
        model.addAttribute("getEditRolesList", rolesList);
        model.addAttribute("getEditDepartmentList", departmentList);
        model.addAttribute("getPositionList", positionsList);

        redirectAttributes.addFlashAttribute("editSuccessMessage", "User has been updated!");
        return "redirect:/user/list";
    }

    @GetMapping("/view/{id}")
    public String viewUserDetail(@PathVariable Integer id, Model model) {
        User findUser = userService.findByUserId(id).orElse(null);

        if (Objects.isNull(findUser)) {
            return "error/404";
        }

        Set<String> roleSet = new HashSet<>();
        for (Roles r : findUser.getRoles()) {
            roleSet.add(r.getRoleName());
        }

        if (findUser.getStatus().equals(UserStatus.ACTIVE)) {
            model.addAttribute("active", "ACTIVE USER");
        }
        if (findUser.getStatus().equals(UserStatus.INACTIVE)) {
            model.addAttribute("inactive", "INACTIVE USER");
        }

        model.addAttribute("userDetail", findUser);
        model.addAttribute("userDetailsRole", roleSet);

        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbUserList());
        model.addAttribute("breadCrumbDetail", breadcrumbService.getBreadcrumbUserDetail());

        return "user/view-user-details";
    }

    @GetMapping("/delete/{id}")
    public String deleteJob(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("deleteErrorMessage", "Failed to delete user");
        }

        redirectAttributes.addFlashAttribute("deleteSuccessMessage", "Deleted user successfully!");
        return "redirect:/user/list";
    }

    @GetMapping("/deactivate/{id}")
    public String postDeactivateUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        User user = userService.findByUserId(id).orElse(null);

        if (Objects.isNull(user)) {
            return "error/404";
        }

        user.setStatus(UserStatus.INACTIVE);
        userService.save(user);

        return "redirect:/user/view/" + id;
    }

    @GetMapping("/activate/{id}")
    public String postActivateUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        User user = userService.findByUserId(id).orElse(null);

        if (Objects.isNull(user)) {
            return "error/404";
        }

        user.setStatus(UserStatus.ACTIVE);
        userService.save(user);

        return "redirect:/user/view/" + id;
    }


    @GetMapping("/change-password")
    public String showResetPasswordForm(Model model) {
        User user = userService.findByUserName(SecurityUtils.getCurrentUserLogin().orElse(null));

        if (Objects.isNull(user)) {
            return "error/404";
        }

        model.addAttribute("changePassword", new ChangePasswordDTO());

        return "login/change-password";
    }

    @PostMapping("/change-password")
    public String processResetPassword(@ModelAttribute(value = "changePassword") @Valid ChangePasswordDTO changePasswordDTO,
                                       BindingResult bindingResult,
                                       Model model, RedirectAttributes redirectAttributes) {

        User user = userService.findByUserName(SecurityUtils.getCurrentUserLogin().orElse(null));

        if (Objects.isNull(user)) {
            return "error/404";
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            model.addAttribute("checkOldPassword", "Old password incorrect");
            return "login/change-password";
        }
        if (bindingResult.hasErrors()) {
            return "login/change-password";
        }

        if (changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            userService.updatePassword(user, changePasswordDTO.getNewPassword());
            return "redirect:/avatar";
        }

        bindingResult.rejectValue("matchPassword", "ME006");
        return "login/change-password";
    }

    private static String generatePassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(7);
        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);
        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);
        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return ERROR_CODE;
            }

            public String getCharacters() {
                return ALLOWED_SPL_CHARACTERS;
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);
        return gen.generatePassword(15, splCharRule, lowerCaseRule, upperCaseRule, digitRule);
    }

    public void sendNewUserEmail(String username, String rawPassword, String email) throws
            MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("no-reply@imssystem.com", "no-reply-mail-IMS-system");
        helper.setTo(email);

        String subject = "Account created";

        String content =
                "<p>Your account has been created. Please use the following credential to\n" +
                        "login: </p>"
                        + "<p>- User name: \"" + username + "\"</p>"
                        + "<p>- Password: \"" + rawPassword + "\"</p>"
                        + "<p>If anything wrong, please reach-out recruiter. We are so sorry for this inconvenience.</p>"
                        + "<p>Thanks & Regards!\n</p>"
                        + "<p>IMS Team.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        javaMailSender.send(message);
    }

    private static String createFormattedName(String fullName) {
        String temp = Normalizer.normalize(fullName, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        fullName = pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
        String[] words = fullName.trim().split("\\s+");
        String lastWord = words[words.length - 1];
        String username = lastWord.substring(0, 1).toUpperCase() +
                lastWord.substring(1).toLowerCase();
        String[] newArray = Arrays.copyOf(words, words.length - 1);

        return username + newString(newArray);
    }

    private static String newString(String[] array) {
        StringBuilder result = new StringBuilder();
        for (String s : array) {
            result.append(Character.toUpperCase(s.charAt(0)));
        }
        return result.toString().trim();
    }


}
