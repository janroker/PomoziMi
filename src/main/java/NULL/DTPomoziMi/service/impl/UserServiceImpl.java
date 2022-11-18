package NULL.DTPomoziMi.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import NULL.DTPomoziMi.exception.EntityMissingException;
import NULL.DTPomoziMi.exception.IllegalAccessException;
import NULL.DTPomoziMi.exception.IllegalActionException;
import NULL.DTPomoziMi.model.Candidacy;
import NULL.DTPomoziMi.model.Location;
import NULL.DTPomoziMi.model.Rating;
import NULL.DTPomoziMi.model.RequestStatus;
import NULL.DTPomoziMi.model.Role;
import NULL.DTPomoziMi.model.RoleEntity;
import NULL.DTPomoziMi.model.User;
import NULL.DTPomoziMi.model.specification.CandidacySpecs;
import NULL.DTPomoziMi.repository.CandidacyRepo;
import NULL.DTPomoziMi.repository.RoleRepo;
import NULL.DTPomoziMi.repository.UserRepo;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.service.LocationService;
import NULL.DTPomoziMi.service.UserService;
import NULL.DTPomoziMi.web.DTO.LocationDTO;
import NULL.DTPomoziMi.web.DTO.RatingDTO;
import NULL.DTPomoziMi.web.DTO.UserDTO;
import NULL.DTPomoziMi.web.DTO.UserRegisterDTO;
import NULL.DTPomoziMi.web.assemblers.RatingDTOAssembler;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

	private static final String numExecutedRequests = "numExecutedR";
	private static final String numAuthoredRequests = "numAuthoredR";
	private static final String numFinalizedAuthoredRequests = "numFinalizedAR";
	private static final String numBlockedRequests = "numBlockedR";
	private static final String AVG_GRADE = "avgGrade";
	private static final String RANK = "rank";

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private LocationService locationService;

	@Autowired
	private CandidacyRepo CandidacyRepo;

	@Autowired
	private RatingDTOAssembler ratingDTOAssembler;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public User registerUser(UserRegisterDTO user) {
		User newUser = new User();

		newUser.setEmail(user.getEmail());
		newUser.setFirstName(user.getFirstName());
		newUser.setLastName(user.getLastName());
		newUser.setEnabled(true);
		newUser.setPassword(passwordEncoder.encode(user.getPassword()));

		if (user.getLatitude() != null && user.getLongitude() != null) {
			LocationDTO location
				= new LocationDTO(null, user.getAdress(), user.getState(), user.getTown(), user.getLongitude(), user.getLatitude());
			Location loc = resolveLocation(location);
			newUser.setLocation(loc);
		}

		RoleEntity roleEntity = roleRepo.findByRole(Role.ROLE_USER);
		if (roleEntity == null) {
			roleEntity = new RoleEntity();
			roleEntity.setRole(Role.ROLE_USER);
			roleEntity = roleRepo.save(roleEntity);
		}

		newUser.addRoleEntity(roleEntity);

		return userRepo.save(newUser);
	}

	@Override
	public User getUserByEmail(String email) {
		email = email == null ? null : email.trim();

		return userRepo.findByEmail(email);
	}

	@Override
	@PreAuthorize("isAuthenticated()")
	public User fetch(long id) {

		return userRepo.findById(id).orElseThrow(() -> new EntityMissingException(User.class, id));
	}

	@Override
	@PreAuthorize("isAuthenticated()")
	public User getUserByID(long ID, UserPrincipal principal) {

		User user = fetch(ID);
		if (!principal.getUser().getEnumRoles().contains(Role.ROLE_ADMIN) && !user.getEnabled())
			throw new IllegalActionException("Cannot access blocked user's profile");

		if (!principal.getUser().getIdUser().equals(ID) && !principal.getUser().getEnumRoles().contains(Role.ROLE_ADMIN))
			user.setLocation(null);

		return user;
	}

	@Override
	@PreAuthorize("isAuthenticated()")
	public Page<User> findUsers(Pageable pageable, Specification<User> specification, UserPrincipal principal) {
		User user = principal.getUser();

		if (!user.getEnumRoles().contains(Role.ROLE_ADMIN)) {
			specification = specification.and((root, query, cb) -> { return cb.equal(root.<Boolean>get("enabled"), true); });
		}

		Page<User> page = userRepo.findAll(specification, pageable);
		if (!user.getEnumRoles().contains(Role.ROLE_ADMIN)) page.forEach(u -> u.setLocation(null));

		return page;
	}

	@Override
	@Secured("ROLE_ADMIN")
	public User blockUnblockUser(long userID, boolean enabled) {
		User user = fetch(userID);
		user.setEnabled(enabled);
		userRepo.save(user);
		return user;
	}

	@Override
	@PreAuthorize("isAuthenticated()")
	public User deleteUser(long idUser, UserPrincipal principal) {
		if (!principal.getUser().getIdUser().equals(idUser)) throw new IllegalAccessException("Missing permission to delete user");

		User user = fetch(idUser);
		userRepo.delete(user);
		return user;
	}

	@Override
	@Transactional
	@PreAuthorize("isAuthenticated()")
	public Map<String, Object> getStatistics(long idUser) {
		Map<String, Object> statistics = new HashMap<>(); // TODO tko moze vidjet?

		User user = fetch(idUser);
		statistics
			.put(numExecutedRequests, user.getExecutedReqs().stream().filter(r -> r.getStatus().equals(RequestStatus.FINALIZED)).count());
		statistics.put(numAuthoredRequests, user.getAuthoredReqs().size());
		statistics
			.put(
				numFinalizedAuthoredRequests,
				user.getAuthoredReqs().stream().filter(r -> r.getStatus().equals(RequestStatus.FINALIZED)).count()
			);
		statistics
			.put(numBlockedRequests, user.getAuthoredReqs().stream().filter(r -> r.getStatus().equals(RequestStatus.BLOCKED)).count());

		List<Candidacy> candidacies = CandidacyRepo.findAll(CandidacySpecs.yearEqual(LocalDate.now().getYear()));
		Integer rank = calculateRank(candidacies, user);
		statistics.put(RANK, rank);

		double avgGrade = user.getRatedBy().stream().mapToInt(Rating::getRate).average().orElse(-1);
		statistics.put(AVG_GRADE, (avgGrade == -1 ? null : avgGrade));
		return statistics;
	}

	private Integer calculateRank(List<Candidacy> candidacies, User user) {
		if (candidacies == null || candidacies.size() == 0) return null;

		int year = LocalDate.now().getYear();
		if (candidacies.size() > 1) throw new RuntimeException("Too many candidacies for year: " + year);

		Set<User> users = candidacies.get(0).getUsers();
		if (!users.contains(user)) return null;

		Comparator<User> comp = (u1, u2) -> {
			Double avgGrade1 = u1.getRatedBy().stream().mapToInt(r -> r.getRate()).average().orElse(-1);
			Double avgGrade2 = u2.getRatedBy().stream().mapToInt(r -> r.getRate()).average().orElse(-1);

			Long numExecuted1 = u1
				.getExecutedReqs()
				.stream()
				.filter(r -> r.getStatus().equals(RequestStatus.FINALIZED) && r.getExecTstmp().getYear() == year)
				.count();
			Long numExecuted2 = u2
				.getExecutedReqs()
				.stream()
				.filter(r -> r.getStatus().equals(RequestStatus.FINALIZED) && r.getExecTstmp().getYear() == year)
				.count();

			if (avgGrade1 == -1 && avgGrade2 == -1) return 0;
			if (avgGrade1 == -1 && avgGrade2 != -1) return -1;
			if (avgGrade1 != -1 && avgGrade2 == -1) return 1;

			if (numExecuted1 == 0 && numExecuted2 == 0) return 0;

			long sum = numExecuted1 + numExecuted2;
			double weighted1 = avgGrade1 * (((double) numExecuted1) / sum);
			double weighted2 = avgGrade2 * (((double) numExecuted2) / sum);

			if (weighted1 > weighted2) return 1;
			if (weighted1 < weighted2) return -1;

			return 0;
		};

		List<User> list = new ArrayList<>(users);
		list.sort(comp.reversed());

		return list.indexOf(user) + 1;
	}

	@Override
	@PreAuthorize("isAuthenticated()")
	@Transactional
	public User updateUser(UserDTO userDTO, long id, UserPrincipal principal) {
		User user = principal.getUser();

		if (!userDTO.getIdUser().equals(id)) throw new IllegalArgumentException("User id must be preserved!");

		if (!user.getIdUser().equals(id)) throw new IllegalAccessException("Missing permission to edit user's information!");

		modelMapper.map(userDTO, user);

		LocationDTO location = userDTO.getLocation();
		Location loc = resolveLocation(location);
		user.setLocation(loc);
		user.setEmail(userDTO.getEmail());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setPicture(userDTO.getPicture());

		return userRepo.save(user);
	}

	@Transactional
	@Override
	@PreAuthorize("isAuthenticated()")
	public List<RatingDTO> getChainOfTrust(long id, UserPrincipal principal) {
		User user = fetch(principal.getUser().getIdUser()); // TODO ovo treba debelo testirat
		//da je korisnik kojeg ste vi visoko ocjenili ocijenio korisnika čiji profil gledate.

		List<RatingDTO> dtos =  user
			.getRatedOthers()
			.stream()
			.filter(r -> r.getRate() > 3)
			.flatMap(r -> r.getRated().getRatedOthers().stream().filter(o -> o.getRated().getIdUser().equals(id)))
			.distinct()
			.map(r -> ratingDTOAssembler.toModel(r))
			.collect(Collectors.toList());
		
		dtos.forEach(r -> {
			r.getRator().setLocation(null);
			r.getRated().setLocation(null);

			if (r.getRequest() != null) {
				r.getRequest().setAuthor(null);
				r.getRequest().setExecutor(null);
				r.getRequest().setLocation(null);
				r.getRequest().setPhone(null);
				r.getRequest().setRatings(null);
			}
		});
		
		return dtos;
	}

	private Location resolveLocation(LocationDTO dto) {
		if (dto != null) { // ako je dana lokacija onda provjeri postoji li vec spremljena pa ju dodaj u req ili... ako ne onda spremi i dodaj u req 

			Location loc = locationService.findByLatitudeAndLongitude(dto.getLatitude(), dto.getLongitude());

			if (loc == null) loc = locationService.save(dto);

			return loc;

		}

		return null;
	}
}
