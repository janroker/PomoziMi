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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import NULL.DTPomoziMi.exception.BindingException;
import NULL.DTPomoziMi.model.Notification;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.service.NotificationService;
import NULL.DTPomoziMi.util.Common;
import NULL.DTPomoziMi.web.DTO.NotificationDTO;
import NULL.DTPomoziMi.web.assemblers.NotificationDTOAssembler;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private NotificationDTOAssembler notificationAssembler;

	@Autowired
	private NotificationService notificationService;

	@GetMapping(value = "/user/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> getUsersNotifications(
		@PathVariable("id") long userID, @PageableDefault Pageable pageable, PagedResourcesAssembler<Notification> assembler,
		@AuthenticationPrincipal UserPrincipal principal
	) {
		try {
			Page<Notification> notifPage = notificationService.findByUser(pageable, userID, principal);
			PagedModel<NotificationDTO> pm = assembler.toModel(notifPage, notificationAssembler);
			return ResponseEntity.ok(pm);

		} catch (Exception e) {
			logger.debug("Exception {} while fetching notifications page for user {}", e.getMessage(), userID);
			throw e;
		}
	}

	@GetMapping(value = "{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> getUsersNotifications(@PathVariable("id") long notifId, @AuthenticationPrincipal UserPrincipal principal) {
		try {
			return ResponseEntity.ok(notificationAssembler.toModel(notificationService.findById(notifId, principal)));
		} catch (Exception e) {
			logger.debug("Exception {} while fetching notification {}", e.getMessage(), notifId);
			throw e;
		}
	}

	@PostMapping(value = "/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> createNotification(
		@PathVariable("id") long idUser, @RequestBody @Valid NotificationDTO notif, BindingResult bindingResult,
		@AuthenticationPrincipal UserPrincipal principal
	) {
		if (bindingResult.hasErrors()) hasErrors(bindingResult);

		try {
			Notification created = notificationService.create(idUser, notif);
			return ResponseEntity
				.created(URI.create("/api/notifications" + created.getIdNotification()))
				.body(notificationAssembler.toModel(created));
		} catch (Exception e) {
			logger.debug("Exception {} while creating notification", e.getMessage(), idUser);
			throw e;
		}

	}
	
	@PatchMapping(value = "/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> markSeen(@PathVariable("id") long idNotif, @AuthenticationPrincipal UserPrincipal principal){
		notificationService.markSeen(idNotif, principal);
		return ResponseEntity.noContent().build();
	}

	private void hasErrors(BindingResult bindingResult) {
		List<FieldError> fieldErrors = bindingResult.getFieldErrors();

		String errors = Common.stringifyErrors(fieldErrors);
		logger.debug("Binding field errors {}", errors);

		throw new BindingException(errors, fieldErrors);
	}

}
