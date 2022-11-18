package NULL.DTPomoziMi.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

import NULL.DTPomoziMi.exception.IllegalAccessException;
import NULL.DTPomoziMi.model.User;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.service.UserService;
import NULL.DTPomoziMi.util.UserPrincipalGetter;
import NULL.DTPomoziMi.web.DTO.UserDTO;
import NULL.DTPomoziMi.web.assemblers.UserDTOModelAssembler;

@SpringBootTest
@ActiveProfiles("dev")
public class UpdateUserTest {

	@Autowired
	private UserService service;

	@Autowired
	private UserDTOModelAssembler assembler;

	private UserPrincipal principal;

	private UserDTO userDTO;

	public void setup() { userDTO = assembler.toModel(service.fetch(11)); }

	//korisnik ima pristup uredivanju profila
	@Test
	@WithUserDetails(value = "marija.orec@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test1() {
		setup();
		principal = UserPrincipalGetter.getPrincipal();

		userDTO.setEmail("bananko@gmail.com");
		userDTO.setFirstName("Janko");
		userDTO.setLastName("bananko");
		userDTO.setPicture("aaa");

		service.updateUser(userDTO, 11, principal);

		User user = service.fetch(11);

		assertEquals(user.getEmail(), userDTO.getEmail());
		assertEquals(user.getFirstName(), userDTO.getFirstName());
		assertEquals(user.getLastName(), userDTO.getLastName());
		assertEquals(user.getPicture(), userDTO.getPicture());
		assertEquals(user.getIdUser(), userDTO.getIdUser());
	}

	// korisnik je postavio već postojeći email
	@Test
	@WithUserDetails(value = "bananko@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test2() {
		setup();
		principal = UserPrincipalGetter.getPrincipal();

		userDTO.setEmail("matea.lipovac@gmail.com");
		assertThrows(DataIntegrityViolationException.class, () -> service.updateUser(userDTO, 11, principal));
	}

	//korisnik nema pristup uredivanju profila
	@Test
	@WithUserDetails(value = "matea.lipovac@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test3() {
		setup();
		principal = UserPrincipalGetter.getPrincipal();
		assertThrows(IllegalAccessException.class, () -> service.updateUser(userDTO, 11, principal));
	}

	//userDTO i id se ne poklapaju
	@Test
	@WithUserDetails(value = "bananko@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test4() {
		setup();
		principal = UserPrincipalGetter.getPrincipal();
		assertThrows(IllegalArgumentException.class, () -> service.updateUser(userDTO, 3, principal));
	}

	//neprijavljen korisnik
	@Test
	public void test5() {
		principal = UserPrincipalGetter.getPrincipal();
		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.updateUser(userDTO, 11, principal));
	}
}
