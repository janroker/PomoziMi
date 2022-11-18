package NULL.DTPomoziMi.repository;

import org.springframework.data.repository.CrudRepository;

import NULL.DTPomoziMi.model.Role;
import NULL.DTPomoziMi.model.RoleEntity;

public interface RoleRepo extends CrudRepository<RoleEntity, Long> {

	RoleEntity findByRole(Role role);

}
