/*
 * 
 */
package NULL.DTPomoziMi.web.controller;

import static NULL.DTPomoziMi.model.specification.ReqSpecs.autAttrLike;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import NULL.DTPomoziMi.exception.BindingException;
import NULL.DTPomoziMi.model.Request;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.service.RequestService;
import NULL.DTPomoziMi.util.Common;
import NULL.DTPomoziMi.web.DTO.CreateRequestDTO;
import NULL.DTPomoziMi.web.DTO.RequestDTO;
import NULL.DTPomoziMi.web.assemblers.RequestDTOAssembler;

@PreAuthorize(value = "isAuthenticated()")
@RestController
@RequestMapping("/api/requests")
public class RequestController { // TODO linkovi...
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RequestService requestService;

	@Autowired
	private RequestDTOAssembler requestDTOassembler;

	/**
	 * Block request.
	 *
	 * @param id the id
	 * @return the response entity
	 */
	@PatchMapping(value = "/blockUnblock/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> blockUnblockRequest(
		@PathVariable("id") long id, @AuthenticationPrincipal UserPrincipal principal, @RequestParam(name = "enabled") boolean enabled
	) {
		try {
			RequestDTO blocked = requestDTOassembler.toModel(requestService.blockUnblockRequest(id, principal, enabled));
			blocked.add(getLinks(id));
			return new ResponseEntity<>(blocked, HttpStatus.OK);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw e;
		}
	}

	/**
	 * Creates the request.
	 *
	 * @param requestDTO    the request DTO
	 * @param bindingResult the binding result
	 * @param request       the request
	 * @return the response entity
	 */
	@PostMapping(value = "", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> createRequest(
		@RequestBody @Valid CreateRequestDTO requestDTO, BindingResult bindingResult, HttpServletRequest request,
		@AuthenticationPrincipal UserPrincipal principal
	) {
		if (bindingResult.hasErrors()) hasErrors(bindingResult);

		try {
			Request saved = requestService.createRequest(requestDTO, principal);
			RequestDTO dto = requestDTOassembler.toModel(saved);
			dto.add(getLinks(saved.getIdRequest()));
			return ResponseEntity.created(URI.create("/api/requests/" + saved.getIdRequest())).body(dto);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw e;
		}
	}

	/**
	 * Delete request.
	 *
	 * @param requestId     the request id
	 * @param principal the principal
	 * @return the response entity
	 */
	@DeleteMapping(value = "/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> deleteRequest(@PathVariable("id") long requestId, @AuthenticationPrincipal UserPrincipal principal) {
		try {
			Request deleted = requestService.deleteRequest(requestId, principal);
			RequestDTO dto = requestDTOassembler.toModel(deleted);
			dto.add(getLinks(requestId));
			return ResponseEntity.ok(dto);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw e;
		}
	}

	/**
	 * Returns active requests, based on the radius and logged in User's location.
	 * The radius is optional and if not set, 0 is treated as default value. If the
	 * logged in user hasn't set his/her location then only requests without
	 * location are returned.
	 *
	 * @param pageable  the pageable
	 * @param assembler the assembler
	 * @param radius    the radius
	 * @return the active
	 */
	@GetMapping(value = "/active", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> getActive(
		@PageableDefault Pageable pageable, PagedResourcesAssembler<Request> assembler,
		@RequestParam(name = "radius", required = false) Double radius, @AuthenticationPrincipal UserPrincipal principal,
		@RequestParam(value = "firstName", required = false) String firstName,
		@RequestParam(value = "lastName", required = false) String lastName, @RequestParam(value = "email", required = false) String email,
		@RequestParam(value = "generalSearch", required = false) String generalSearch
	) {
		try {
			
			Specification<Request> specs = autAttrLike(firstName, "firstName")
					.and(autAttrLike(lastName, "lastName"))
					.and(autAttrLike(email, "email"))
					.and(createGeneralSpecs(generalSearch));
			
			Page<Request> page = requestService.getAllActiveRequests(specs, pageable, radius, principal);

			PagedModel<RequestDTO> pagedModel = assembler
				.toModel(
					page, requestDTOassembler,
					linkTo(methodOn(RequestController.class).getActive(pageable, null, radius, principal, firstName, lastName, email, generalSearch)).withSelfRel()
				);

			pagedModel.add(getLinks(0));

			return new ResponseEntity<>(pagedModel, HttpStatus.OK);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw e;
		}
	}
	
	private Specification<Request> createGeneralSpecs(String str) {
		Specification<Request> spec = autAttrLike(null, null); // ovaj je always true;
		if (str == null) return spec; // nije bitno ak je null

		String[] parts = str.split("\\s+");

		for (String part : parts) { spec = spec.and(autAttrLike(part, "firstName").or(autAttrLike(part, "lastName")).or(autAttrLike(part, "email"))); }
		return spec;
	}

	/**
	 * Returns all authored requests by author's id as a map of finalized, blocked
	 * and active requests.
	 *
	 * @param userId the user id
	 * @return the authored requests
	 */
	@GetMapping(value = "/authored/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> getAuthoredRequests(@PathVariable(name = "id") Long userId, @AuthenticationPrincipal UserPrincipal principal) {
		try {
			EntityModel<?> model = EntityModel.of(requestService.getAuthoredRequests(userId, principal));
			model.add(linkTo(methodOn(getClass()).getAuthoredRequests(userId, principal)).withSelfRel());
			model.add(getLinks(0));

			return ResponseEntity.ok(model);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw e;
		}
	}

	@GetMapping(value = "/byExecutor/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> getRequestsByExecutor(
		@PathVariable(name = "id") Long userId, @AuthenticationPrincipal UserPrincipal principal
	) {
		try {
			EntityModel<?> model = EntityModel.of(requestService.getRequestsByExecutor(userId, principal));
			model.add(linkTo(methodOn(getClass()).getRequestsByExecutor(userId, principal)).withSelfRel());
			model.add(getLinks(0));

			return ResponseEntity.ok(model);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw e;
		}
	}

	/**
	 * Gets the request by id.
	 *
	 * @param id the id
	 * @return the request
	 */
	@GetMapping(value = "/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<RequestDTO> getRequest(@PathVariable("id") long id, @AuthenticationPrincipal UserPrincipal principal) {
		try {
			RequestDTO req = requestDTOassembler.toModel(requestService.getRequestbyId(id, principal));

			req.add(getLinks(id));

			return new ResponseEntity<>(req, HttpStatus.OK);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw e;
		}
	}

	/**
	 * Mark executed.
	 *
	 * @param id the id
	 * @return the response entity
	 */
	@PatchMapping(value = "/markExecuted/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> markExecuted(@PathVariable("id") long id, @AuthenticationPrincipal UserPrincipal principal) {
		try {
			RequestDTO req = requestDTOassembler.toModel(requestService.markExecuted(id, principal));
			req.add(getLinks(id));
			return ResponseEntity.ok(req);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw e;
		}
	}

	/**
	 * Pick for execution.
	 *
	 * @param id         the id
	 * @param user       the user
	 * @param requestDTO the request DTO
	 * @return the response entity
	 */
	@PatchMapping(value = "pickForExecution/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> pickForExecution(@PathVariable("id") long id, @AuthenticationPrincipal UserPrincipal principal) {
		try {
			RequestDTO executed = requestDTOassembler.toModel(requestService.pickForExecution(id, principal));
			executed.add(getLinks(id));
			return new ResponseEntity<>(executed, HttpStatus.OK);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw e;
		}
	}

	/**
	 * Confirm execution.
	 *
	 * @param id        the id
	 * @param confirm   if true then confirm, else remove executor
	 * @param principal the principal
	 * @return the response entity
	 */
	@PatchMapping(value = "confirmExecution/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> confirmExecution(
		@PathVariable("id") long id, @RequestParam("confirm") boolean confirm, @AuthenticationPrincipal UserPrincipal principal
	) {
		try {
			RequestDTO dto = requestDTOassembler.toModel(requestService.confirmExecution(id, confirm, principal));
			dto.add(getLinks(id));
			return ResponseEntity.ok(dto);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw e;
		}
	}

	@PatchMapping(value = "backOff/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> backOff(@PathVariable("id") long id, @AuthenticationPrincipal UserPrincipal principal) {
		try {
			RequestDTO dto = requestDTOassembler.toModel(requestService.backOff(id, principal));
			dto.add(getLinks(id));
			return ResponseEntity.ok(dto);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw e;
		}
	}

	/**
	 * Update request.
	 *
	 * @param id         the id
	 * @param requestDTO the request DTO
	 * @return the response entity
	 */
	@PutMapping(value = "/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> updateRequest(
		@PathVariable("id") long id, @RequestBody @Valid RequestDTO requestDTO, BindingResult bindingResult,
		@AuthenticationPrincipal UserPrincipal principal
	) {
		if (bindingResult.hasErrors()) hasErrors(bindingResult);

		try {
			RequestDTO updated = requestDTOassembler.toModel(requestService.updateRequest(id, requestDTO, principal));
			updated.add(getLinks(id));
			return new ResponseEntity<>(updated, HttpStatus.OK);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw e;
		}
	}

	private void hasErrors(BindingResult bindingResult) {
		List<FieldError> fieldErrors = bindingResult.getFieldErrors();

		String errors = Common.stringifyErrors(fieldErrors);
		logger.debug("Binding field errors {}", errors);

		throw new BindingException(errors, fieldErrors);
	}

	private Link[] getLinks(long id) {
		return new Link[] { linkCreate(), linkOne(id), linkUpdate(id), linkDelete(id), linkBlock(id), linkPick(id), confirmExecution(id), linkExecuted(id),
			linkBackoff(id), linkActive(id), linkAuthored(id), linkByExecutor(id) };
	}

	private Link linkBlock(long id) {
		return linkTo(methodOn(getClass()).blockUnblockRequest(id, null, true)).withRel("blockUnblock").withType("patch");
	}

	private Link linkCreate() {
		return linkTo(methodOn(getClass()).createRequest(null, null, null, null)).withRel("create").withType("post");
	}

	private Link linkDelete(long id) { return linkTo(methodOn(getClass()).deleteRequest(id, null)).withRel("delete").withType("delete"); }

	private Link linkActive(long id) {
		return linkTo(methodOn(getClass()).getActive(PageRequest.of(0, 10, Sort.by("idRequest").ascending()), null, null, null, null, null, null, null))
			.withRel("active")
			.withType("get");
	}

	private Link linkAuthored(long id) {
		return linkTo(methodOn(getClass()).getAuthoredRequests(id, null)).withRel("authored").withType("get");
	}

	private Link linkByExecutor(long id) {
		return linkTo(methodOn(getClass()).getRequestsByExecutor(id, null)).withRel("byExecutor").withType("get");
	}

	private Link linkOne(long id) { return linkTo(methodOn(getClass()).getRequest(id, null)).withRel("one").withType("get"); }

	private Link linkExecuted(long id) {
		return linkTo(methodOn(getClass()).markExecuted(id, null)).withRel("markExecuted").withType("patch");
	}

	private Link linkPick(long id) { return linkTo(methodOn(getClass()).pickForExecution(id, null)).withRel("pick").withType("patch"); }

	private Link linkBackoff(long id) { return linkTo(methodOn(getClass()).backOff(id, null)).withRel("backoff").withType("patch"); }

	private Link linkUpdate(long id) {
		return linkTo(methodOn(getClass()).updateRequest(id, null, null, null)).withRel("update").withType("put");
	}

	private Link confirmExecution(long id) {
		return linkTo(methodOn(getClass()).confirmExecution(id, true, null)).withRel("confirmExecution").withType("patch");
	}
}
