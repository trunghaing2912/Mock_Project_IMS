package fa.training.fjb04.ims.controller.job;

import fa.training.fjb04.ims.config.security.SecurityUtils;
import fa.training.fjb04.ims.entity.jobs.Benefit;
import fa.training.fjb04.ims.entity.jobs.Jobs;
import fa.training.fjb04.ims.entity.jobs.Levels;
import fa.training.fjb04.ims.entity.jobs.Skills;
import fa.training.fjb04.ims.enums.job.JobStatus;
import fa.training.fjb04.ims.service.breadcrumb.BreadcrumbService;
import fa.training.fjb04.ims.service.jobs.BenefitService;
import fa.training.fjb04.ims.service.jobs.JobService;
import fa.training.fjb04.ims.service.jobs.LevelService;
import fa.training.fjb04.ims.service.jobs.SkillService;
import fa.training.fjb04.ims.util.dto.jobs.EditJobDTO;
import fa.training.fjb04.ims.util.dto.jobs.JobImportDTO;
import fa.training.fjb04.ims.util.dto.jobs.JobsDTO;
import fa.training.fjb04.ims.util.imports.ImportFile;
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

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/job")
public class JobController {

    private final LevelService levelService;
    private final BenefitService benefitService;
    private final SkillService skillService;
    private final JobService jobService;
    private final BreadcrumbService breadcrumbService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','INTERVIEWER','MANAGER')")
    public String getJobList(Model model) {
        model.addAttribute("breadcrumbList", breadcrumbService.getBreadcrumbJobList());
        return "job/view-job-list";
    }


    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    public String showFormAdd(Model model){

        List<Levels> levelsList = levelService.findAll();
        List<Skills> skillsList = skillService.findAll();
        List<Benefit> benefitList = benefitService.findAll();

        model.addAttribute("newJob", new JobsDTO());
        model.addAttribute("levelList", levelsList);
        model.addAttribute("skillsList", skillsList);
        model.addAttribute("benefitList", benefitList);

        model.addAttribute("breadcrumbList", breadcrumbService.getBreadcrumbJobList());
        model.addAttribute("breadcrumbAdd", breadcrumbService.getBreadcrumbJobAdd());

        return "job/add-new-job";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    public String addNewJob(@ModelAttribute("newJob") @Valid JobsDTO jobsDTO,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes){

        List<Levels> levelsList = levelService.findAll();
        List<Skills> skillsList = skillService.findAll();
        List<Benefit> benefitList = benefitService.findAll();

        model.addAttribute("levelList", levelsList);
        model.addAttribute("skillsList", skillsList);
        model.addAttribute("benefitList", benefitList);

        model.addAttribute("breadcrumbList", breadcrumbService.getBreadcrumbJobList());
        model.addAttribute("breadcrumbAdd", breadcrumbService.getBreadcrumbJobAdd());

        if (bindingResult.hasErrors()){

            model.addAttribute("createFailed","Failed to created job");
            return "job/add-new-job";
        }

        Jobs job = new Jobs();
        BeanUtils.copyProperties(jobsDTO,job);


        Set<Skills> skillsSet = new HashSet<>();
        Set<String> skills = jobsDTO.getSkillsSet();

        for (String skill: skills){

            Skills skills1= skillService.findBySkillName(skill);
            skillsSet.add(skills1);
        }
        job.setSkillsSet(skillsSet);

        Set<Levels> levelsSet = new HashSet<>();
        Set<String> levels = jobsDTO.getLevelsSet();

        for (String level: levels){
            Levels levels1 = levelService.findByLevelName(level);
            levelsSet.add(levels1);
        }
        job.setLevelsSet(levelsSet);

        Set<Benefit> benefitSet = new HashSet<>();
        Set<String> benefits = jobsDTO.getBenefitSet();

        for (String benefit: benefits){
            Benefit benefit1 = benefitService.findByBenefitName(benefit);
            benefitSet.add(benefit1);
        }
        job.setBenefitSet(benefitSet);

        LocalDate currentTime = LocalDate.now();

        if(jobsDTO.getStartDate().equals(currentTime)){
            job.setStatus(JobStatus.OPEN);
        }else if(jobsDTO.getStartDate().isAfter(currentTime)){
            job.setStatus(JobStatus.DRAFT);
        }

        jobService.save(job);

        redirectAttributes.addFlashAttribute("createSuccessMessage", "Successfully created job");
        return "redirect:/job/list";
    }

    @GetMapping("/view/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','INTERVIEWER','MANAGER')")
    public String viewJobDetail(@PathVariable Integer id, Model model){
        Jobs jobs=jobService.findByJobId(id).orElse(null);
        if(Objects.isNull(jobs)){
            return "error/404";
        }
        Set<String> skillSet=new HashSet<>();
        for (Skills skills:jobs.getSkillsSet()) {
            skillSet.add(skills.getSkillName());
        }
        Set<String>benefitSet=new HashSet<>();
        for (Benefit benefit:jobs.getBenefitSet()){
            benefitSet.add(benefit.getBenefitName());
        }

        Set<String>levelSet=new HashSet<>();
        for(Levels levels:jobs.getLevelsSet()){
            levelSet.add(levels.getLevelName());
        }

        model.addAttribute("skillName",skillSet);
        model.addAttribute("benefitName",benefitSet);
        model.addAttribute("levelName",levelSet);
        model.addAttribute("view",jobs);

        model.addAttribute("breadcrumbList", breadcrumbService.getBreadcrumbJobList());
        model.addAttribute("breadcrumbDetail", breadcrumbService.getBreadcrumbJobDetail());

        return "job/view-job-details";

    }
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    public String showFormEdit(@PathVariable Integer id, Model model,
                               @RequestParam(required = false, value = "prevPage") String prevPage) {

        Jobs job = jobService.findByJobId(id).orElse(null);
        if (Objects.isNull(job)) {
            return "error/404";
        }

        EditJobDTO jobsDTO = new EditJobDTO();

        BeanUtils.copyProperties(job, jobsDTO);

        List<Levels> levelsList = levelService.findAll();
        List<Skills> skillsList = skillService.findAll();
        List<Benefit> benefitList = benefitService.findAll();

        Set<String> benefitSet = job.getBenefitSet().stream()
                .map(Benefit::getBenefitName)
                .collect(Collectors.toSet());

        Set<String> skillsSet = job.getSkillsSet().stream()
                .map(Skills::getSkillName)
                .collect(Collectors.toSet());

        Set<String> levelsSet = job.getLevelsSet().stream()
                .map(Levels::getLevelName)
                .collect(Collectors.toSet());

        model.addAttribute("levelSets", levelsSet);
        model.addAttribute("skillsSets", skillsSet);
        model.addAttribute("benefitSets", benefitSet);
        model.addAttribute("job", jobsDTO);
        model.addAttribute("levelList", levelsList);
        model.addAttribute("skillsList", skillsList);
        model.addAttribute("benefitList", benefitList);

        if (prevPage != null) {
            if (prevPage.equalsIgnoreCase("list")) {
                model.addAttribute("breadcrumbList", breadcrumbService.getBreadcrumbJobList());
                model.addAttribute("breadcrumbEdit", breadcrumbService.getBreadcrumbJobEdit());
                model.addAttribute("prevPage", "list");
            } else {
                model.addAttribute("breadcrumbDetail", breadcrumbService.getBreadcrumbJobDetail());
                model.addAttribute("breadcrumbEdit", breadcrumbService.getBreadcrumbJobEdit());
                model.addAttribute("prevPage", "detail");
            }
        }

        return "job/edit-job-details";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    @Transactional
    public String editJob(@PathVariable Integer id,
                          @ModelAttribute("job") @Valid EditJobDTO editJobDTO,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes,
                          @RequestParam(required = false) String prevPage) {

        List<Levels> levelsList = levelService.findAll();
        List<Skills> skillsList = skillService.findAll();
        List<Benefit> benefitList = benefitService.findAll();

        model.addAttribute("levelList", levelsList);
        model.addAttribute("skillsList", skillsList);
        model.addAttribute("benefitList", benefitList);

        if (prevPage != null) {
            if (prevPage.equalsIgnoreCase("list")) {
                model.addAttribute("breadcrumbList", breadcrumbService.getBreadcrumbJobList());
                model.addAttribute("breadcrumbEdit", breadcrumbService.getBreadcrumbJobEdit());
                model.addAttribute("prevPage", "list");
            } else {
                model.addAttribute("breadcrumbDetail", breadcrumbService.getBreadcrumbJobDetail());
                model.addAttribute("breadcrumbEdit", breadcrumbService.getBreadcrumbJobEdit());
                model.addAttribute("prevPage", "detail");
            }
        }

        Jobs existingJob = jobService.findByJobId(id).orElse(null);

        if (existingJob == null) {
            return "error/404";
        }

        if (bindingResult.hasErrors()) {

            Jobs editJobs = jobService.findByJobId(id).orElse(null);
            Set<String> editLevels = editJobs.getLevelsSet().stream()
                    .map(Levels::getLevelName).collect(Collectors.toSet());

            Set<String> editSkill = editJobs.getSkillsSet().stream()
                    .map(Skills::getSkillName).collect(Collectors.toSet());
            Set<String> editBenefit = editJobs.getBenefitSet().stream()
                    .map(Benefit::getBenefitName).collect(Collectors.toSet());

            model.addAttribute("levelSets", editLevels);
            model.addAttribute("skillsSets", editSkill);
            model.addAttribute("benefitSets", editBenefit);

            LocalDate currentStartDate = existingJob.getStartDate();
            LocalDate newStartDate = editJobDTO.getStartDate();
            LocalDate currentDate = LocalDate.now();

            if(currentStartDate.isBefore(currentDate)){
                if(newStartDate.equals(currentStartDate) || newStartDate.isAfter(currentStartDate)||newStartDate.equals(currentDate)){
                    existingJob.setStartDate(newStartDate);
                }else {
                    model.addAttribute("invalidStartDate", "Invalid start date");
                    return "job/edit-job-details";
                }
            }else {
                if(newStartDate.isBefore(currentDate)){
                    model.addAttribute("invalidStartDate", "Invalid start date");
                    return "job/edit-job-details";
                }else{
                    existingJob.setStartDate(newStartDate);
                }

            }

            model.addAttribute("createFailed","Failed to updated change");
            return "job/edit-job-details";
        }



        BeanUtils.copyProperties(editJobDTO, existingJob);

        Set<String> levels = editJobDTO.getLevelsSet();
        Set<String> skills = editJobDTO.getSkillsSet();
        Set<String> benefits = editJobDTO.getBenefitSet();

        Set<Levels> levelSet = levels.stream()
                .map(levelService::findByLevelName)
                .collect(Collectors.toSet());

        Set<Skills> skillsSet = skills.stream()
                .map(skillService::findBySkillName)
                .collect(Collectors.toSet());

        Set<Benefit> benefitSet = benefits.stream()
                .map(benefitService::findByBenefitName)
                .collect(Collectors.toSet());

        existingJob.setSkillsSet(skillsSet);
        existingJob.setLevelsSet(levelSet);
        existingJob.setBenefitSet(benefitSet);

        jobService.changeStatus(existingJob.getId());

        redirectAttributes.addFlashAttribute("editSuccessMessage", "Change has been successfully updated");
        return "redirect:/job/list";

    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    public String deleteJob(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            jobService.findByJobId(id).orElse(null);
            jobService.deleteJob(id);

            redirectAttributes.addFlashAttribute("deleteSuccessMessage", "Successfully delete job");
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("deleteErrorMessage", "Failed to delete job");
        }

        return "redirect:/job/list";
    }


    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER','MANAGER')")
    @Transactional
    public String importNewJob(@RequestParam("file") MultipartFile file, Model model,
                               RedirectAttributes redirectAttributes) {
        if (!file.isEmpty()) {
            try {

                List<JobImportDTO> jobImportDTOList = ImportFile.processExelFile(file);
                List<Jobs> jobsList = new ArrayList<>();
                for (JobImportDTO jobImportDTO : jobImportDTOList) {
                    Jobs job = new Jobs();
                    BeanUtils.copyProperties(jobImportDTO, job);

                    Set<Skills> skillsSet = new HashSet<>();
                    String skills = jobImportDTO.getSkillsSet();
                    String[] arrSkills = skills.split(",");

                    for (String arrSkill : arrSkills) {
                        String valueSkill = arrSkill.trim();

                        Skills skill = skillService.findBySkillName(valueSkill);
                        skillsSet.add(skill);
                    }
                    job.setSkillsSet(skillsSet);

                    Set<Benefit> benefitSet = new HashSet<>();
                    String benefits = jobImportDTO.getBenefitSet();
                    String[] arrBenefits = benefits.split(",");

                    for (String arrBenefit : arrBenefits) {
                        String valueBenefit = arrBenefit.trim();

                        Benefit benefit = benefitService.findByBenefitName(valueBenefit);
                        benefitSet.add(benefit);
                    }
                    job.setBenefitSet(benefitSet);

                    Set<Levels> levelsSet = new HashSet<>();
                    String levels = jobImportDTO.getLevelsSet();
                    String[] arrLevels = levels.split(",");

                    for (String arrLevel : arrLevels) {
                        String valueLevel = arrLevel.trim();

                        Levels level = levelService.findByLevelName(valueLevel);
                        levelsSet.add(level);
                    }
                    job.setLevelsSet(levelsSet);

                    job.setCreatedBy(SecurityUtils.getCurrentUserLogin().orElse("System"));

                    LocalDate currentTime = LocalDate.now();

                    if(jobImportDTO.getStartDate().equals(currentTime)){
                        job.setStatus(JobStatus.OPEN);
                    }else if(jobImportDTO.getStartDate().isAfter(currentTime)){
                        job.setStatus(JobStatus.DRAFT);
                    }

                    jobsList.add(job);

                }

                jobService.saveAllJob(jobsList);

                redirectAttributes.addFlashAttribute("createSuccessMessage", "Successfully created job");
                return "redirect:/job/list";

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/job/list";
    }

}

