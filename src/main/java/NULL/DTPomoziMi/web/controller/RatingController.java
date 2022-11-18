package NULL.DTPomoziMi.web.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import NULL.DTPomoziMi.exception.BindingException;
import NULL.DTPomoziMi.model.Rating;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.service.RatingService;
import NULL.DTPomoziMi.util.Common;
import NULL.DTPomoziMi.web.DTO.RatingDTO;
import NULL.DTPomoziMi.web.assemblers.RatingDTOAssembler;

/**
 * The Class RatingController.
 */
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/ratings")
public class RatingController {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/** The rating DTO assembler. */
	@Autowired
	private RatingDTOAssembler ratingDTOAssembler;

	/** The rating service. */
	@Autowired
	private RatingService ratingService;

	/**
	 * Gets the ratings.
	 *
	 * @param pageable  the pageable
	 * @param assembler the assembler
	 * @return the ratings
	 */
	@GetMapping(value = "/user/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> getUsersRatings(
		@PathVariable("id") long userID, @PageableDefault Pageable pageable, PagedResourcesAssembler<Rating> assembler
	) {
		try {
			Page<Rating> ratingsPage = ratingService.findByRated(pageable, userID);
			PagedModel<RatingDTO> pm = assembler.toModel(ratingsPage, ratingDTOAssembler);
			return ResponseEntity.ok(pm);

		} catch (Exception e) {
			logger.debug("Exception {} while fetching ratings page", e.getMessage());
			throw e;
		}
	}

	/**
	 * Gets the rating by id.
	 *
	 * @param id the id
	 * @return the rating by id
	 */
	@GetMapping(value = "/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> getRatingById(@PathVariable("id") Long id, @AuthenticationPrincipal UserPrincipal principal) {
		try {
			return ResponseEntity.ok(ratingDTOAssembler.toModel(ratingService.getRatingById(id, principal)));
		} catch (Exception e) {
			logger.debug("Exception {} while fetching rating by id", e.getMessage());
			throw e;
		}
	}

	/**
	 * Creates the rating.
	 *
	 * @param idUser        the id user to be rated
	 * @param idReq         the id of the request - optional
	 * @param rating        the rating
	 * @param bindingResult the binding result
	 * @return the response entity
	 */
	@PostMapping(value = "/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> createRating(
		@PathVariable("id") long idUser, @RequestParam(value = "idReq", required = false) Long idReq, @RequestBody @Valid RatingDTO rating,
		BindingResult bindingResult, @AuthenticationPrincipal UserPrincipal principal
	) {
		if (bindingResult.hasErrors()) hasErrors(bindingResult);

		try {
			Rating saved = ratingService.create(rating, idUser, idReq, principal);
			return ResponseEntity.created(URI.create("/api/ratings" + saved.getIdRating())).body(ratingDTOAssembler.toModel(saved));
		} catch (Exception e) {
			logger.debug("Exception {} while creating rating", e.getMessage());
			throw e;
		}
	}

	/**
	 * Update rating.
	 *
	 * @param idRating  the id rating
	 * @param ratingDTO the rating DTO
	 * @return the response entity
	 */

	@PutMapping(value = "/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> updateRating(
		@PathVariable("id") long idRating, @RequestBody @Valid RatingDTO ratingDTO, BindingResult bindingResult,
		@AuthenticationPrincipal UserPrincipal principal
	) {
		if (bindingResult.hasErrors()) hasErrors(bindingResult);

		try {
			return ResponseEntity.ok(ratingDTOAssembler.toModel(ratingService.update(ratingDTO, idRating, principal)));
		} catch (Exception e) {
			logger.debug("Exception {} while updating rating", e.getMessage());
			throw e;
		}

	}

	/**
	 * Delete rating.
	 *
	 * @param idRating the id rating
	 * @return the response entity
	 */
	@DeleteMapping(value = "/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> deleteRating(@PathVariable("id") long idRating, @AuthenticationPrincipal UserPrincipal principal) {
		try {
			return ResponseEntity.ok(ratingDTOAssembler.toModel(ratingService.deleteById(idRating, principal)));
		} catch (Exception e) {
			logger.debug("Exception {} while updating rating", e.getMessage());
			throw e;
		}
	}

	private void hasErrors(BindingResult bindingResult) {
		List<FieldError> fieldErrors = bindingResult.getFieldErrors();

		String errors = Common.stringifyErrors(fieldErrors);
		logger.debug("Binding field errors {}", errors);

		throw new BindingException(errors, fieldErrors);
	}

}
