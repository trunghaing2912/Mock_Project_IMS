package fa.training.fjb04.ims.controller.candidate;

import fa.training.fjb04.ims.config.security.SecurityUtils;
import fa.training.fjb04.ims.entity.candidates.*;
import fa.training.fjb04.ims.entity.jobs.Skills;
import fa.training.fjb04.ims.enums.Gender;
import fa.training.fjb04.ims.service.breadcrumb.BreadcrumbService;
import fa.training.fjb04.ims.service.candidates.*;
import fa.training.fjb04.ims.service.file.FileStorageService;
import fa.training.fjb04.ims.service.file.impl.JavaFileToMultipartFile;
import fa.training.fjb04.ims.service.jobs.SkillService;
import fa.training.fjb04.ims.service.user.UserService;
import fa.training.fjb04.ims.util.dto.candidates.CandidateDTO;
import fa.training.fjb04.ims.util.dto.candidates.EditCandidateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/candidate")
public class CandidateController {

    private final FileStorageService fileStorageService;
    private final CandidatesService candidateService;
    private final CandidateStatusService candidateStatusService;
    private final HighestLevelService highestLevelService;
    private final PositionService positionService;
    private final RecruiterService recruiterService;
    private final SkillService skillService;
    private final UserService userService;
    private final BreadcrumbService breadcrumbService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','INTERVIEWER','MANAGER')")
    public String getCandidateList(Model model) {

        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbCandidateList());

        return "candidate/view-candidate-list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    public String showFormAdd(@ModelAttribute("newCandidate") @Valid CandidateDTO candidateDTO,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {


        List<HighestLevel> highestLevelList = highestLevelService.findAll();
        List<Positions> positionsList = positionService.findAll();
        List<Recruiters> recruitersList = recruiterService.findAll();
        List<Skills> skillsList = skillService.findAll();

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        Integer recruiterId = recruiterService.findIdByUserName(currentUserLogin);
        model.addAttribute("userLoginId", recruiterId);


        model.addAttribute("newCandidate", new CandidateDTO());
        model.addAttribute("skillsList", skillsList);
        model.addAttribute("genders", Gender.values());
        model.addAttribute("highestLevelList", highestLevelList);
        model.addAttribute("positionsList", positionsList);
        model.addAttribute("recruitersList", recruitersList);

        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbCandidateList());
        model.addAttribute("breadCrumbAdd", breadcrumbService.getBreadcrumbCandidateAdd());

        return "candidate/add-new-candidate";
    }


    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    @Transactional
    public String addNewCandidate(@RequestParam("cvAttachment") MultipartFile file, @ModelAttribute("newCandidate") @Valid CandidateDTO candidateDTO,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) throws IOException {

        List<CandidateStatus> candidateStatusList = candidateStatusService.findAll();
        List<HighestLevel> highestLevelList = highestLevelService.findAll();
        List<Positions> positionsList = positionService.findAll();
        List<Skills> skillsList = skillService.findAll();
        List<Recruiters> recruitersList = recruiterService.findAll();

        model.addAttribute("skillsList", skillsList);
        model.addAttribute("genders", Gender.values());
        model.addAttribute("candidateStatusList", candidateStatusList);
        model.addAttribute("highestLevelList", highestLevelList);
        model.addAttribute("positionsList", positionsList);
        model.addAttribute("recruitersList", recruitersList);

        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbCandidateList());
        model.addAttribute("breadCrumbAdd", breadcrumbService.getBreadcrumbCandidateAdd());


        if (file.isEmpty()) {
            bindingResult.rejectValue("cvAttachment", "ME002");
        }

        if (candidateDTO.getEmail() != null && !candidateDTO.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            bindingResult.rejectValue("email", "ME009");
        }

        if (bindingResult.hasErrors()) {

            String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
            Integer recruiterId = recruiterService.findIdByUserName(currentUserLogin);
            model.addAttribute("userLoginId", recruiterId);

            model.addAttribute("createFailed", "Failed to create candidate");
            return "candidate/add-new-candidate";
        }

        Candidates candidate = new Candidates();
        BeanUtils.copyProperties(candidateDTO, candidate);

        Set<Skills> skillsSet = new HashSet<>();
        Set<String> skills = candidateDTO.getSkillsSet();

        for (String skill : skills) {
            Skills skills1 = skillService.findBySkillName(skill);
            skillsSet.add(skills1);
        }
        candidate.setSkillsSet(skillsSet);

        CandidateStatus candidateStatus = candidateStatusService.findByStatusName(candidateDTO.getCandidateStatus().getStatusName());
        candidate.setCandidateStatus(candidateStatus);

        HighestLevel highestLevel = highestLevelService.findByHighestLevelName(candidateDTO.getHighestLevel().getHighestLevelName());
        candidate.setHighestLevel(highestLevel);

        Positions position = positionService.findByPositionName(candidateDTO.getPosition().getPositionName());
        candidate.setPosition(position);


        Recruiters recruiter = recruiterService.findByRecruiterName(candidateDTO.getRecruiter().getRecruiterName());
        candidate.setRecruiter(recruiter);

        candidate.setCvAttachment(fileStorageService.saveFile(candidateDTO.getCvAttachment(),"candidate"));
        candidateService.save(candidate);

        redirectAttributes.addFlashAttribute("createSuccessMessage", "Candidate has been created successfully!");
        return "redirect:/candidate/list";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    public String showFormEdit(@PathVariable Integer id, Model model,
                               @RequestParam(required = false, value = "prevPage") String prevPage) {
        Candidates candidate = candidateService.findByCandidateId(id).orElse(null);
        if (Objects.isNull(candidate)) {
            List<Skills> skillsList = skillService.findAll();
            Set<String> skillsSet = new HashSet<>();
            for (Skills skills : candidate.getSkillsSet()) {
                skillsSet.add(skills.getSkillName());

            }
            model.addAttribute("skillsSets", skillsSet);
            return "error/404";
        }
        EditCandidateDTO candidateDTO = new EditCandidateDTO();
        BeanUtils.copyProperties(candidate, candidateDTO);


        List<Skills> skillsList = skillService.findAll();
        Set<String> skillsSet = new HashSet<>();
        for (Skills skills : candidate.getSkillsSet()) {
            skillsSet.add(skills.getSkillName());

        }

        List<Positions> positionsList = positionService.findAll();

        List<CandidateStatus> candidateStatusList = candidateStatusService.findAll();
        List<HighestLevel> highestLevelList = highestLevelService.findAll();
        List<Recruiters> recruitersList = recruiterService.findAll();
        List<Gender> genderList = List.of(Gender.FEMALE, Gender.MALE);

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        Integer recruiterId = recruiterService.findIdByUserName(currentUserLogin);
        model.addAttribute("userLoginId", recruiterId);

        String fileName = candidate.getCvAttachment().substring(10);


        model.addAttribute("skillsSets", skillsSet);
        model.addAttribute("positionList", positionsList);
        model.addAttribute("candidateStatusList", candidateStatusList);
        model.addAttribute("highestLevelList", highestLevelList);
        model.addAttribute("recuitersList", recruitersList);
        model.addAttribute("cvAttachment", fileName);
        model.addAttribute("candidate", candidateDTO);
        model.addAttribute("skillsList", skillsList);
        model.addAttribute("genderList", genderList);

        if (prevPage != null) {
            if (prevPage.equalsIgnoreCase("list")) {
                model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbCandidateList());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbCandidateEdit());
                model.addAttribute("prevPage", "list");
            } else {
                model.addAttribute("breadCrumbDetails", breadcrumbService.getBreadcrumbCandidateDetail());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbCandidateEdit());
                model.addAttribute("prevPage", "detail");
            }
        }
        return "candidate/edit-candidate-details";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    @Transactional
    public String editCandidate(@PathVariable Integer id,
                                @RequestParam("cvAttachment") MultipartFile file,
                                @ModelAttribute("candidate") @Valid EditCandidateDTO editCandidateDTO,
                                BindingResult bindingResult,
                                @RequestParam(required = false, value = "prevPage") String prevPage,
                                Model model,
                                RedirectAttributes redirectAttributes) throws IOException {

        List<Positions> positionsList = positionService.findAll();
        List<CandidateStatus> candidateStatusList = candidateStatusService.findAll();
        List<HighestLevel> highestLevelList = highestLevelService.findAll();
        List<Recruiters> recruitersList = recruiterService.findAll();
        List<Gender> genderList = List.of(Gender.FEMALE, Gender.MALE);
        List<Skills> skillsList = skillService.findAll();

        model.addAttribute("positionList", positionsList);
        model.addAttribute("candidateStatusList", candidateStatusList);
        model.addAttribute("highestLevelList", highestLevelList);
        model.addAttribute("recuitersList", recruitersList);
        model.addAttribute("skillsList", skillsList);
        model.addAttribute("genderList", genderList);

        Candidates exitingCandidate = candidateService.findByCandidateId(id).orElse(null);
        editCandidateDTO.setId(id);
        if (exitingCandidate == null) {
            return "error/404";
        }

        if (candidateService.existsByPhone(editCandidateDTO.getPhone()) && !candidateService.existsByPhoneAndId(editCandidateDTO.getPhone(), editCandidateDTO.getId())) {
            bindingResult.rejectValue("phone", "", "Phone number has exist");
        }

        if (candidateService.existsByEmail(editCandidateDTO.getEmail()) && !candidateService.existsByEmailAndId(editCandidateDTO.getEmail(), editCandidateDTO.getId())) {
            bindingResult.rejectValue("email", "", "Email number has exist");
        }
        String fileName = exitingCandidate.getCvAttachment();
        File existedFiles = new File(fileStorageService.getFileLocation() + "/" + fileName);
        MultipartFile fileAttach = new JavaFileToMultipartFile(existedFiles);

        if (file.isEmpty()) {
            editCandidateDTO.setCvAttachment( fileAttach);
        }

        if (bindingResult.hasErrors()) {

            Candidates existingCandidate = candidateService.findByCandidateId(id).orElse(null);
            Set<String> skillsSet = new HashSet<>();
            for (Skills skills : existingCandidate.getSkillsSet()) {
                skillsSet.add(skills.getSkillName());
            }
            String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
            Integer recruiterId = recruiterService.findIdByUserName(currentUserLogin);
            model.addAttribute("userLoginId", recruiterId);
            model.addAttribute("skillsSets", skillsSet);

            model.addAttribute("createFailed", "Failed to update candidate");
            return "candidate/edit-candidate-details";


        }
        Set<String> skillsSet = new HashSet<>();
        for (Skills skills : exitingCandidate.getSkillsSet()) {
            skillsSet.add(skills.getSkillName());

        }

        BeanUtils.copyProperties(editCandidateDTO, exitingCandidate);
        exitingCandidate.setCvAttachment(fileStorageService.saveFile(editCandidateDTO.getCvAttachment(), "candidate"));
        Set<Skills> skillsSetNew = new HashSet<>();
        Set<String> skills = editCandidateDTO.getSkillsSet();
        for (String skill : skills) {
            Skills skills1 = skillService.findBySkillName(skill);
            skillsSetNew.add(skills1);
        }

        exitingCandidate.setId(id);
        exitingCandidate.setSkillsSet(skillsSetNew);


        model.addAttribute("skillSets", skillsSet);
        candidateService.save(exitingCandidate);

        if (prevPage != null) {
            if (prevPage.equalsIgnoreCase("list")) {
                model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbCandidateList());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbCandidateEdit());
                model.addAttribute("prevPage", "list");
            } else {
                model.addAttribute("breadCrumbDetails", breadcrumbService.getBreadcrumbCandidateDetail());
                model.addAttribute("breadCrumbEdit", breadcrumbService.getBreadcrumbCandidateEdit());
                model.addAttribute("prevPage", "detail");
            }
        }

        return "redirect:/candidate/list";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    public String deleteCandidate(@PathVariable Integer id,RedirectAttributes redirectAttributes){
        try{
            candidateService.deleteCandidate(id);

            redirectAttributes.addFlashAttribute("deleteSuccessMessage", "Candidate has been deleted successfully!");
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("deleteErrorMessage", "Failed to delete candidate");
        }
        return "redirect:/candidate/list";

    }

    @GetMapping("/view/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','INTERVIEWER','MANAGER')")
    public String viewUserDetail(@PathVariable Integer id, Model model) {
        Candidates candidate = candidateService.findByCandidateId(id).orElse(null);
        if (Objects.isNull(candidate)) {
            return "error/404";
        }

        Set<String> skillSet = new HashSet<>();
        for (Skills skill : candidate.getSkillsSet()) {
            skillSet.add(skill.getSkillName());
        }

        String role = SecurityUtils.getCurrentRole().toString();

        if (!candidate.getCandidateStatus().getStatusName().equals("Banned") && !role.equalsIgnoreCase("[ROLE_INTERVIEWER]")) {
            model.addAttribute("banned", "Ban Candidate");
        }
        String fileName = candidate.getCvAttachment().substring(10);

        model.addAttribute("fileName",fileName);
        model.addAttribute("candidate", candidate);
        model.addAttribute("skills", skillSet);

        model.addAttribute("breadCrumbList", breadcrumbService.getBreadcrumbCandidateList());
        model.addAttribute("breadCrumbDetails", breadcrumbService.getBreadcrumbCandidateDetail());

        return "candidate/view-candidate-details";
    }

    @GetMapping("/ban/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    @Transactional
    public String banCandidate(@PathVariable Integer id) {

        Candidates candidate = candidateService.findByCandidateId(id).orElse(null);

        if (Objects.isNull(candidate)) {
            return "error/404";
        }

        CandidateStatus candidateStatus = candidateStatusService.findByStatusName("Banned");
        candidate.setCandidateStatus(candidateStatus);

        candidateService.save(candidate);
        return "redirect:/candidate/view/" + id;
    }
}

