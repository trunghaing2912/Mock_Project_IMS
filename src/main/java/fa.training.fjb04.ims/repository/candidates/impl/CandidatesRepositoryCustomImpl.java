package fa.training.fjb04.ims.repository.candidates.impl;

import fa.training.fjb04.ims.entity.candidates.CandidateStatus;
import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.entity.candidates.Positions;
import fa.training.fjb04.ims.entity.candidates.Recruiters;
import fa.training.fjb04.ims.repository.candidates.CandidatesRepositoryCustom;
import fa.training.fjb04.ims.util.dto.candidates.CandidatePositionDTO;
import fa.training.fjb04.ims.util.dto.candidates.CandidateRecruiterDTO;
import fa.training.fjb04.ims.util.dto.candidates.CandidatesListDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

public class CandidatesRepositoryCustomImpl implements CandidatesRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    public List<CandidatesListDTO> getCandidatesPaging(Integer pageIndex, Integer pageSize, String search, String status) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CandidatesListDTO> query = builder.createQuery(CandidatesListDTO.class);
        Root<Candidates> root = query.from(Candidates.class);
        Join<Candidates, Positions> candidatesPositionsJoin = root.join("position", JoinType.LEFT);
        Join<Candidates, Recruiters> candidatesRecruitersJoin = root.join("recruiter", JoinType.LEFT);
        Join<Candidates, CandidateStatus> statusJoin = root.join("candidateStatus", JoinType.INNER);

        Predicate searchPre = null;
        Predicate statusPre = null;

        if (StringUtils.hasLength(search)) {
            Predicate fullName = builder.like(root.get("fullName"), "%" + search + "%");
            Predicate email =  builder.like(root.get("email"), "%" + search + "%");
            Predicate phone = builder.like(root.get("phone"), "%" + search + "%");
            Predicate positionName =builder.like(candidatesPositionsJoin.get("positionName"), "%" + search + "%");
            Predicate recruiterName = builder.like(candidatesRecruitersJoin.get("recruiterName"), "%" + search + "%");

            searchPre =  builder.or(fullName,email,phone,positionName,recruiterName);

            query.where(searchPre);

        }

        if( StringUtils.hasLength(status)){
            statusPre = builder.equal(statusJoin.get("statusName"), status);
            query.where(statusPre);
        }

        if(StringUtils.hasLength(search) && StringUtils.hasLength(status)){
            query.where(searchPre,statusPre);
        }


        Selection<CandidatesListDTO> selection = builder.construct(CandidatesListDTO.class,
                root.get("id"),
                root.get("fullName"),
                root.get("phone"),
                root.get("email"),
                root.get("candidateStatus")
        );

        query.select(selection).distinct(true);
        Order orderByCreateDate = builder.desc(root.get("id"));
        Order orderByStatus = builder.asc(builder.selectCase()
                .when(root.get("candidateStatus").get("statusName").in("Waiting for interview"), 1)
                .when(root.get("candidateStatus").get("statusName").in("Waiting for approval"), 2)
                .when(root.get("candidateStatus").get("statusName").in("Waiting for response"), 3)
                .when(root.get("candidateStatus").get("statusName").in("Open"), 4)
                .when(root.get("candidateStatus").get("statusName").in("Passed Interview"), 5)
                .when(root.get("candidateStatus").get("statusName").in("Approved Offer"), 6)
                .when(root.get("candidateStatus").get("statusName").in("Rejected Offer"), 7)
                .when(root.get("candidateStatus").get("statusName").in("Accepted offer"), 8)
                .when(root.get("candidateStatus").get("statusName").in("Declined offer"), 9)
                .when(root.get("candidateStatus").get("statusName").in("Cancelled offer"), 10)
                .when(root.get("candidateStatus").get("statusName").in("Failed interview"), 11)
                .when(root.get("candidateStatus").get("statusName").in("Cancelled interview"), 12)
                .otherwise(13));

        query.orderBy(orderByStatus, orderByCreateDate);

        return entityManager.createQuery(query)
                .setFirstResult((pageIndex - 1) * pageSize)
                .setMaxResults(pageSize).getResultList();

    }

    public List<CandidatesListDTO> getCandidatesSearch(String search, String status) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CandidatesListDTO> query = builder.createQuery(CandidatesListDTO.class);
        Root<Candidates> root = query.from(Candidates.class);
        Join<Candidates, Positions> candidatesPositionsJoin = root.join("position", JoinType.LEFT);
        Join<Candidates, Recruiters> candidatesRecruitersJoin = root.join("recruiter", JoinType.LEFT);
        Join<Candidates, CandidateStatus> statusJoin = root.join("candidateStatus", JoinType.INNER);

        Predicate searchPre = null;
        Predicate statusPre = null;

        if (StringUtils.hasLength(search)) {
            Predicate fullName = builder.like(root.get("fullName"), "%" + search + "%");
            Predicate email =  builder.like(root.get("email"), "%" + search + "%");
            Predicate phone = builder.like(root.get("phone"), "%" + search + "%");
            Predicate positionName =builder.like(candidatesPositionsJoin.get("positionName"), "%" + search + "%");
            Predicate recruiterName = builder.like(candidatesRecruitersJoin.get("recruiterName"), "%" + search + "%");

            searchPre =  builder.or(fullName,email,phone,positionName,recruiterName);

            query.where(searchPre);
        }

        if( StringUtils.hasLength(status)){
            statusPre = builder.equal(statusJoin.get("statusName"), status);
            query.where(statusPre);
        }

        if(StringUtils.hasLength(search) && StringUtils.hasLength(status)){
            query.where(searchPre,statusPre);
        }


        Selection<CandidatesListDTO> selection = builder.construct(CandidatesListDTO.class,
                root.get("id"),
                root.get("fullName"),
                root.get("phone"),
                root.get("email"),
                root.get("candidateStatus")
        );

        query.select(selection).distinct(true);
        Order orderByCreateDate = builder.desc(root.get("id"));
        Order orderByStatus = builder.asc(builder.selectCase()
                .when(root.get("candidateStatus").get("statusName").in("Waiting for interview"), 1)
                .when(root.get("candidateStatus").get("statusName").in("Waiting for approval"), 2)
                .when(root.get("candidateStatus").get("statusName").in("Waiting for response"), 3)
                .when(root.get("candidateStatus").get("statusName").in("Open"), 4)
                .when(root.get("candidateStatus").get("statusName").in("Passed Interview"), 5)
                .when(root.get("candidateStatus").get("statusName").in("Approved Offer"), 6)
                .when(root.get("candidateStatus").get("statusName").in("Rejected Offer"), 7)
                .when(root.get("candidateStatus").get("statusName").in("Accepted offer"), 8)
                .when(root.get("candidateStatus").get("statusName").in("Declined offer"), 9)
                .when(root.get("candidateStatus").get("statusName").in("Cancelled offer"), 10)
                .when(root.get("candidateStatus").get("statusName").in("Failed interview"), 11)
                .when(root.get("candidateStatus").get("statusName").in("Cancelled interview"), 12)
                .otherwise(13));

        query.orderBy(orderByStatus, orderByCreateDate);

        return entityManager.createQuery(query)
                            .getResultList();

    }

    public CandidatePositionDTO getPositionById(Integer id){

         return entityManager.createQuery("select new fa.training.fjb04.ims.util.dto.candidates.CandidatePositionDTO(p.positionName) from Candidates c join c.position p where c.id=:id",CandidatePositionDTO.class)
                 .setParameter("id",id)
                 .getSingleResult();
    }

    public CandidateRecruiterDTO getRecruiterById(Integer id){

        return entityManager.createQuery("select new fa.training.fjb04.ims.util.dto.candidates.CandidateRecruiterDTO(r.recruiterName) from Candidates c join c.recruiter r where c.id=:id", CandidateRecruiterDTO.class)
                .setParameter("id",id)
                .getSingleResult();
    }

    public List<Candidates> getCandidatesHaveStatusOtherThanBan() {
        return entityManager.createQuery("select c from Candidates c join c.candidateStatus st where st.statusName <> :name")
                .setParameter("name", "Banned")
                .getResultList();
    }

    public void updateStatusCancelScheduleInterview(Integer id, CandidateStatus status) {
        entityManager.createQuery("update Candidates c set c.candidateStatus = :status where c.id = :id")
                .setParameter("status", status)
                .setParameter("id", id);
    }

    public List<Candidates> getCandidatesHaveStatusPassedInterview() {
        return entityManager.createQuery("select c from Candidates c join c.candidateStatus st where st.statusName = :name")
                .setParameter("name", "Passed Interview")
                .getResultList();
    }
}
