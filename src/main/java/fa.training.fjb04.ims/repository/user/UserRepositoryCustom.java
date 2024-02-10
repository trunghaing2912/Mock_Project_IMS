package fa.training.fjb04.ims.repository.user;

import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.util.dto.user.RoleListDTO;
import fa.training.fjb04.ims.util.dto.user.UserListDTO;

import java.util.List;

public interface UserRepositoryCustom {

    List<UserListDTO> getAllUser(String search, String field);

    List<UserListDTO> getUserPaging(Integer pageIndex, Integer pageSize, String search, String role);

    List<User> getUsersIsManager();
    List<RoleListDTO> getRolesById(Integer id);
}
