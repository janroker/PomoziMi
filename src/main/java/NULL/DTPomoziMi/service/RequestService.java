package NULL.DTPomoziMi.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.MappingException;
import org.springframework.hateoas.CollectionModel;

import NULL.DTPomoziMi.exception.EntityMissingException;
import NULL.DTPomoziMi.exception.IllegalActionException;
import NULL.DTPomoziMi.model.Request;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.web.DTO.CreateRequestDTO;
import NULL.DTPomoziMi.web.DTO.RequestDTO;

public interface RequestService {

	/**
	 * Block or unblock request.
	 *
	 * @param idRequest the id request
	 * @param principal the principal
	 * @param enabled   the enabled
	 * @return the request
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 */
	Request blockUnblockRequest(long idRequest, UserPrincipal principal, boolean enabled);

	/**
	 * Creates the request.
	 *
	 * @param request   the request
	 * @param principal the principal
	 * @return the created request
	 * @throws MappingException     - if a runtime error occurs while mapping DTO to
	 *                              entity
	 * @throws NullPointerException if given {@literal request} is {@literal null}
	 *                              reference
	 */
	Request createRequest(CreateRequestDTO request, UserPrincipal principal);

	/**
	 * Delete request.
	 *
	 * @param id_zahtjev the id zahtjev
	 * @param principal  the principal
	 * @return the request
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 */
	Request deleteRequest(long id_zahtjev, UserPrincipal principal);

	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 */
	Request fetch(long requestId);

	/**
	 * Gets the all active requests.
	 *
	 * @param specs the specs
	 * @param pageable  the pageable
	 * @param radius    the radius
	 * @param principal the principal
	 * @return the all active requests
	 */
	Page<Request> getAllActiveRequests(Specification<Request> specs, Pageable pageable, Double radius, UserPrincipal principal);

	/**
	 * Gets the authored requests.
	 *
	 * @param userID    the user ID
	 * @param principal the principal
	 * @return the authored requests
	 */
	Map<String, CollectionModel<RequestDTO>> getAuthoredRequests(long userID, UserPrincipal principal);

	/**
	 * Gets request by id and checks if user is permitted to see such resource. If
	 * not, then throws {@link IllegalAccessException}.
	 *
	 * @param id_zahtjev the id zahtjev
	 * @param principal  the principal
	 * @return the requestby id
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 */
	Request getRequestbyId(long id_zahtjev, UserPrincipal principal);

	/**
	 * Mark executed.
	 *
	 * @param idRequest the id request
	 * @param principal the principal
	 * @return the request
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 * @throws IllegalActionException - if request cannot be marked as finalized
	 */
	Request markExecuted(long idRequest, UserPrincipal principal);

	/**
	 * Execute request.
	 *
	 * @param idRequest the id request
	 * @param principal the principal
	 * @return the request
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 */
	Request pickForExecution(long idRequest, UserPrincipal principal);

	/**
	 * Update request.
	 *
	 * @param idRequest  the id request
	 * @param requestDTO the request DTO
	 * @param principal  the principal
	 * @return the request
	 * @throws NullPointerException   if given {@literal request} is {@literal null}
	 *                                reference
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 */
	Request updateRequest(long idRequest, RequestDTO requestDTO, UserPrincipal principal);

	/**
	 * Back off from execution.
	 *
	 * @param id        the id
	 * @param principal the principal
	 * @return the request
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 */
	Request backOff(long id, UserPrincipal principal);

	/**
	 * Gets the requests by executor.
	 *
	 * @param userId    the user id
	 * @param principal the principal
	 * @return the requests by executor
	 */
	Map<String, CollectionModel<RequestDTO>> getRequestsByExecutor(Long userId, UserPrincipal principal);

	/**
	 * Confirm execution.
	 *
	 * @param id the id
	 * @param confirm the confirm
	 * @param principal the principal
	 * @return the request
	 */
	Request confirmExecution(long id, boolean confirm, UserPrincipal principal);
}