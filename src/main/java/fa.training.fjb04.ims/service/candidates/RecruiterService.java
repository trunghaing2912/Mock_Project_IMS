package fa.training.fjb04.ims.service.candidates;

import fa.training.fjb04.ims.entity.candidates.Recruiters;

import java.util.List;

public interface RecruiterService {
    List<Recruiters> findAll();
    Recruiters findByRecruiterName(String recruiterName);
    Recruiters save(Recruiters recruiters);
    Integer findIdByUserName(String userName);
    Recruiters findById(Integer id);

}
