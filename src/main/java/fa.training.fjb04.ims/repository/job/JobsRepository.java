package fa.training.fjb04.ims.repository.job;


import fa.training.fjb04.ims.entity.jobs.Jobs;
import fa.training.fjb04.ims.enums.job.JobStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobsRepository extends CrudRepository<Jobs,Integer>, JobsRepositoryCustom {

    List<Jobs> findByStartDateAndStatus(LocalDate startDate, JobStatus status);

    List<Jobs> findByEndDateAndStatus(LocalDate endDate, JobStatus status);

    Jobs findByTitle(String titleName);



}
