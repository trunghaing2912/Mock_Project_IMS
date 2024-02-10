package fa.training.fjb04.ims.service.user;

import fa.training.fjb04.ims.entity.user.Department;

import java.util.List;

public interface DepartmentService {

    Department findByDepartmentName (String departmentName);

    List<Department> findAllDepartment();
}
