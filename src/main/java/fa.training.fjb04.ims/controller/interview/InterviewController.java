package fa.training.fjb04.ims.controller.interview;

import fa.training.fjb04.ims.config.security.SecurityUtils;
import fa.training.fjb04.ims.config.security.Users;
import fa.training.fjb04.ims.entity.candidates.CandidateStatus;
import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.entity.candidates.Recruiters;
import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.entity.interview.Interviewer;
import fa.training.fjb04.ims.entity.jobs.Jobs;
import fa.training.fjb04.ims.enums.interview.InterviewResult;
import fa.training.fjb04.ims.enums.interview.ScheduleStatus;
import fa.training.fjb04.ims.service.breadcrumb.BreadcrumbService;
import fa.training.fjb04.ims.service.candidates.CandidateStatusService;
import fa.training.fjb04.ims.service.candidates.CandidatesService;
import fa.training.fjb04.ims.service.candidates.RecruiterService;
import fa.training.fjb04.ims.service.interview.InterviewService;
import fa.training.fjb04.ims.service.interview.InterviewerService;
import fa.training.fjb04.ims.service.jobs.JobService;
import fa.training.fjb04.ims.service.user.UserService;
import fa.training.fjb04.ims.util.dto.interview.EditInterviewDTO;
import fa.training.fjb04.ims.util.dto.interview.InterviewDTO;
import fa.training.fjb04.ims.util.dto.interview.SubmitInterviewDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/interview")

public class InterviewController {
    private final InterviewService interviewService;
    private final InterviewerService interviewerService;
    private final CandidatesService candidatesService;
    private final JobService jobService;
    private final RecruiterService recruiterService;
    private final CandidateStatusService candidateStatusService;
    private final UserService userService;
    private final BreadcrumbService breadcrumbService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','INTERVIEWER','MANAGER')")
    public String showInterviewList(Model model) {

        List<Interviewer> interviewerList = interviewerService.findAll();
        Set<String> interviewerSet = new HashSet<>();
        for (Interviewer i : interviewerList) {
            interviewerSet.add(i.getInterviewerName());
        }
        model.addAttribute("interviewers", interviewerSet);

        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbInterviewList());

        return "interview/view-interview-schedule-list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    public String showAddNewInterview(Model model) {
        List<Interviewer> interviewerList = interviewerService.findAll();
        List<Candidates> candidatesList = candidatesService.findAll();
        List<Jobs> jobsList = jobService.findAll();
        List<Recruiters> recruitersList = recruiterService.findAll();

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        Integer recruiterId = recruiterService.findIdByUserName(currentUserLogin);
        model.addAttribute("userLoginId", recruiterId);

        model.addAttribute("interviewerList", interviewerList);
        model.addAttribute("candidatesList", candidatesList);
        model.addAttribute("jobsList", jobsList);
        model.addAttribute("recruitersList", recruitersList);
        model.addAttribute("newInterview", new InterviewDTO());

        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbInterviewList());
        model.addAttribute("breadCrumbAdd", breadcrumbService.getBreadcrumbInterviewAdd());

        return "interview/add-new-interview";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    @Transactional
    public String addNewInterview(@ModelAttribute("newInterview") @Valid InterviewDTO interviewDTO,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        List<Interviewer> interviewerList = interviewerService.findAll();
        List<Candidates> candidatesList = candidatesService.findAll();
        List<Jobs> jobsList = jobService.findAll();
        List<Recruiters> recruitersList = recruiterService.findAll();


        model.addAttribute("interviewerList", interviewerList);
        model.addAttribute("candidatesList", candidatesList);
        model.addAttribute("jobsList", jobsList);
        model.addAttribute("recruitersList", recruitersList);

        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbInterviewList());
        model.addAttribute("breadCrumbAdd", breadcrumbService.getBreadcrumbInterviewAdd());

        if (bindingResult.hasErrors()) {

            String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
            Integer recruiterId = recruiterService.findIdByUserName(currentUserLogin);
            model.addAttribute("userLoginId", recruiterId);

            model.addAttribute("createFailed", "Failed to created interview schedule");

            return "interview/add-new-interview";
        }
        Interview interview = new Interview();

        Candidates candidates = candidatesService.findByFullName(interviewDTO.getCandidates());

        String findStatus = "Waiting for interview";

        CandidateStatus candidateStatus = candidateStatusService.findByStatusName(findStatus);

        candidates.setCandidateStatus(candidateStatus);

        candidatesService.save(candidates);
        BeanUtils.copyProperties(interviewDTO, interview);

        Set<Interviewer> interviewerSet = new HashSet<>();
        Set<String> interviews = interviewDTO.getInterviewers();
        for (String inter : interviews) {
            Interviewer interviewerNew = interviewerService.findByInterviewerName(inter);
            interviewerSet.add(interviewerNew);
        }
        interview.setInterviewers(interviewerSet);


        interview.setCandidates(candidates);

        Jobs jobs = jobService.findByTitle(interviewDTO.getJobs());
        interview.setJobs(jobs);

        Recruiters recruiters = recruiterService.findById(Integer.valueOf(interviewDTO.getRecruiters()));

        interview.setResult(InterviewResult.NA);

        interview.setRecruiters(recruiters);

        interview.setStatus(ScheduleStatus.New);

        try {
            interviewService.save(interview);
        } catch (Exception e) {
            e.printStackTrace();
        }

        redirectAttributes.addFlashAttribute("createSuccessMessage", "Successfully created interview schedule");

        return "interview/view-interview-schedule-list";
    }

    @GetMapping("/view/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','INTERVIEWER','MANAGER')")
    public String showInterviewDetail(@PathVariable Integer id, Model model) {

        Interview interview = interviewService.findById(id).orElse(null);
        if (Objects.isNull(interview)) {
            return "error/404";
        }
        Set<String> interviewerSet = new HashSet<>();
        for (Interviewer i : interview.getInterviewers()) {
            interviewerSet.add(i.getInterviewerName());
        }

        model.addAttribute("interviewerSet", interviewerSet);
        model.addAttribute("interview", interview);
        model.addAttribute("now", LocalDate.now());

        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbInterviewList());
        model.addAttribute("breadCrumbDetails", breadcrumbService.getBreadcrumbInterviewDetail());

        return "interview/view-interview-details";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    public String showEditForm(@PathVariable Integer id, Model model,
                               @RequestParam(required = false, value = "prevPage") String prevPage) {

        Interview interview = interviewService.findById(id).orElse(null);

        if (Objects.isNull(interview)) {
            return "error/404";
        }

        EditInterviewDTO interviewDTO = new EditInterviewDTO();
        BeanUtils.copyProperties(interview, interviewDTO);

        List<Interviewer> interviewerList = interviewerService.findAll();
        List<Jobs> jobsList = jobService.getJobsHaveStatusOpen();
        List<Candidates> candidatesList = candidatesService.getCandidatesHaveStatusOtherThanBan();
        List<Recruiters> recruitersList = recruiterService.findAll();
        List<InterviewResult> resultList = List.of(InterviewResult.Passed, InterviewResult.Failed,InterviewResult.NA);

        Set<String> interviewerSet = new HashSet<>();
        for (Interviewer i : interview.getInterviewers()) {
            interviewerSet.add(i.getInterviewerName());
        }

        String role = SecurityUtils.getCurrentRole().toString();
        String username = SecurityUtils.getCurrentUserLogin().orElse("null");
        if (role.equalsIgnoreCase("[ROLE_RECRUITER]")) {
            model.addAttribute("role", role);
            String currentUserFullName = userService.findFullNameByUserName(username);
            Integer idCurrentUser = recruiterService.findByRecruiterName(currentUserFullName).getId();
            model.addAttribute("idCurrentUser", idCurrentUser);
        }

        ScheduleStatus status = interview.getStatus();
        if (!status.equals(ScheduleStatus.Cancelled)) {
            model.addAttribute("statusCancelled", status);
        }

        model.addAttribute("interviewerList", interviewerList);
        model.addAttribute("jobsList", jobsList);
        model.addAttribute("candidatesList", candidatesList);
        model.addAttribute("recruitersList", recruitersList);
        model.addAttribute("resultList", resultList);
        model.addAttribute("interviewerSet", interviewerSet);

        model.addAttribute("interview", interviewDTO);

        if (prevPage != null) {
            if (prevPage.equalsIgnoreCase("list")) {
                model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbInterviewList());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbInterviewEdit());
                model.addAttribute("prevPage", "list");
            } else {
                model.addAttribute("breadCrumbDetails", breadcrumbService.getBreadcrumbCandidateDetail());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbInterviewEdit());
                model.addAttribute("prevPage", "detail");
            }
        }

        return "interview/edit-interview-details";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    @Transactional
    public String editInterview(@PathVariable Integer id,
                                @ModelAttribute("interview") @Valid EditInterviewDTO editInterviewDTO,
                                BindingResult bindingResult,
                                @RequestParam("prevPage") String prevPage,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {

        Interview interview = interviewService.findById(id).orElse(null);

        if (interview == null) {
            return "error/404";
        }

        ScheduleStatus status1 = interview.getStatus();
        editInterviewDTO.setStatus(status1);


        Set<String> interviewerSet = new HashSet<>();
        for (Interviewer i : interview.getInterviewers()) {
            interviewerSet.add(i.getInterviewerName());
        }

        List<Interviewer> interviewerList = interviewerService.findAll();
        List<Jobs> jobsList = jobService.getJobsHaveStatusOpen();
        List<Candidates> candidatesList = candidatesService.getCandidatesHaveStatusOtherThanBan();
        List<Recruiters> recruitersList = recruiterService.findAll();
        List<InterviewResult> resultList = List.of(InterviewResult.Passed, InterviewResult.Failed,InterviewResult.NA);

        String role = SecurityUtils.getCurrentRole().toString();
        String username = SecurityUtils.getCurrentUserLogin().orElse("null");
        if (role.equalsIgnoreCase("[ROLE_RECRUITER]")) {
            model.addAttribute("role", role);
            String currentUserFullName = userService.findFullNameByUserName(username);
            Integer idCurrentUser = recruiterService.findByRecruiterName(currentUserFullName).getId();
            model.addAttribute("idCurrentUser", idCurrentUser);
        }

        ScheduleStatus status2 = interview.getStatus();
        if (!status2.equals(ScheduleStatus.Cancelled)) {
            model.addAttribute("statusCancelled", status2);
        }

        model.addAttribute("interviewerList", interviewerList);
        model.addAttribute("jobsList", jobsList);
        model.addAttribute("candidatesList", candidatesList);
        model.addAttribute("recruitersList", recruitersList);
        model.addAttribute("resultList", resultList);
        model.addAttribute("interviewerSet", interviewerSet);

        if (prevPage != null) {
            if (prevPage.equalsIgnoreCase("list")) {
                model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbInterviewList());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbInterviewEdit());
                model.addAttribute("prevPage", "list");
            } else {
                model.addAttribute("breadCrumbDetails", breadcrumbService.getBreadcrumbCandidateDetail());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbInterviewEdit());
                model.addAttribute("prevPage", "detail");
            }
        }

        if (bindingResult.hasErrors()) {

            return "interview/edit-interview-details";
        } else {
            ScheduleStatus status = interview.getStatus();

            String scheduleTitle = editInterviewDTO.getScheduleTitle();
            String candidateName = editInterviewDTO.getCandidates().getFullName();
            String job = editInterviewDTO.getJobs().getTitle();
            String recruiter = editInterviewDTO.getRecruiters().getRecruiterName();

            BeanUtils.copyProperties(editInterviewDTO, interview);
            Set<Interviewer> interviewerSetNew = new HashSet<>();
            Set<String> interviewerListString = editInterviewDTO.getInterviewers();
            for (String i : interviewerListString) {
                Interviewer interviewer = interviewerService.findByInterviewerName(i);
                if (interviewer != null) {
                    interviewerSetNew.add(interviewer);
                }
            }
            interview.setId(id);
            interview.setInterviewers(interviewerSetNew);
            interview.setStatus(status);
            interviewService.save(interview);
            return "redirect:/interview/list";
        }
    }


    @GetMapping("/cancelSchedule/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    @Transactional
    public String cancelSchedule(@PathVariable Integer id, Model model) {
        Interview interview = interviewService.findById(id).orElse(null);
        if (interview == null) {
            return "error/404";
        }
        Integer idCandidate = interview.getCandidates().getId();

        if (!interview.getStatus().equals(ScheduleStatus.Cancelled)) {
            Candidates candidates = candidatesService.findByCandidateId(idCandidate).orElse(null);
            if (candidates == null) {
                return "error/404";
            }
            CandidateStatus candidateStatus = candidateStatusService.findByCandidateStatusName("Cancelled Interview");
            candidates.setCandidateStatus(candidateStatus);

            interview.setStatus(ScheduleStatus.Cancelled);
            interviewService.save(interview);
//            interviewService.updateStatus(id);
        }
        return "redirect:/interview/edit/" + id;
    }

    @PostMapping("/submit/{id}")
    @PreAuthorize("hasRole('INTERVIEWER')")
    public String submitResult(@PathVariable Integer id,
                               @ModelAttribute("interviewSubmit") @Valid SubmitInterviewDTO submitInterviewDTO,
                               Model model) {
        Interview interview = interviewService.findById(id).orElse(null);
        if (interview == null) {
            return "error/404";
        }
        Integer idCandidate = interview.getCandidates().getId();
        Candidates candidates = candidatesService.findByCandidateId(idCandidate).orElse(null);
        BeanUtils.copyProperties(submitInterviewDTO, interview);
        if (candidates == null) {
            return "error/404";
        }
        Set<Interviewer> interviewers = interview.getInterviewers();
        Set<String> interviewerSetString = new HashSet<>();
        for (Interviewer intw: interviewers){
            interviewerSetString.add(intw.getInterviewerName());
        }
        String users = SecurityUtils.getCurrentUserLogin().orElse(null);
        if(!interviewerSetString.contains(users)){
            return "error/403";
        }
        if (interview.getResult().equals(InterviewResult.Passed)) {
            CandidateStatus candidateStatus = candidateStatusService.findByStatusName("Passed Interview");
            candidates.setCandidateStatus(candidateStatus);
            submitInterviewDTO.setResult(InterviewResult.Passed);

        } else if (interview.getResult().equals(InterviewResult.Failed)) {
            CandidateStatus candidateStatus = candidateStatusService.findByStatusName("Failed interview");
            candidates.setCandidateStatus(candidateStatus);
            submitInterviewDTO.setResult(InterviewResult.Failed);

        }
        submitInterviewDTO.setStatus(ScheduleStatus.Interviewed);
        BeanUtils.copyProperties(submitInterviewDTO, interview);
        candidatesService.save(candidates);

        return "redirect:/interview/view/" + id;

    }

    @GetMapping("/submit/{id}")
    @PreAuthorize("hasRole('INTERVIEWER')")
    public String showInterviewDetail2(@PathVariable Integer id,
                                       Model model) {
        String users = SecurityUtils.getCurrentUserLogin().orElse(null);
        Interview interview = interviewService.findById(id).orElse(null);
        if (Objects.isNull(interview)) {
            return "error/404";
        }
        Set<Interviewer> interviewers = interview.getInterviewers();
        Set<String> interviewerSetString = new HashSet<>();
        for (Interviewer intw: interviewers){
            interviewerSetString.add(intw.getInterviewerName());
        }
        if(!interviewerSetString.contains(users)){
            return "error/403";
        }
        Set<String> interviewerSet = new HashSet<>();
        for (Interviewer i : interview.getInterviewers()) {
            interviewerSet.add(i.getInterviewerName());
        }

        SubmitInterviewDTO submitInterviewDTO = new SubmitInterviewDTO();
        BeanUtils.copyProperties(interview, submitInterviewDTO);

        List<InterviewResult> resultList = List.of(InterviewResult.Passed, InterviewResult.Failed);

        model.addAttribute("interviewerSet", interviewerSet);

        model.addAttribute("now", LocalDate.now());
        model.addAttribute("resultList", resultList);
        model.addAttribute("interviewSubmit", submitInterviewDTO);

        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbInterviewList());
        model.addAttribute("breadCrumbSubmit", breadcrumbService.getBreadcrumbInterviewSubmit());

        return "interview/submit-interview-result";
    }

}


