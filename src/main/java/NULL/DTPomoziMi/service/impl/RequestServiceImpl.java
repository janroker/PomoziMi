package NULL.DTPomoziMi.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.CollectionModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import NULL.DTPomoziMi.event.SendMessageEventPublisher;
import NULL.DTPomoziMi.exception.EntityMissingException;
import NULL.DTPomoziMi.exception.IllegalAccessException;
import NULL.DTPomoziMi.exception.IllegalActionException;
import NULL.DTPomoziMi.model.Location;
import NULL.DTPomoziMi.model.Request;
import NULL.DTPomoziMi.model.RequestStatus;
import NULL.DTPomoziMi.model.Role;
import NULL.DTPomoziMi.model.User;
import NULL.DTPomoziMi.model.specification.ReqSpecs;
import NULL.DTPomoziMi.repository.RequestRepo;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.service.LocationService;
import NULL.DTPomoziMi.service.RequestService;
import NULL.DTPomoziMi.web.DTO.CreateRequestDTO;
import NULL.DTPomoziMi.web.DTO.LocationDTO;
import NULL.DTPomoziMi.web.DTO.RequestDTO;
import NULL.DTPomoziMi.web.assemblers.RequestDTOAssembler;

@PreAuthorize(value = "isAuthenticated()")
@Service
public class RequestServiceImpl implements RequestService {
	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private RequestRepo requestRepo;

	@Autowired
	private LocationService locationService;

	@Autowired
	private RequestDTOAssembler requestDTOAssembler;

	@Autowired
	private SendMessageEventPublisher publisher;
	
	@Override
	public Request createRequest(CreateRequestDTO request, UserPrincipal principal) {
		Request req = modelMapper.map(request, Request.class);

		LocationDTO location = request.getLocation();
		Location loc = resolveLocation(location);
		req.setLocation(loc);

		req.setAuthor(principal.getUser());
		req.setStatus(RequestStatus.ACTIVE);
		req.setConfirmed(false);

		return requestRepo.save(req);
	}

	//location, description, phone, tstmp
	@Override
	public Request updateRequest(long idRequest, RequestDTO requestDTO, UserPrincipal principal) {
		if (!requestDTO.getIdRequest().equals(idRequest)) throw new IllegalArgumentException("Request id must be preserved!");

		User user = principal.getUser();
		Request req = fetch(idRequest);

		if (!user.getIdUser().equals(req.getAuthor().getIdUser())) throw new IllegalAccessException("Only authors can modify requests!");

		if (!req.getStatus().equals(RequestStatus.ACTIVE)) throw new IllegalActionException("Only active requests can be modified!");

		LocationDTO location = requestDTO.getLocation();
		Location loc = resolveLocation(location);
		req.setLocation(loc);

		req.setDescription(requestDTO.getDescription());
		req.setPhone(requestDTO.getPhone());
		req.setTstmp(requestDTO.getTstmp());

		return requestRepo.save(req);
	}

	@Override
	public Request deleteRequest(long idRequest, UserPrincipal principal) {
		Request req = fetch(idRequest);
		User user = principal.getUser();

		if (!user.getIdUser().equals(req.getAuthor().getIdUser()) && !user.getEnumRoles().contains(Role.ROLE_ADMIN))
			throw new IllegalAccessException("Missing permissions to delete the request!");

		if (req.getExecutor() != null) // samo aktivni se smiju obrisati samo tako...
			throw new IllegalActionException("Cannot delete request that has an executor!");

		requestRepo.deleteById(idRequest);
		req.setStatus(RequestStatus.DELETED);
		return req;
	}

	@Override
	public Request blockUnblockRequest(long idRequest, UserPrincipal principal, boolean enabled) {
		Request r = fetch(idRequest);
		User user = principal.getUser();

		if (!user.getIdUser().equals(r.getAuthor().getIdUser()) && !user.getEnumRoles().contains(Role.ROLE_ADMIN))
			throw new IllegalAccessException("Missing permissions to block the request!");

		if (r.getStatus().equals(RequestStatus.FINALIZED)) throw new IllegalActionException("Can't block or unblock finalized request");

		RequestStatus status;

		if (!enabled) {
			status = RequestStatus.BLOCKED;

			if (r.getExecutor() != null) {
				User author = r.getAuthor();
				publisher
					.publishMessageEvent(
						"Zahtijev autora " + author.getFirstName() + " " + author.getLastName() + " s opisom: " + r.getDescription()
							+ " je upravo blokiran.",
						r.getExecutor().getIdUser()
					);
			}
		} else if (r.getExecutor() != null) {
			status = RequestStatus.EXECUTING;

			User author = r.getAuthor();
			publisher
				.publishMessageEvent(
					"Zahtijev autora " + author.getFirstName() + " " + author.getLastName() + " s opisom: " + r.getDescription()
						+ ", čiji ste izvršitelj, je upravo odblokiran.",
					r.getExecutor().getIdUser()
				);
		} else
			status = RequestStatus.ACTIVE;

		r.setStatus(status);

		return requestRepo.save(r);
	}

	@Override
	public Request pickForExecution(long idRequest, UserPrincipal principal) { // TODO ide notifikacija!! ... sto ako... autor ne zeli da mu ovaj izvede..?
		User user = principal.getUser();
		Request r = fetch(idRequest);

		if (!r.getStatus().equals(RequestStatus.ACTIVE)) throw new IllegalActionException("Cannot pick non active request for execution!");

		if (r.getAuthor().getIdUser().equals(user.getIdUser())) throw new IllegalActionException("Cannot pick your request for execution!");

		r.setExecutor(user);
		r.setStatus(RequestStatus.EXECUTING);

		publisher
			.publishMessageEvent(
				"Vaš zahtjev s opisom: " + r.getDescription() + " je upravo odabran za izvšavanje od strane korisnika: "
					+ user.getFirstName() + " " + user.getLastName() + " molimo prihvatite/odbijte pomoć navedenog korisnika.",
				r.getAuthor().getIdUser()
			);

		publisher
			.publishMessageEvent(
				"Upravo ste zahtjev s opisom: " + r.getDescription() + " autora: " + r.getAuthor().getFirstName() + " "
					+ r.getAuthor().getLastName() + " odabrali za izvšavanje. Molimo pričekajte da korisnik prihvati Vašu pomoć.",
				user.getIdUser()
			);

		r = requestRepo.save(r);

		r.setPhone(null);
		r.getAuthor().setLocation(null);

		return r;
	}

	@Override
	public Request confirmExecution(long id, boolean confirm, UserPrincipal principal) {
		User user = principal.getUser();
		Request r = fetch(id);

		if (!r.getStatus().equals(RequestStatus.EXECUTING))
			throw new IllegalActionException("Cannot confirm non executing request for execution!");

		if (!r.getAuthor().getIdUser().equals(user.getIdUser()))
			throw new IllegalActionException("Cannot confirm execution for someone else's request!");

		if (confirm) {
			publisher
				.publishMessageEvent(
					"Korisnik: " + user.getFirstName() + " " + user.getLastName() + "(mail-adresa: " + user.getEmail() + ", broj mobitela: "
						+ r.getPhone() + ")" + "je potvrdio Vas kao izvršitelja zahtjeva s opisom: " + r.getDescription(),
					r.getExecutor().getIdUser()
				);
			r.setConfirmed(true);
		} else {
			
			publisher
				.publishMessageEvent(
					"Korisnik: " + user.getFirstName() + " " + user.getLastName()
						+ " je odbio Vaš zahtjev za izvršavanjem zahtjeva s opisom: " + r.getDescription(),
					r.getExecutor().getIdUser()
				);
			
			r.setExecutor(null);
			r.setStatus(RequestStatus.ACTIVE);
			r.setConfirmed(false);
		}

		r = requestRepo.save(r);
	
		return r;
	}

	@Override
	public Request markExecuted(long idRequest, UserPrincipal principal) { // TODO ide ocjenjivanje
		User user = principal.getUser();
		Request r = fetch(idRequest);

		if (!user.getIdUser().equals(r.getAuthor().getIdUser()))
			throw new IllegalAccessException("Only author can mark request as executed!");

		if (!r.getStatus().equals(RequestStatus.EXECUTING) || !r.isConfirmed())
			throw new IllegalActionException("Cannot mark a request without an executor as executed!");
		
		if(!r.isConfirmed())
			throw new IllegalActionException("Cannot mark a non confirmed request as executed!");

		publisher
			.publishMessageEvent(
				"Upravo ste zahtjev s opisom: " + r.getDescription()
					+ " označili kao izvršen, molimo vas ocijenite izvršitelja prije nastavka korištenja aplikacije.",
				user.getIdUser()
			);

		publisher
			.publishMessageEvent(
				"Zahtjev s opisom: " + r.getDescription()
					+ " kojemu ste Vi izvršitelj je upravo označen kao izvršen, molimo vas ocijenite autora prije nastavka korištenja aplikacije.",
				r.getExecutor().getIdUser()
			);

		r.setStatus(RequestStatus.FINALIZED);
		r.setExecTstmp(LocalDateTime.now());

		return requestRepo.save(r);
	}

	@Override
	public Request backOff(long id, UserPrincipal principal) {
		User user = principal.getUser();
		Request req = fetch(id);

		if (req.getExecutor() == null || !user.getIdUser().equals(req.getExecutor().getIdUser()))
			throw new IllegalAccessException("Missing permission to change request status");

		if (!req.getStatus().equals(RequestStatus.EXECUTING)) throw new IllegalActionException("Cannot backoff from request!");

		publisher
			.publishMessageEvent(
				"Korisnik " + user.getFirstName() + " " + user.getLastName() + " je upravo odustao od izvšavanja vašeg zahtjeva s opisom: "
					+ req.getDescription() + " i zahtjev je ponovno aktiviran.",
				req.getAuthor().getIdUser()
			);

		req.setStatus(RequestStatus.ACTIVE);
		req.setExecutor(null);

		return requestRepo.save(req);
	}

	@Override
	public Request fetch(long requestId) {
		return requestRepo.findById(requestId).orElseThrow(() -> new EntityMissingException(Request.class, requestId));
	}

	@Override
	public Request getRequestbyId(long idRequest, UserPrincipal principal) {
		Request req = fetch(idRequest);
		User user = principal.getUser();

		if (
			user.getIdUser().equals(req.getAuthor().getIdUser())
				|| (req.getExecutor() != null && user.getIdUser().equals(req.getExecutor().getIdUser()))
				|| user.getEnumRoles().contains(Role.ROLE_ADMIN)
		) return req;

		if (req.getStatus().equals(RequestStatus.ACTIVE)) {
			hideInfo(req);
		} else {
			throw new IllegalAccessException(
				"User with id: " + user.getIdUser() + "is trying to access request with id: " + req.getIdRequest() + " without permission."
			);
		}

		return req;
	}

	@Override
	public Page<Request> getAllActiveRequests(Specification<Request> specs, Pageable pageable, Double radius, UserPrincipal principal) {
		if(radius == null) radius = 0.0;
		
		specs = specs.and(ReqSpecs.getByStatusOrderByLocation(RequestStatus.ACTIVE, principal.getUser().getLocation(), radius));
		specs = specs.and(ReqSpecs.<Request, User>atributeEqualNotEqual("author", principal.getUser(), false)); // author != prijavljeni korisnik
		
		Page<Request> page = requestRepo.findAll(specs, pageable);
		page.forEach(r -> {r.setPhone(null); r.getAuthor().setLocation(null);});
		
		return page;
	}

	@Override
	public Map<String, CollectionModel<RequestDTO>> getAuthoredRequests(long userID, UserPrincipal principal) {
		User user = principal.getUser(); // principal exists in the context because user has to be authenticated before accessing this point
		if (!user.getIdUser().equals(userID)) throw new IllegalAccessException("ID of logged in user is not the same as given userID!");

		Specification<Request> spec = ReqSpecs.<Request, User>atributeEqualNotEqual("author", user, true); // author = prijavljenom korisniku

		List<Request> active = requestRepo.findAll(spec.and(ReqSpecs.statusEqual(RequestStatus.ACTIVE)));
		List<Request> finalized = requestRepo.findAll(spec.and(ReqSpecs.statusEqual(RequestStatus.FINALIZED)));
		List<Request> blocked = requestRepo.findAll(spec.and(ReqSpecs.statusEqual(RequestStatus.BLOCKED)));
		List<Request> executing = requestRepo.findAll(spec.and(ReqSpecs.statusEqual(RequestStatus.EXECUTING)));

		Map<String, CollectionModel<RequestDTO>> map = new HashMap<>();

		map.put(RequestStatus.ACTIVE.toString(), requestDTOAssembler.toCollectionModel(active));
		map.put(RequestStatus.FINALIZED.toString(), requestDTOAssembler.toCollectionModel(finalized));
		map.put(RequestStatus.BLOCKED.toString(), requestDTOAssembler.toCollectionModel(blocked));
		map.put(RequestStatus.EXECUTING.toString(), requestDTOAssembler.toCollectionModel(executing));

		return map;
	}

	@Override
	public Map<String, CollectionModel<RequestDTO>> getRequestsByExecutor(Long userId, UserPrincipal principal) {
		User user = principal.getUser(); // principal exists in the context because user has to be authenticated before accessing this point
		if (!user.getIdUser().equals(userId)) throw new IllegalAccessException("ID of logged in user is not the same as given userID!");

		Specification<Request> spec = ReqSpecs.<Request, User>atributeEqualNotEqual("executor", user, true); // executor = prijavljeni korisnik

		List<Request> finalized = requestRepo.findAll(spec.and(ReqSpecs.statusEqual(RequestStatus.FINALIZED)));
		List<Request> blocked = requestRepo.findAll(spec.and(ReqSpecs.statusEqual(RequestStatus.BLOCKED)));
		List<Request> executing = requestRepo.findAll(spec.and(ReqSpecs.statusEqual(RequestStatus.EXECUTING)));

		Map<String, CollectionModel<RequestDTO>> map = new HashMap<>();

		map.put(RequestStatus.FINALIZED.toString(), requestDTOAssembler.toCollectionModel(finalized));
		map.put(RequestStatus.BLOCKED.toString(), requestDTOAssembler.toCollectionModel(blocked));
		map.put(RequestStatus.EXECUTING.toString(), requestDTOAssembler.toCollectionModel(executing));

		return map;
	}

	private void hideInfo(Request req) { req.setPhone(null); }

//	public static double calculateDistanceInKM(Location l1, Location l2) {
//		double lat1 = l1.getLatitude().doubleValue(), lon1 = l1.getLongitude().doubleValue();
//		double lat2 = l2.getLatitude().doubleValue(), lon2 = l2.getLongitude().doubleValue();
//
//		//Haversine formula 
//		// distance between latitudes and longitudes
//		double dLat = Math.toRadians(lat2 - lat1);
//		double dLon = Math.toRadians(lon2 - lon1);
//
//		// convert to radians 
//		lat1 = Math.toRadians(lat1);
//		lat2 = Math.toRadians(lat2);
//
//		// apply formulae 
//		double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
//		double rad = 6371; //radius zemlje
//		double c = 2 * Math.asin(Math.sqrt(a));
//		return rad * c;
//	}

	private Location resolveLocation(LocationDTO dto) {
		if (dto != null) { // ako je dana lokacija onda provjeri postoji li vec spremljena pa ju dodaj u req ili... ako ne onda spremi i dodaj u req 

			Location loc = locationService.findByLatitudeAndLongitude(dto.getLatitude(), dto.getLongitude());

			if (loc == null) loc = locationService.save(dto);

			return loc;
		}

		return null;
	}

}