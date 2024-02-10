package fa.training.fjb04.ims.api.interview;


import fa.training.fjb04.ims.entity.interview.Interviewer;
import fa.training.fjb04.ims.entity.jobs.Skills;
import fa.training.fjb04.ims.service.interview.InterviewService;
import fa.training.fjb04.ims.service.interview.InterviewerService;
import fa.training.fjb04.ims.util.dto.interview.*;
import fa.training.fjb04.ims.util.page.Page;
import fa.training.fjb04.ims.util.page.PageInterviewer;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class ApiInterviewController {

    private final InterviewService interviewService;


    @GetMapping
    public Page<InterviewListDTO> getInterviews(@RequestParam(defaultValue = "1", required = false, value = "pageIndex") Integer pageIndex,
                                                @RequestParam(defaultValue = "5", required = false, value = "pageSize") Integer pageSize,
                                                @RequestParam(required = false, value = "search") String search,
                                                @RequestParam(required = false, value = "interviewer") String interviewer,
                                                @RequestParam(required = false, value = "status") String status, Model model) {

        return interviewService.getPageInterview(pageIndex, pageSize, search, interviewer, status);
    }

    @GetMapping("/candidate/{id}")
    public InterviewCandidateDTO interviewCandidateDTO (@PathVariable Integer id) {
        return interviewService.getPageCandidate(id);
    }

    @GetMapping("/interviewer/{id}")
    public PageInterviewer<InterviewerListDTO> interviewInterviewerDTO (@PathVariable Integer id) {

        return interviewService.getPageInterviewer(id);
    }

    @GetMapping("/job/{id}")
    public InterviewJobDTO interviewJobDTO (@PathVariable Integer id) {
        return interviewService.getPageJob(id);
    }
}
