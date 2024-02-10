package fa.training.fjb04.ims.repository.candidates;
import fa.training.fjb04.ims.entity.candidates.Recruiters;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface RecruiterRepository extends CrudRepository<Recruiters,Integer> {
    Recruiters findByRecruiterName(String recruiterName);

    @Query("select r.id from Recruiters r where r.recruiterUserName=:userName")
    Integer findIdByUserName(String userName);
}
