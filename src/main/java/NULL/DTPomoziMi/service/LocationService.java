package NULL.DTPomoziMi.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.modelmapper.MappingException;

import NULL.DTPomoziMi.exception.EntityMissingException;
import NULL.DTPomoziMi.model.Location;
import NULL.DTPomoziMi.web.DTO.LocationDTO;

public interface LocationService {

	/**
	 * Saves a given entity. Use the returned instance for further operations as the
	 * save operation might have changed the entity instance completely.
	 *
	 * @param entity must not be {@literal null}.
	 * @return the saved entity; will never be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@literal entity} is
	 *                                  {@literal null}.
	 * @throws MappingException         - if a runtime error occurs while mapping
	 */
	Location save(LocationDTO entity);

	/**
	 * Saves a given entity. Use the returned instance for further operations as the
	 * save operation might have changed the entity instance completely.
	 *
	 * @param entity must not be {@literal null}.
	 * @return the saved entity; will never be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@literal entity} is
	 *                                  {@literal null}.
	 */
	Location save(Location entity);

	/**
	 * Saves all given entities.
	 *
	 * @param entities must not be {@literal null} nor must it contain
	 *                 {@literal null}.
	 * @return the saved entities; will never be {@literal null}. The returned
	 *         {@literal Iterable} will have the same size as the
	 *         {@literal Iterable} passed as an argument.
	 * @throws IllegalArgumentException in case the given {@link Iterable entities}
	 *                                  or one of its entities is {@literal null}.
	 * @throws MappingException         - if a runtime error occurs while mapping
	 */
	Iterable<Location> saveAll(Iterable<LocationDTO> entities);

	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id or {@literal Optional#empty()} if none
	 *         found.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
	Optional<Location> findById(Long id);

	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id
	 * @throws EntityMissingException   - if element with given <code>id</code> does
	 *                                  not exist
	 * @throws IllegalArgumentException - if id is null.
	 */
	Location fetch(Long id);

	/**
	 * Returns whether an entity with the given id exists.
	 *
	 * @param id must not be {@literal null}.
	 * @return {@literal true} if an entity with the given id exists,
	 *         {@literal false} otherwise.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
	boolean existsById(Long id);

	/**
	 * Returns all instances of the type.
	 *
	 * @return all entities
	 */
	Iterable<Location> findAll();

	/**
	 * Returns all instances of the type {@code T} with the given IDs.
	 * <p>
	 * If some or all ids are not found, no entities are returned for these IDs.
	 * <p>
	 * Note that the order of elements in the result is not guaranteed.
	 *
	 * @param ids must not be {@literal null} nor contain any {@literal null}
	 *            values.
	 * @return guaranteed to be not {@literal null}. The size can be equal or less
	 *         than the number of given {@literal ids}.
	 * @throws IllegalArgumentException in case the given {@link Iterable ids} or
	 *                                  one of its items is {@literal null}.
	 */
	Iterable<Location> findAllById(Iterable<Long> ids);

	/**
	 * Returns the number of entities available.
	 *
	 * @return the number of entities.
	 */
	long count();

	/**
	 * Deletes the entity with the given id.
	 *
	 * @param id must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@literal id} is
	 *                                  {@literal null}
	 * @throws EntityMissingException   - if element with given <code>id</code> does
	 *                                  not exist
	 */
	Location deleteById(Long id);

	/**
	 * Deletes the given entities.
	 *
	 * @param entities must not be {@literal null}. Must not contain {@literal null}
	 *                 elements.
	 * @throws IllegalArgumentException in case the given {@literal entities} or one
	 *                                  of its entities is {@literal null}.
	 */
	Iterable<Location> deleteAll(Iterable<LocationDTO> entities);

	/**
	 * Deletes all entities managed by the repository.
	 */
	Iterable<Location> deleteAll();

	/**
	 * Find by latitude and longitude.
	 *
	 * @param latitude  the latitude
	 * @param longitude the longitude
	 * @return the location
	 */
	Location findByLatitudeAndLongitude(BigDecimal latitude, BigDecimal longitude);

}
