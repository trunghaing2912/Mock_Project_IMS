package fa.training.fjb04.ims.repository.user.impl;

import fa.training.fjb04.ims.entity.candidates.Positions;
import fa.training.fjb04.ims.entity.user.Department;
import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.repository.user.UserRepositoryCustom;
import fa.training.fjb04.ims.util.dto.user.RoleListDTO;
import fa.training.fjb04.ims.util.dto.user.UserListDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.util.StringUtils;

import java.util.List;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<UserListDTO> getAllUser(String search, String role) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserListDTO> query = criteriaBuilder.createQuery(UserListDTO.class);
        Root<User> root = query.from(User.class);
        Join<User, Department> joinUserDepartment = root.join("department", JoinType.INNER);
        Join<User, Positions> joinUserPosition = root.join("position", JoinType.INNER);

        Predicate searchPre = null;
        Predicate rolePre = null;

        if (StringUtils.hasLength(search)) {
            Predicate username = criteriaBuilder.like(root.get("username"), "%" + search + "%");
            Predicate email = criteriaBuilder.like(root.get("email"), "%" + search + "%");
            Predicate phoneNumber = criteriaBuilder.like(root.get("phoneNumber"), "%" + search + "%");
            Predicate status = criteriaBuilder.equal(root.get("status"), search);
            Predicate departmentName = criteriaBuilder.like(joinUserDepartment.get("departmentName"), "%" + search + "%");

            searchPre = criteriaBuilder.or(username, email, phoneNumber, status, departmentName);

            query.where(searchPre);
        }

        if (StringUtils.hasLength(role)) {
            rolePre = criteriaBuilder.equal(joinUserPosition.get("positionName"), role);
            query.where(rolePre);
        }

        if (StringUtils.hasLength(search) && StringUtils.hasLength(role)) {
            query.where(searchPre, rolePre);
        }

        Selection<UserListDTO> selection = criteriaBuilder.construct(UserListDTO.class,
                root.get("id"),
                root.get("username"),
                root.get("email"),
                root.get("phoneNumber"),
                root.get("position"),
                root.get("roles"),
                root.get("status")
        );

        query.select(selection).distinct(true);

        Order orderByRole = criteriaBuilder.asc(root.get("roles"));
        Order orderByStatus = criteriaBuilder.asc(criteriaBuilder.selectCase()
                .when(root.get("status").in("ACTIVE"), 1)
                .when(root.get("status").in("INACTIVE"), 2)
                .otherwise(3)
        );

        query.orderBy(orderByStatus, orderByRole);

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<UserListDTO> getUserPaging(Integer pageIndex, Integer pageSize, String search, String role) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserListDTO> query = criteriaBuilder.createQuery(UserListDTO.class);
        Root<User> root = query.from(User.class);
        Join<User, Department> joinUserDepartment = root.join("department", JoinType.INNER);
        Join<User, Positions> joinUserPosition = root.join("position", JoinType.INNER);

        Predicate searchPre = null;
        Predicate rolePre = null;

        if (StringUtils.hasLength(search)) {
            Predicate username = criteriaBuilder.like(root.get("username"), "%" + search + "%");
            Predicate email = criteriaBuilder.like(root.get("email"), "%" + search + "%");
            Predicate phoneNumber = criteriaBuilder.like(root.get("phoneNumber"), "%" + search + "%");
            Predicate status = criteriaBuilder.equal(root.get("status"), search);
            Predicate departmentName = criteriaBuilder.like(joinUserDepartment.get("departmentName"), "%" + search + "%");

            searchPre = criteriaBuilder.or(username, email, phoneNumber, status, departmentName);

            query.where(searchPre);
        }

        if (StringUtils.hasLength(role)) {
            rolePre = criteriaBuilder.equal(joinUserPosition.get("positionName"), role);
            query.where(rolePre);
        }

        if (StringUtils.hasLength(search) && StringUtils.hasLength(role)) {
            query.where(searchPre, rolePre);
        }

        Selection<UserListDTO> selection = criteriaBuilder.construct(UserListDTO.class,
                root.get("id"),
                root.get("username"),
                root.get("email"),
                root.get("phoneNumber"),
                root.get("position"),
                root.get("roles"),
                root.get("status")
        );

        query.select(selection).distinct(true);

        Order orderByRole = criteriaBuilder.asc(root.get("roles"));
        Order orderByStatus = criteriaBuilder.asc(criteriaBuilder.selectCase()
                .when(root.get("status").in("ACTIVE"), 1)
                .when(root.get("status").in("INACTIVE"), 2)
                .otherwise(3)
        );

        query.orderBy(orderByStatus, orderByRole);

        return  entityManager.createQuery(query)
                .setFirstResult((pageIndex - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<User> getUsersIsManager() {
        return entityManager.createQuery("select u from User u join u.roles r where r.roleName = :role")
                .setParameter("role", "MANAGER")
                .getResultList();
    }

    @Override
    public List<RoleListDTO> getRolesById(Integer id) {
        return entityManager.createQuery("select new fa.training.fjb04.ims.util.dto.user.RoleListDTO(r.roleName) from User  u join u.roles r where u.id=:id",RoleListDTO.class )
                .setParameter("id",id)
                .getResultList();
    }
}
