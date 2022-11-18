package NULL.DTPomoziMi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import NULL.DTPomoziMi.exception.EntityMissingException;
import NULL.DTPomoziMi.model.Candidacy;
import NULL.DTPomoziMi.security.UserPrincipal;

public interface CandidacyService {
	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id
	 * @throws EntityMissingException   - if element with given <code>id</code> does
	 *                                  not exist
	 * @throws IllegalArgumentException - if id is null.
	 */
	Candidacy fetch(Long id);

	/**
	 * Gets the candidacies.
	 *
	 * @param pageable the pageable
	 * @param spec     the spec
	 * @return the candidacies
	 */
	Page<Candidacy> getCandidacies(Pageable pageable, Specification<Candidacy> spec);

	/**
	 * Count.
	 *
	 * @return the long
	 */
	long count();

	/**
	 * Candidate yourself.
	 *
	 * @param principal the principal
	 * @return the candidacy
	 * @throws RuntimeException if wrong number of candidacies for a year
	 */
	Candidacy candidateYourself(UserPrincipal principal);

	/**
	 * Delete candidacy.
	 *
	 * @param principal the principal
	 * @return the candidacy
	 */
	Candidacy deleteCandidacy(UserPrincipal principal);

}
