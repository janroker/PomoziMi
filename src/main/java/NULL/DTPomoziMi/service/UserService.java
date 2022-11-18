package NULL.DTPomoziMi.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import NULL.DTPomoziMi.exception.EntityMissingException;
import NULL.DTPomoziMi.model.User;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.web.DTO.RatingDTO;
import NULL.DTPomoziMi.web.DTO.UserDTO;
import NULL.DTPomoziMi.web.DTO.UserRegisterDTO;

public interface UserService {

	/**
	 * Register user.
	 *
	 * @param user the user
	 * @return the user
	 */
	User registerUser(UserRegisterDTO user);

	/**
	 * Gets the user by email.
	 *
	 * @param email the email
	 * @return the user by email
	 */
	User getUserByEmail(String email);

	/**
	 * Fetch.
	 *
	 * @param id the id
	 * @return the user
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 */
	User fetch(long id);

	/**
	 * Gets the user by ID.
	 *
	 * @param ID the id
	 * @return the user by ID
	 * @throws EntityMissingException if element with given <code>id</code> does not
	 *                                exist
	 */
	User getUserByID(long ID, UserPrincipal principal);

	/**
	 * Find users.
	 *
	 * @param pageable      the pageable
	 * @param specification the specification
	 * @return the page
	 */
	Page<User> findUsers(Pageable pageable, Specification<User> specification, UserPrincipal principal);

	/**
	 * Block or unblock user.
	 *
	 * @param id      the id
	 * @param enabled the enabled
	 * @return the user
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 */
	User blockUnblockUser(long id, boolean enabled);

	/**
	 * Gets the statistics.
	 *
	 * @param idUser the id user
	 * @return the statistics
	 * @throws RuntimeException if too many candidacies for a year
	 */
	Map<String, Object> getStatistics(long idUser);

	/**
	 * Update user.
	 *
	 * @param userDTO the user DTO
	 * @param id      the id
	 * @return the user
	 */
	User updateUser(@Valid UserDTO userDTO, long id, UserPrincipal principal);

	/**
	 * Gets the chain of trust.
	 *
	 * @param id the id
	 * @return the chain of trust
	 */
	List<RatingDTO> getChainOfTrust(long id, UserPrincipal principal);

	/**
	 * Delete user.
	 *
	 * @param idUser the id user
	 * @return the user
	 */
	User deleteUser(long idUser, UserPrincipal principal);
}
