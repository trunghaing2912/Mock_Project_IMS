package fa.training.fjb04.ims.controller.offer;

import fa.training.fjb04.ims.config.security.SecurityUtils;
import fa.training.fjb04.ims.controller.login.Utility;
import fa.training.fjb04.ims.entity.candidates.CandidateStatus;
import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.entity.candidates.Positions;
import fa.training.fjb04.ims.entity.candidates.Recruiters;
import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.entity.interview.Interviewer;
import fa.training.fjb04.ims.entity.jobs.Levels;
import fa.training.fjb04.ims.entity.offer.Offer;
import fa.training.fjb04.ims.entity.user.Department;
import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.enums.interview.InterviewResult;
import fa.training.fjb04.ims.enums.offer.ContractType;
import fa.training.fjb04.ims.enums.offer.OfferStatus;
import fa.training.fjb04.ims.service.breadcrumb.BreadcrumbService;
import fa.training.fjb04.ims.service.candidates.CandidateStatusService;
import fa.training.fjb04.ims.service.candidates.CandidatesService;
import fa.training.fjb04.ims.service.candidates.PositionService;
import fa.training.fjb04.ims.service.candidates.RecruiterService;
import fa.training.fjb04.ims.service.interview.InterviewService;
import fa.training.fjb04.ims.service.interview.InterviewerService;
import fa.training.fjb04.ims.service.jobs.LevelService;
import fa.training.fjb04.ims.service.offer.OfferService;
import fa.training.fjb04.ims.service.user.DepartmentService;
import fa.training.fjb04.ims.service.user.UserService;
import fa.training.fjb04.ims.util.dto.offer.AddOfferDTO;
import fa.training.fjb04.ims.util.dto.offer.EditOfferDTO;
import fa.training.fjb04.ims.util.export.OfferExcelExporter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.BeanUtils;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/offer")
@RequiredArgsConstructor
public class OfferController {

    private final CandidatesService candidatesService;
    private final PositionService positionService;
    private final UserService userService;
    private final InterviewService interviewService;
    private final LevelService levelService;
    private final DepartmentService departmentService;
    private final RecruiterService recruiterService;
    private final OfferService offerService;
    private final CandidateStatusService candidateStatusService;
    private final JavaMailSender javaMailSender;
    private final BreadcrumbService breadcrumbService;


    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    public String showInterviewList(HttpServletRequest request, Model model) {

        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            inputFlashMap.get("updatedSuccessOffer");
        }
        List<OfferStatus> offerStatuses = List.of(
                OfferStatus.APPROVED_OFFER,
                OfferStatus.ACCEPTED_OFFER,
                OfferStatus.DECLINED_OFFER,
                OfferStatus.WAITING_FOR_APPROVAL,
                OfferStatus.WAITING_FOR_RESPONSE,
                OfferStatus.CANCELLED,
                OfferStatus.REJECTED_OFFER);
        List<Department> departmentList = departmentService.findAllDepartment();
        model.addAttribute("departmentList", departmentList);
        model.addAttribute("statusList", offerStatuses);

        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbOfferList());

        return "offer/view-offer-list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    public String showAddNewOffer(Model model) {

        List<Candidates> candidatesList = candidatesService.getCandidatesHaveStatusPassedInterview();
        List<Positions> positionsList = positionService.findAll();
        List<User> approverList = userService.getUsersIsManager();
        List<Interview> interviewPassList = interviewService.getInterviewsPass();
        if (candidatesList.isEmpty()) {
            interviewPassList = null;
        }
        List<ContractType> contractTypeList = List.of(ContractType.FULL_TIME,
                ContractType.PART_TIME,
                ContractType.CONTRACTOR,
                ContractType.INTERNSHIP,
                ContractType.TEMPORARY);
        List<Levels> levelsList = levelService.findAll();
        List<Department> departmentList = departmentService.findAllDepartment();
        List<Recruiters> recruitersList = recruiterService.findAll();

        String role = SecurityUtils.getCurrentRole().toString();
        String username = SecurityUtils.getCurrentUserLogin().orElse("null");
        if (role.equalsIgnoreCase("[ROLE_RECRUITER]")) {
            model.addAttribute("role", role);
            String currentUserFullName = userService.findFullNameByUserName(username);
            Integer idCurrentUser = recruiterService.findByRecruiterName(currentUserFullName).getId();
            model.addAttribute("idCurrentUser", idCurrentUser);
        }

        model.addAttribute("offer", new AddOfferDTO());
        model.addAttribute("candidatesList", candidatesList);
        model.addAttribute("positionsList", positionsList);
        model.addAttribute("approverList", approverList);
        model.addAttribute("interviewPassList", interviewPassList);
        model.addAttribute("contractTypeList", contractTypeList);
        model.addAttribute("levelsList", levelsList);
        model.addAttribute("departmentList", departmentList);
        model.addAttribute("recruitersList", recruitersList);

        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbOfferList());
        model.addAttribute("breadCrumbAdd", breadcrumbService.getBreadcrumbOfferAdd());

        return "offer/add-new-offer";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    @Transactional
    public String addNewOffer(@ModelAttribute("offer") @Valid AddOfferDTO addOfferDTO,
                              BindingResult bindingResult,
                              Model model) {

        List<Candidates> candidatesList = candidatesService.getCandidatesHaveStatusPassedInterview();
        List<Positions> positionsList = positionService.findAll();
        List<User> approverList = userService.getUsersIsManager();
        List<Interview> interviewPassList = interviewService.getInterviewsPass();
        if (candidatesList.isEmpty()) {
            interviewPassList = null;
        }
        List<ContractType> contractTypeList = List.of(ContractType.FULL_TIME,
                ContractType.PART_TIME,
                ContractType.CONTRACTOR,
                ContractType.INTERNSHIP,
                ContractType.TEMPORARY);
        List<Levels> levelsList = levelService.findAll();
        List<Department> departmentList = departmentService.findAllDepartment();
        List<Recruiters> recruitersList = recruiterService.findAll();

        String role = SecurityUtils.getCurrentRole().toString();
        String username = SecurityUtils.getCurrentUserLogin().orElse("null");
        if (role.equalsIgnoreCase("[ROLE_RECRUITER]")) {
            model.addAttribute("role", role);
            String currentUserFullName = userService.findFullNameByUserName(username);
            Integer idCurrentUser = recruiterService.findByRecruiterName(currentUserFullName).getId();
            model.addAttribute("idCurrentUser", idCurrentUser);
        }

        model.addAttribute("candidatesList", candidatesList);
        model.addAttribute("positionsList", positionsList);
        model.addAttribute("approverList", approverList);
        model.addAttribute("interviewPassList", interviewPassList);
        model.addAttribute("contractTypeList", contractTypeList);
        model.addAttribute("levelsList", levelsList);
        model.addAttribute("departmentList", departmentList);
        model.addAttribute("recruitersList", recruitersList);

        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbOfferList());
        model.addAttribute("breadCrumbAdd", breadcrumbService.getBreadcrumbOfferAdd());

        if (bindingResult.hasErrors()) {
            return "offer/add-new-offer";
        } else {
            Offer offer = new Offer();
            BeanUtils.copyProperties(addOfferDTO, offer);

            offer.setOfferStatus(OfferStatus.WAITING_FOR_APPROVAL);
            offerService.save(offer);

            CandidateStatus candidateStatus = candidateStatusService.findByCandidateStatusName("Waiting for approval");
            offer.getCandidate().setCandidateStatus(candidateStatus);

            return "redirect:/offer/list";
        }
    }

    @GetMapping("/view/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    public String showInterviewDetail(@PathVariable Integer id, Model model) {
        Offer findOffer = offerService.findById(id).orElse(null);
        if (Objects.isNull(findOffer)) {
            return "error/404";
        }
        model.addAttribute("getOffer", findOffer);

        String role = SecurityUtils.getCurrentRole().toString();

        String userLogin = SecurityUtils.getCurrentUserLogin().orElse(null);

        if (userLogin != null) {
            String userDepartment = userService.findByUserName(userLogin).getDepartment().getDepartmentName();

            if (role.equals("[ROLE_MANAGER]") || role.equals("[ROLE_ADMIN]")) {
                if (findOffer.getOfferStatus().equals(OfferStatus.WAITING_FOR_APPROVAL)) {
                    model.addAttribute("approveButton", "Approve");
                    model.addAttribute("rejectButton", "Reject");
                    model.addAttribute("editButton", "Edit");
                }
                if (findOffer.getOfferStatus().equals(OfferStatus.APPROVED_OFFER)) {
                    model.addAttribute("sendButton", "Mark as Sent to Candidate");
                }
                if (findOffer.getOfferStatus().equals(OfferStatus.WAITING_FOR_RESPONSE)) {
                    model.addAttribute("acceptButton", "Accept Offer");
                    model.addAttribute("declineButton", "Decline Offer");
                }
            }

            if ((role.equals("[ROLE_MANAGER]") && userDepartment.equalsIgnoreCase("HR")) || role.equals("[ROLE_ADMIN]") || role.equals("[ROLE_RECRUITER]")) {
                model.addAttribute("cancelButton", "Cancel Offer");
            }
        }
        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbOfferList());
        model.addAttribute("breadCrumbDetails", breadcrumbService.getBreadcrumbOfferDetail());

        return "offer/view-offer-details";
    }

    @GetMapping("/view/approve-offer/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Transactional
    public String approveOffer(@PathVariable Integer id, Model model) {
        Offer offer = offerService.findById(id).orElse(null);

        if (Objects.isNull(offer)) {
            return "error/404";
        }

        String role = SecurityUtils.getCurrentRole().toString();

        String userLogin = SecurityUtils.getCurrentUserLogin().orElse(null);

        if (userLogin != null) {
            String userDepartment = userService.findByUserName(userLogin).getDepartment().getDepartmentName();

            if ((role.equals("[ROLE_MANAGER]") && userDepartment.equalsIgnoreCase("HR")) || role.equals("[ROLE_ADMIN]")) {
                if (offer.getOfferStatus().equals(OfferStatus.WAITING_FOR_APPROVAL)) {
                    offer.setOfferStatus(OfferStatus.APPROVED_OFFER);
                    offerService.save(offer);
                    model.addAttribute("sendButton", "Mark as Sent to Candidate");
                }
            }

        }
        return "redirect:/offer/view/" + id;

    }

    @GetMapping("/view/reject-offer/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Transactional
    public String rejectOffer(@PathVariable Integer id) {
        Offer offer = offerService.findById(id).orElse(null);

        if (Objects.isNull(offer)) {
            return "error/404";
        }

        String role = SecurityUtils.getCurrentRole().toString();

        String userLogin = SecurityUtils.getCurrentUserLogin().orElse(null);

        if (userLogin != null) {
            String userDepartment = userService.findByUserName(userLogin).getDepartment().getDepartmentName();

            if ((role.equals("[ROLE_MANAGER]") && userDepartment.equalsIgnoreCase("HR")) || role.equals("[ROLE_ADMIN]")) {
                if (offer.getOfferStatus().equals(OfferStatus.WAITING_FOR_APPROVAL)) {
                    offer.setOfferStatus(OfferStatus.REJECTED_OFFER);
                    offerService.save(offer);
                }
            }
        }

        return "redirect:/offer/view/" + id;
    }

    @GetMapping("/view/cancel-offer/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'RECRUITER')")
    @Transactional
    public String cancelOffer(@PathVariable Integer id) {
        Offer offer = offerService.findById(id).orElse(null);

        if (Objects.isNull(offer)) {
            return "error/404";
        }

        offer.setOfferStatus(OfferStatus.CANCELLED);
        offerService.save(offer);

        return "redirect:/offer/view/" + id;
    }

    @GetMapping("/view/send-offer/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'RECRUITER')")
    @Transactional
    public String sendOfferToCandidate(@PathVariable Integer id, Model model) {
        Offer offer = offerService.findById(id).orElse(null);

        if (Objects.isNull(offer)) {
            return "error/404";
        }

        String role = SecurityUtils.getCurrentRole().toString();

        String userLogin = SecurityUtils.getCurrentUserLogin().orElse(null);

        if (userLogin != null) {
            String userDepartment = userService.findByUserName(userLogin).getDepartment().getDepartmentName();

            if ((role.equals("[ROLE_MANAGER]") && userDepartment.equalsIgnoreCase("HR")) || role.equals("[ROLE_ADMIN]") || role.equals("[ROLE_RECRUITER]")) {
                if (offer.getOfferStatus().equals(OfferStatus.APPROVED_OFFER)) {
                    offer.setOfferStatus(OfferStatus.WAITING_FOR_RESPONSE);
                    offerService.save(offer);
                }

            }
        }
        return "redirect:/offer/view/" + id;
    }

    @GetMapping("/view/accept-offer/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'RECRUITER')")
    @Transactional
    public String acceptOffer(@PathVariable Integer id, Model model) {
        Offer offer = offerService.findById(id).orElse(null);

        if (Objects.isNull(offer)) {
            return "error/404";
        }

        String role = SecurityUtils.getCurrentRole().toString();

        String userLogin = SecurityUtils.getCurrentUserLogin().orElse(null);

        if (userLogin != null) {
            String userDepartment = userService.findByUserName(userLogin).getDepartment().getDepartmentName();

            if ((role.equals("[ROLE_MANAGER]") && userDepartment.equalsIgnoreCase("HR")) || role.equals("[ROLE_ADMIN]") || role.equals("[ROLE_RECRUITER]")) {
                if (offer.getOfferStatus().equals(OfferStatus.WAITING_FOR_RESPONSE)) {
                    offer.setOfferStatus(OfferStatus.ACCEPTED_OFFER);
                    offerService.save(offer);
                }
            }
        }
        return "redirect:/offer/view/" + id;
    }

    @GetMapping("/view/decline-offer/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'RECRUITER')")
    @Transactional
    public String declineOffer(@PathVariable Integer id, Model model) {
        Offer offer = offerService.findById(id).orElse(null);

        if (Objects.isNull(offer)) {
            return "error/404";
        }

        String role = SecurityUtils.getCurrentRole().toString();

        String userLogin = SecurityUtils.getCurrentUserLogin().orElse(null);

        if (userLogin != null) {
            String userDepartment = userService.findByUserName(userLogin).getDepartment().getDepartmentName();

            if ((role.equals("[ROLE_MANAGER]") && userDepartment.equalsIgnoreCase("HR")) || role.equals("[ROLE_ADMIN]") || role.equals("[ROLE_RECRUITER]")) {

                if (offer.getOfferStatus().equals(OfferStatus.WAITING_FOR_RESPONSE)) {
                    offer.setOfferStatus(OfferStatus.DECLINED_OFFER);
                    offerService.save(offer);
                }
            }
        }
        return "redirect:/offer/view/" + id;
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    public String showEditForm(@PathVariable Integer id, Model model,
                               @RequestParam(required = false, value = "prevPage") String prevPage) {

        Offer editOffer = offerService.findById(id).orElse(null);

        if (Objects.isNull(editOffer)) {
            return "error/404";
        }

        if (!editOffer.getOfferStatus().equals(OfferStatus.WAITING_FOR_APPROVAL)) {
            model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbOfferList());
            model.addAttribute("breadCrumbDetails", breadcrumbService.getBreadcrumbOfferDetail());
            model.addAttribute("getOffer", editOffer);
            return "offer/view-offer-details";
        }

        EditOfferDTO editOfferDTO = new EditOfferDTO();
        BeanUtils.copyProperties(editOffer, editOfferDTO);

        Candidates editCandidate = candidatesService.findCandidatesByOfferId(id);
        List<Positions> positionsList = positionService.findAll();
        List<User> approverList = userService.getUsersIsManager();
        Integer candidateId = editCandidate.getId();
        List<Interview> interviewPassList = interviewService.findInterviewByCandidatesAndResult(candidateId, InterviewResult.Passed);
        List<ContractType> contractTypeList = List.of(ContractType.FULL_TIME,
                ContractType.PART_TIME,
                ContractType.CONTRACTOR,
                ContractType.INTERNSHIP,
                ContractType.TEMPORARY);
        List<Levels> levelsList = levelService.findAll();
        List<Department> departmentList = departmentService.findAllDepartment();
        List<Recruiters> recruitersList = recruiterService.findAll();

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        Integer recruiterId = recruiterService.findIdByUserName(currentUserLogin);
        model.addAttribute("userLoginId", recruiterId);

        model.addAttribute("editOffer", editOfferDTO);
        model.addAttribute("editCandidate", editCandidate);
        model.addAttribute("positionsList", positionsList);
        model.addAttribute("approverList", approverList);
        model.addAttribute("interviewPassList", interviewPassList);
        model.addAttribute("contractTypeList", contractTypeList);
        model.addAttribute("levelsList", levelsList);
        model.addAttribute("departmentList", departmentList);
        model.addAttribute("recruitersList", recruitersList);

        if (prevPage != null) {
            if (prevPage.equalsIgnoreCase("list")) {
                model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbOfferList());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbOfferEdit());
                model.addAttribute("prevPage", "list");
            } else {
                model.addAttribute("breadCrumbDetails", breadcrumbService.getBreadcrumbOfferDetail());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbOfferEdit());
                model.addAttribute("prevPage", "detail");
            }
        }

        return "offer/edit-offer-details";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    @Transactional
    public String editOffer(@PathVariable Integer id,
                            @ModelAttribute("editOffer") @Valid EditOfferDTO editOfferDTO,
                            BindingResult bindingResult,
                            @RequestParam(required = false, value = "prevPage") String prevPage,
                            Model model, RedirectAttributes redirectAttributes) {

        Offer existingOffer = offerService.findById(id).orElse(null);
        if (Objects.isNull(existingOffer)) {
            return "error/404";
        }
        existingOffer.setId(id);


        if (!editOfferDTO.getDueDate().equals(existingOffer.getDueDate()) && editOfferDTO.getDueDate().isBefore(LocalDate.now())) {
            bindingResult.rejectValue("dueDate", "ME033");
        }
        if (editOfferDTO.getNote().length() > 500) {
            bindingResult.rejectValue("note", "ME034");
        }

        if (bindingResult.hasErrors()) {
            Candidates editCandidate = candidatesService.findCandidatesByOfferId(id);
            List<Positions> positionsList = positionService.findAll();
            List<User> approverList = userService.getUsersIsManager();
            Integer candidateId = editCandidate.getId();
            List<Interview> interviewPassList = interviewService.findInterviewByCandidatesAndResult(candidateId, InterviewResult.Passed);
            List<ContractType> contractTypeList = List.of(
                    ContractType.FULL_TIME,
                    ContractType.PART_TIME,
                    ContractType.CONTRACTOR,
                    ContractType.INTERNSHIP,
                    ContractType.TEMPORARY);
            List<Levels> levelsList = levelService.findAll();
            List<Department> departmentList = departmentService.findAllDepartment();
            List<Recruiters> recruitersList = recruiterService.findAll();

            String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
            Integer recruiterId = recruiterService.findIdByUserName(currentUserLogin);
            model.addAttribute("userLoginId", recruiterId);

            if (prevPage != null) {
                if (prevPage.equalsIgnoreCase("list")) {
                    model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbOfferList());
                    model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbOfferEdit());
                    model.addAttribute("prevPage", "list");
                } else {
                    model.addAttribute("breadCrumbDetails", breadcrumbService.getBreadcrumbOfferDetail());
                    model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbOfferEdit());
                    model.addAttribute("prevPage", "detail");
                }
            }

            model.addAttribute("editCandidate", editCandidate);
            model.addAttribute("positionsList", positionsList);
            model.addAttribute("approverList", approverList);
            model.addAttribute("interviewPassList", interviewPassList);
            model.addAttribute("contractTypeList", contractTypeList);
            model.addAttribute("levelsList", levelsList);
            model.addAttribute("departmentList", departmentList);
            model.addAttribute("recruitersList", recruitersList);
            BeanUtils.copyProperties(existingOffer, editOfferDTO);
            model.addAttribute("editOffer", editOfferDTO);

            model.addAttribute("updateFailed", "Failed to update offer");
            return "offer/edit-offer-details";
        }
        BeanUtils.copyProperties(editOfferDTO, existingOffer);
        offerService.save(existingOffer);

        if (prevPage != null) {
            if (prevPage.equalsIgnoreCase("list")) {
                model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbOfferList());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbOfferEdit());
                model.addAttribute("prevPage", "list");
            } else {
                model.addAttribute("breadCrumbDetails", breadcrumbService.getBreadcrumbOfferDetail());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbOfferEdit());
                model.addAttribute("prevPage", "detail");
            }
        }

        String successMessage = "Offer has been updated successfully!";
        redirectAttributes.addFlashAttribute("updatedSuccessOffer", successMessage);
        return "redirect:/offer/list";
    }

    @PostMapping("/export-excel")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    public void exportToExcel(HttpServletResponse response,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) throws IOException {

        response.setContentType("application/octet-stream");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Offerlist-" + startDate + "-" + endDate + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Offer> offerList = offerService.findAllByDate(startDate, endDate);

        OfferExcelExporter offerExcelExporter = new OfferExcelExporter(offerList);

        offerExcelExporter.export(response);
    }

}
