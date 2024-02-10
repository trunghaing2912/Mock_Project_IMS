package fa.training.fjb04.ims.api.candidates;

import fa.training.fjb04.ims.service.candidates.CandidatesService;
import fa.training.fjb04.ims.service.candidates.impl.CandidatesServiceImpl;
import fa.training.fjb04.ims.util.dto.candidates.CandidatePositionDTO;
import fa.training.fjb04.ims.util.dto.candidates.CandidateRecruiterDTO;
import fa.training.fjb04.ims.util.dto.candidates.CandidatesListDTO;
import fa.training.fjb04.ims.util.page.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/candidates")
public class ApiCandidateController {

    private final CandidatesService candidatesService;

    @GetMapping
    public Page<CandidatesListDTO> getEmployees(@RequestParam(defaultValue = "1", required = false, value = "pageIndex") Integer pageIndex,
                                                @RequestParam(defaultValue = "5", required = false, value = "pageSize") Integer pageSize,
                                                @RequestParam(required = false, value = "search") String search,
                                                @RequestParam(required = false, value = "field") String field) {

        return candidatesService.getPageCandidates(pageIndex, pageSize, search, field);
    }

    @GetMapping("/position/{id}")
    public CandidatePositionDTO candidatePositionDTO(@PathVariable Integer id){

        return candidatesService.getPagePosition(id);
    }

    @GetMapping("/recruiter/{id}")
    public CandidateRecruiterDTO candidateRecruiterDTO(@PathVariable Integer id){

        return candidatesService.getPageRecruiter(id);
    }

}
