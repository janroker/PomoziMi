package NULL.DTPomoziMi.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import NULL.DTPomoziMi.model.User;

public interface UserRepo extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {

	User findByEmail(String email);

	@Transactional
	@Modifying
	@Query(nativeQuery = false, value = "update korisnik u set u.token = :token where u.email = :email")
	void updateToken(@Param(value = "token") String token, @Param(value = "email") String email);

	@Query(nativeQuery = false, value = "SELECT token FROM korisnik WHERE EMAIL = :email AND enabled = TRUE")
	String getTokenByEmail(@Param(value = "email") String email);

	/*
	 * @Transactional
	 * 
	 * @Modifying
	 * 
	 * @Query( nativeQuery = false, value =
	 * "UPDATE korisnik u SET u.enabled = :e where u.IdUser = :IdUser" ) void
	 * updateEnabled(@Param("IdUser") long userID, @Param("e") boolean enabled);
	 */
}
