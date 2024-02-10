package fa.training.fjb04.ims.repository.offer.Impl;

import fa.training.fjb04.ims.entity.candidates.Candidates;
import fa.training.fjb04.ims.entity.offer.Offer;
import fa.training.fjb04.ims.entity.user.Department;
import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.repository.offer.OfferRepositoryCustom;
import fa.training.fjb04.ims.util.dto.offer.DepartmentListDTO;
import fa.training.fjb04.ims.util.dto.offer.OfferCandidateListDTO;
import fa.training.fjb04.ims.util.dto.offer.OfferListDTO;
import fa.training.fjb04.ims.util.dto.offer.OfferUserListDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public class OfferRepositoryCustomImpl implements OfferRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<OfferListDTO> getAllOffer(String search, String department,String status) {
        CriteriaBuilder criteriaBuilder=entityManager.getCriteriaBuilder();
        CriteriaQuery<OfferListDTO> query=criteriaBuilder.createQuery(OfferListDTO.class);
        Root<Offer> root=query.from(Offer.class);
        Join<Offer, Department> offerDepartmentJoin=root.join("department", JoinType.INNER);
        Join<Offer, Candidates>offerCandidatesJoin=root.join("candidate",JoinType.INNER);
        Join<Offer, User>offerUserJoin =root.join("approvedBy",JoinType.INNER);

        Predicate searchPre=null;
        Predicate departmentPre=null;
        Predicate statusPre=null;

        if(StringUtils.hasLength(search)){
            Predicate candidateName=criteriaBuilder.like(offerCandidatesJoin.get("fullName"),"%" + search + "%");
            Predicate approvedBy=criteriaBuilder.like(offerUserJoin.get("username"),"%" + search + "%");
            Predicate email=criteriaBuilder.like(offerCandidatesJoin.get("email"),"%" + search + "%");
            Predicate note=criteriaBuilder.equal(root.get("note"),search);

            searchPre=criteriaBuilder.or(candidateName,approvedBy,email,note);

            query.where(searchPre);
        }
        if(StringUtils.hasLength(department)){
            departmentPre=criteriaBuilder.equal(offerDepartmentJoin.get("departmentName"), department);
            query.where(departmentPre);
        }
        if(StringUtils.hasLength(status)){
            statusPre=criteriaBuilder.equal(root.get("offerStatus").as(String.class),status);
            query.where(statusPre);
        }
        if(StringUtils.hasLength(search)&& StringUtils.hasLength(department)){
            query.where(searchPre,departmentPre);
        }
        if(StringUtils.hasLength(search) && StringUtils.hasLength(status)){
            query.where(searchPre,statusPre);
        }
        if(StringUtils.hasLength(department) && StringUtils.hasLength(status)){
            query.where(departmentPre,statusPre);
        }
        if(StringUtils.hasLength(search) && StringUtils.hasLength(department) && StringUtils.hasLength(status)){
            query.where(searchPre,departmentPre,statusPre);
        }
        Selection<OfferListDTO>selection=criteriaBuilder.construct(OfferListDTO.class,
                root.get("id"),
                root.get("note"),
                root.get("offerStatus")
        );
        query.select(selection).distinct(true);

        Order orderByApprover=criteriaBuilder.asc(criteriaBuilder.selectCase()
                .when(root.get("offerStatus").in("WAITING_FOR_APPROVAL"),1)
                .when(root.get("offerStatus").in("APPROVED_OFFER"),2)
                .when(root.get("offerStatus").in("REJECTED_OFFER"),3)
                .when(root.get("offerStatus").in("WAITING_FOR_RESPONSE"),4)
                .when(root.get("offerStatus").in("ACCEPTED_OFFER"),5)
                .when(root.get("offerStatus").in("DECLINED_OFFER"),6)
                .when(root.get("offerStatus").in("CANCELLED"),7)
                .otherwise(8));
        query.orderBy(orderByApprover);

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<OfferListDTO> getOfferPage(Integer pageIndex,Integer pageSize,String search,String department,String status) {
        CriteriaBuilder criteriaBuilder=entityManager.getCriteriaBuilder();
        CriteriaQuery<OfferListDTO> query=criteriaBuilder.createQuery(OfferListDTO.class);
        Root<Offer> root=query.from(Offer.class);
        Join<Offer, Department> offerDepartmentJoin=root.join("department", JoinType.INNER);
        Join<Offer, Candidates>offerCandidatesJoin=root.join("candidate",JoinType.INNER);
        Join<Offer, User>offerUserJoin =root.join("approvedBy",JoinType.INNER);

        Predicate searchPre=null;
        Predicate departmentPre=null;
        Predicate statusPre=null;

        if(StringUtils.hasLength(search)){
            Predicate candidateName=criteriaBuilder.like(offerCandidatesJoin.get("fullName"),"%" + search + "%");
            Predicate approvedBy=criteriaBuilder.like(offerUserJoin.get("username"),"%" + search + "%");
            Predicate email=criteriaBuilder.like(offerCandidatesJoin.get("email"),"%" + search + "%");
            Predicate note=criteriaBuilder.equal(root.get("note"),search);

            searchPre=criteriaBuilder.or(candidateName,approvedBy,email,note);

            query.where(searchPre);
        }
        if(StringUtils.hasLength(department)){
            departmentPre=criteriaBuilder.equal(offerDepartmentJoin.get("departmentName"), department);
            query.where(departmentPre);
        }
        if(StringUtils.hasLength(status)){
            statusPre=criteriaBuilder.equal(root.get("offerStatus"),status);
            query.where(statusPre);
        }
        if(StringUtils.hasLength(search)&& StringUtils.hasLength(department)){
            query.where(searchPre,departmentPre);
        }
        if(StringUtils.hasLength(search) && StringUtils.hasLength(status)){
            query.where(searchPre,statusPre);
        }
        if(StringUtils.hasLength(department) && StringUtils.hasLength(status)){
            query.where(departmentPre,statusPre);
        }

        if(StringUtils.hasLength(search) && StringUtils.hasLength(department) && StringUtils.hasLength(status)){
            query.where(searchPre,departmentPre,statusPre);
        }
        Selection<OfferListDTO>selection=criteriaBuilder.construct(OfferListDTO.class,
                root.get("id"),
                root.get("note"),
                root.get("offerStatus")

        );
        query.select(selection).distinct(true);

        Order orderByApprover=criteriaBuilder.asc(criteriaBuilder.selectCase()
                .when(root.get("offerStatus").in("WAITING_FOR_APPROVAL"),1)
                .when(root.get("offerStatus").in("APPROVED_OFFER"),2)
                .when(root.get("offerStatus").in("REJECTED_OFFER"),3)
                .when(root.get("offerStatus").in("WAITING_FOR_RESPONSE"),4)
                .when(root.get("offerStatus").in("ACCEPTED_OFFER"),5)
                .when(root.get("offerStatus").in("DECLINED_OFFER"),6)
                .when(root.get("offerStatus").in("CANCELLED"),7)
                .otherwise(8));
        query.orderBy(orderByApprover);
        return entityManager.createQuery(query)
                .setFirstResult((pageIndex - 1) * pageSize)
                .setMaxResults(pageSize).getResultList();
    }

    @Override
    public OfferCandidateListDTO getCandidateById(Integer id) {
        return entityManager.createQuery("select new fa.training.fjb04.ims.util.dto.offer.OfferCandidateListDTO(c.fullName,c.email) from Offer o join o.candidate c where o.id=:id",OfferCandidateListDTO.class)
                .setParameter("id",id)
                .getSingleResult();
    }

    @Override
    public List<DepartmentListDTO> getDepartmentById(Integer id) {
        return entityManager.createQuery("select new fa.training.fjb04.ims.util.dto.offer.DepartmentListDTO(d.departmentName) from Offer o join o.department d where o.id=:id",DepartmentListDTO.class)
                .setParameter("id",id)
                .getResultList();

    }

    @Override
    public OfferUserListDTO getUserById(Integer id) {
        return entityManager.createQuery("select new fa.training.fjb04.ims.util.dto.offer.OfferUserListDTO(u.fullName) from Offer o join o.approvedBy u where o.id=:id",OfferUserListDTO.class)
                .setParameter("id",id)
                .getSingleResult();
    }
}
