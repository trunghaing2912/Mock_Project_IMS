package fa.training.fjb04.ims.repository.interview.impl;

import fa.training.fjb04.ims.entity.interview.Interviewer;
import fa.training.fjb04.ims.repository.interview.InterviewerRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class InterviewerRepositoryCustomImpl implements InterviewerRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Interviewer findByInterviewerName(String interviewerName) {
        return (Interviewer) entityManager.createQuery("select i from Interviewer i where i.interviewerName = :name")
                .setParameter("name", interviewerName)
                .getSingleResult();
    }
}
