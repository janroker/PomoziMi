package NULL.DTPomoziMi.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import NULL.DTPomoziMi.model.Candidacy;
import NULL.DTPomoziMi.model.specification.CandidacySpecs;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.service.CandidacyService;
import NULL.DTPomoziMi.web.DTO.CandidacyDTO;
import NULL.DTPomoziMi.web.assemblers.CandidacyDTOAssembler;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/candidacies")
public class CandidacyController {
	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CandidacyDTOAssembler candAssembler;

	@Autowired
	private CandidacyService candidacyService;

	/**
	 * Gets the candidacies for a year. If year is not given then all candidacies
	 * are returned.
	 *
	 * @param year      the year
	 * @param pageable  the pageable
	 * @param assembler the assembler
	 * @return the candidacies
	 */
	@GetMapping(value = "", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> getCandidacies(
		@RequestParam(value = "year", required = false) Integer year, @PageableDefault Pageable pageable,
		PagedResourcesAssembler<Candidacy> assembler
	) {
		try {
			Specification<Candidacy> spec = CandidacySpecs.yearEqual(year);
			Page<Candidacy> page = candidacyService.getCandidacies(pageable, spec);
			PagedModel<CandidacyDTO> pm = assembler.toModel(page, candAssembler);
			return ResponseEntity.ok(pm);

		} catch (Exception e) {
			logger.debug("Exception: {} while fetching candidacies page", e.getMessage());
			throw e;
		}
	}

	/**
	 * Candidate yourself.
	 *
	 * @param year      the year
	 * @param pageable  the pageable
	 * @param assembler the assembler
	 * @return the response entity
	 */
	@PostMapping(value = "", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> candidateYourself(@AuthenticationPrincipal UserPrincipal principal) {
		try {
			return ResponseEntity.ok(candAssembler.toModel(candidacyService.candidateYourself(principal)));
		} catch (Exception e) {
			logger.debug("Exception: {} while creating candidacy", e.getMessage());
			throw e;
		}
	}

	/**
	 * Delete candidacy.
	 *
	 * @param principal the principal
	 * @return the response entity
	 */
	@DeleteMapping(value = "", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> deleteCandidacy(@AuthenticationPrincipal UserPrincipal principal) {
		try {
			return ResponseEntity.ok(candAssembler.toModel(candidacyService.deleteCandidacy(principal)));
		} catch (Exception e) {
			logger.debug("Exception {} while deleting candidacy", e.getMessage());
			throw e;
		}
	}

}
