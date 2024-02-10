package fa.training.fjb04.ims.service.user.impl;

import fa.training.fjb04.ims.entity.user.Roles;
import fa.training.fjb04.ims.repository.user.RolesRepository;
import fa.training.fjb04.ims.service.user.RolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RolesServiceImpl implements RolesService {

    private final RolesRepository rolesRepository;

    @Override
    public Roles findByRoleName(String roleName) {
        return rolesRepository.findByRoleName(roleName);
    }

    @Override
    public Roles findByRoleId(Integer id) {
        return rolesRepository.findById(id).orElse(null);
    }

    @Override
    public List<Roles> findAllRoles() {
        return rolesRepository.findAll();
    }
}
