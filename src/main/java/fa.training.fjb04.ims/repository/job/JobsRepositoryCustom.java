package fa.training.fjb04.ims.repository.job;

import fa.training.fjb04.ims.entity.jobs.Jobs;
import fa.training.fjb04.ims.util.dto.jobs.JobsListDTO;
import fa.training.fjb04.ims.util.dto.jobs.LevelListDTO;
import fa.training.fjb04.ims.util.dto.jobs.SkillListDTO;

import java.util.List;

public interface JobsRepositoryCustom {

    List<JobsListDTO> getAllJob(String search, String status);

    List<JobsListDTO> getJobPaging(Integer pageIndex, Integer pageSize, String search, String status);

    List<SkillListDTO> getSkillsById(Integer id);

    List<LevelListDTO> getLevelsById(Integer id);

    List<Jobs> getJobsHaveStatusOpen();
}
