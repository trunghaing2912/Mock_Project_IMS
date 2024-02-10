package fa.training.fjb04.ims.service.user;

import fa.training.fjb04.ims.entity.user.Roles;

import java.util.List;

public interface RolesService {

    Roles findByRoleName (String roleName);

    Roles findByRoleId (Integer id);
    List<Roles> findAllRoles();
}
