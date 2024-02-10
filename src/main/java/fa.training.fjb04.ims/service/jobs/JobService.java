package fa.training.fjb04.ims.service.jobs;


import fa.training.fjb04.ims.entity.jobs.Jobs;
import fa.training.fjb04.ims.util.dto.jobs.JobsListDTO;
import fa.training.fjb04.ims.util.dto.jobs.LevelListDTO;
import fa.training.fjb04.ims.util.dto.jobs.SkillListDTO;
import fa.training.fjb04.ims.util.page.Page;
import fa.training.fjb04.ims.util.page.PageLevel;
import fa.training.fjb04.ims.util.page.PageSkill;

import java.util.List;
import java.util.Optional;

public interface JobService {

    Jobs save(Jobs job);
    void updateJobStatus();

    List<Jobs> findAll();

    Page<JobsListDTO> getPageJob(Integer pageIndex, Integer pageSize, String search, String status);

    PageSkill<SkillListDTO> getPageSkill(Integer id);

    PageLevel<LevelListDTO> getPageLevel(Integer id);

    Optional<Jobs> findByJobId(Integer id);

    void deleteJob(Integer jobId);

    void saveAllJob(List<Jobs> list);

    Jobs changeStatus(Integer id);

    Jobs findByTitle(String title);

    List<Jobs> getJobsHaveStatusOpen();
}
