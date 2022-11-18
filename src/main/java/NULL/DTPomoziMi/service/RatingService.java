package NULL.DTPomoziMi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.MappingException;

import NULL.DTPomoziMi.exception.EntityMissingException;
import NULL.DTPomoziMi.model.Rating;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.web.DTO.RatingDTO;

public interface RatingService {

	/**
	 * Deletes the entity with the given id.
	 *
	 * @param id        must not be {@literal null}.
	 * @param principal the principal
	 * @return deleted entity
	 * @throws IllegalArgumentException in case the given {@literal id} is
	 *                                  {@literal null}
	 * @throws EntityMissingException   - if element with given <code>id</code> does
	 *                                  not exist
	 */
	Rating deleteById(Long id, UserPrincipal principal);

	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id
	 * @throws EntityMissingException   - if element with given <code>id</code> does
	 *                                  not exist
	 * @throws IllegalArgumentException - if id is null.
	 */
	Rating fetch(Long id);

	/**
	 * Creates the rating.
	 *
	 * @param rating    the rating
	 * @param idUser    the id user
	 * @param idRequest the id request
	 * @param principal the principal
	 * @return the rating
	 * @throws MappingException - if a runtime error occurs while mapping
	 */
	Rating create(RatingDTO rating, long idUser, Long idRequest, UserPrincipal principal);

	/**
	 * Update rating.
	 *
	 * @param rating    the rating
	 * @param ratingId  the rating id
	 * @param principal the principal
	 * @return the rating
	 * @throws MappingException       - if a runtime error occurs while mapping
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 */
	Rating update(RatingDTO rating, long ratingId, UserPrincipal principal);

	/**
	 * Gets the rating by id.
	 *
	 * @param id        the id
	 * @param principal the principal
	 * @return the rating by id
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 */
	Rating getRatingById(Long id, UserPrincipal principal);

	/**
	 * Find by rated.
	 *
	 * @param pageable the pageable
	 * @param userID   the user ID
	 * @return the page
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 */
	Page<Rating> findByRated(Pageable pageable, long userID);
}
