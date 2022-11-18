package NULL.DTPomoziMi.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

import NULL.DTPomoziMi.exception.EntityMissingException;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.service.UserService;
import NULL.DTPomoziMi.util.UserPrincipalGetter;

@SpringBootTest
@ActiveProfiles("dev")
class GetUserByIdTest {

	@Autowired
	private UserService service;

	private UserPrincipal principal;

	//dobar id
	@Test
	@WithUserDetails(value = "jan.rocek@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test1() {
		principal = UserPrincipalGetter.getPrincipal();
		assertEquals("jan.rocek@gmail.com", service.getUserByID(3, principal).getEmail());
	}

	//nepostojeci id
	@Test
	@WithUserDetails(value = "jan.rocek@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test2() {
		principal = UserPrincipalGetter.getPrincipal();
		assertThrows(EntityMissingException.class, () -> service.getUserByID(110l, principal));
	}

	// ne-adminski dohvat tuđeg profila rezultira skrivanjem lokacije
	@Test
	@WithUserDetails(value = "matea.lipovac@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test3() { principal = UserPrincipalGetter.getPrincipal(); assertNull(service.getUserByID(7, principal).getLocation()); }

	//neprijavljen korisnik
	@Test
	public void test4() { assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.getUserByID(110l, principal)); }

}
