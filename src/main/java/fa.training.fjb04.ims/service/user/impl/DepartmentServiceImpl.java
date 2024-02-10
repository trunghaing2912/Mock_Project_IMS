package fa.training.fjb04.ims.service.user.impl;

import fa.training.fjb04.ims.entity.user.Department;
import fa.training.fjb04.ims.repository.user.DepartmentRepository;
import fa.training.fjb04.ims.service.user.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public Department findByDepartmentName(String departmentName) {
        return departmentRepository.findByDepartmentName(departmentName);
    }

    @Override
    public List<Department> findAllDepartment() {
        return departmentRepository.findAll();
    }
}
