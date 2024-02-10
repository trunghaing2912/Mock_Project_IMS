package fa.training.fjb04.ims.api.user;

import fa.training.fjb04.ims.service.user.UserService;
import fa.training.fjb04.ims.util.dto.user.RoleListDTO;
import fa.training.fjb04.ims.util.dto.user.UserListDTO;
import fa.training.fjb04.ims.util.page.Page;
import fa.training.fjb04.ims.util.page.PageRoles;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class ApiUserController {

    private final UserService userService;

    @GetMapping
    public Page<UserListDTO> getUsers(
            @RequestParam(defaultValue = "1", required = false, value = "pageIndex") Integer pageIndex,
            @RequestParam(defaultValue = "7", required = false, value = "pageSize") Integer pageSize,
            @RequestParam(required = false, value = "search") String search,
            @RequestParam(required = false, value = "role") String role
    ) {
        return userService.getPageUser(pageIndex, pageSize, search, role);
    }

    @GetMapping("/roles/{id}")
    public PageRoles<RoleListDTO>getRole(@PathVariable Integer id){
        return userService.getPageRole(id);
    }
}
