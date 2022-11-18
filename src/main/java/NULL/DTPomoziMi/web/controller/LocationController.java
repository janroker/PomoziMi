package NULL.DTPomoziMi.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import NULL.DTPomoziMi.service.LocationService;
import NULL.DTPomoziMi.web.assemblers.LocationDTOAssembler;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/locations")
public class LocationController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private LocationService LocationService;

	@Autowired
	private LocationDTOAssembler locationAssembler;

	@GetMapping(value = "/{id}", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> getMethodName(@PathVariable("id") long id) {
		try {
			return ResponseEntity.ok(locationAssembler.toModel(LocationService.fetch(id)));
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw e;
		}
	}

}
