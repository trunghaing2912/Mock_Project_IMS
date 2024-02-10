package fa.training.fjb04.ims.repository.user;

import fa.training.fjb04.ims.entity.user.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    Department findByDepartmentName(String departmentName);
}
