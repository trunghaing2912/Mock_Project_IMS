package fa.training.fjb04.ims.repository.user;

import fa.training.fjb04.ims.entity.user.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Roles, Integer>, UserRepositoryCustom {

    Roles findByRoleName(String roleName);
}
