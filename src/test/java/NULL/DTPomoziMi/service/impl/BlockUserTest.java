package NULL.DTPomoziMi.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

import NULL.DTPomoziMi.service.UserService;

@SpringBootTest
@ActiveProfiles("dev")
public class BlockUserTest {
	@Autowired
	private UserService service;

	// korisnik sa id=12 postoji u bazi jer se prilikom pokretanja aplikacije nad bazom izvode upiti 
	// uneseni u data.sql file ==> INSERT INTO korisnik (...) VALUES (12, ...)

	//admin blokira korisnika
	@Test
	@WithUserDetails(value = "jan.rocek@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test1() { assertEquals(false, service.blockUnblockUser(12, false).getEnabled()); }

	//korisnik koji nije admin pokusava blokirati drugog korisnika
	@Test
	@WithUserDetails(value = "robert.dakovic@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test2() { assertThrows(AccessDeniedException.class, () -> service.blockUnblockUser(12, false)); }

	//neprijavljen korisnik
	@Test
	public void test3() { assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.blockUnblockUser(12, false)); }

}
