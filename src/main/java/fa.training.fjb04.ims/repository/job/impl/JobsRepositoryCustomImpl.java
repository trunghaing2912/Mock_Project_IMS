package fa.training.fjb04.ims.repository.job.impl;


import fa.training.fjb04.ims.entity.jobs.Jobs;
import fa.training.fjb04.ims.entity.jobs.Levels;
import fa.training.fjb04.ims.entity.jobs.Skills;
import fa.training.fjb04.ims.enums.job.JobStatus;
import fa.training.fjb04.ims.repository.job.JobsRepositoryCustom;
import fa.training.fjb04.ims.util.dto.jobs.JobsListDTO;
import fa.training.fjb04.ims.util.dto.jobs.LevelListDTO;
import fa.training.fjb04.ims.util.dto.jobs.SkillListDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

public class JobsRepositoryCustomImpl implements JobsRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<JobsListDTO> getAllJob(String search, String status) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<JobsListDTO> query = criteriaBuilder.createQuery(JobsListDTO.class);
        Root<Jobs> root = query.from(Jobs.class);
        Join<Jobs, Skills> jobsSkillsJoin = root.join("skillsSet", JoinType.LEFT);
        Join<Jobs, Levels> jobsLevelsJoin = root.join("levelsSet", JoinType.LEFT);

        Predicate searchPre = null;
        Predicate statusPre = null;

        if (StringUtils.hasLength(search)) {
            Predicate title = criteriaBuilder.like(root.get("title"), "%" + search + "%");
            Predicate skill = criteriaBuilder.like(jobsSkillsJoin.get("skillName"), "%" + search + "%");
            Predicate startDate = criteriaBuilder.like(root.get("startDate").as(String.class), "%" + search + "%");
            Predicate endDate = criteriaBuilder.like(root.get("endDate").as(String.class), "%" + search + "%");
            Predicate level = criteriaBuilder.like(jobsLevelsJoin.get("levelName"), "%" + search + "%");

            searchPre = criteriaBuilder.or(title, skill, startDate, endDate, level);

            query.where(searchPre);
        }

        if (StringUtils.hasLength(status)) {
            statusPre = criteriaBuilder.equal(root.get("status"), status);
            query.where(statusPre);
        }

        if (StringUtils.hasLength(search) && StringUtils.hasLength(status)) {
            query.where(searchPre, statusPre);
        }

        Selection<JobsListDTO> selection = criteriaBuilder.construct(JobsListDTO.class,
                root.get("id"),
                root.get("title"),
                root.get("startDate"),
                root.get("status"),
                root.get("endDate")
        );

        query.select(selection).distinct(true);

        Order orderByCreatDate = criteriaBuilder.desc(root.get("id"));
        Order orderByStatus = criteriaBuilder.asc(criteriaBuilder.selectCase()
                .when(root.get("status").in("OPEN"), 1)
                .when(root.get("status").in("DRAFT"), 2)
                .otherwise(3));

        query.orderBy(orderByStatus, orderByCreatDate);
        List<JobsListDTO> list = entityManager.createQuery(query)
                .getResultList();
        return list;
    }

    @Override
    public List<JobsListDTO> getJobPaging(Integer pageIndex, Integer pageSize, String search, String status) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<JobsListDTO> query = criteriaBuilder.createQuery(JobsListDTO.class);
        Root<Jobs> root = query.from(Jobs.class);
        Join<Jobs, Skills> jobsSkillsJoin = root.join("skillsSet", JoinType.LEFT);
        Join<Jobs, Levels> jobsLevelsJoin = root.join("levelsSet", JoinType.LEFT);

        Predicate searchPre = null;
        Predicate statusPre = null;

        if (StringUtils.hasLength(search)) {
            Predicate title = criteriaBuilder.like(root.get("title"), "%" + search + "%");
            Predicate skill = criteriaBuilder.like(jobsSkillsJoin.get("skillName"), "%" + search + "%");
            Predicate startDate = criteriaBuilder.like(root.get("startDate").as(String.class), "%" + search + "%");
            Predicate endDate = criteriaBuilder.like(root.get("endDate").as(String.class), "%" + search + "%");
            Predicate level = criteriaBuilder.like(jobsLevelsJoin.get("levelName"), "%" + search + "%");

            searchPre = criteriaBuilder.or(title, skill, startDate, endDate, level);

            query.where(searchPre);
        }

        if (StringUtils.hasLength(status)) {
            statusPre = criteriaBuilder.equal(root.get("status"), status);
            query.where(statusPre);
        }

        if (StringUtils.hasLength(search) && StringUtils.hasLength(status)) {
            query.where(searchPre, statusPre);
        }

        Selection<JobsListDTO> selection = criteriaBuilder.construct(JobsListDTO.class,
                root.get("id"),
                root.get("title"),
                root.get("startDate"),
                root.get("status"),
                root.get("endDate")
        );

        query.select(selection).distinct(true);

        Order orderByCreatDate = criteriaBuilder.desc(root.get("id"));
        Order orderByStatus = criteriaBuilder.asc(criteriaBuilder.selectCase()
                .when(root.get("status").in("OPEN"), 1)
                .when(root.get("status").in("DRAFT"), 2)
                .otherwise(3));

        query.orderBy(orderByStatus, orderByCreatDate);
        List<JobsListDTO> list = entityManager.createQuery(query)
                .setFirstResult((pageIndex -1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
        return list;
    }

    @Override
    public List<SkillListDTO> getSkillsById(Integer id) {
        return entityManager.createQuery("select new fa.training.fjb04.ims.util.dto.jobs.SkillListDTO(s.skillName) from Jobs j join j.skillsSet s where j.id = :id", SkillListDTO.class)
                .setParameter("id", id)
                .getResultList();
    }

    @Override
    public List<LevelListDTO> getLevelsById(Integer id) {
        return entityManager.createQuery("select new fa.training.fjb04.ims.util.dto.jobs.LevelListDTO(l.levelName) from Jobs j join j.levelsSet l where j.id = :id", LevelListDTO.class)
                .setParameter("id", id)
                .getResultList();
    }

    @Override
    public List<Jobs> getJobsHaveStatusOpen() {
        return entityManager.createQuery("select j from Jobs j where j.status = :status")
                .setParameter("status", JobStatus.OPEN)
                .getResultList();
    }
}
