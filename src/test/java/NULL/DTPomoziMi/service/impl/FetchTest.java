package NULL.DTPomoziMi.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

import NULL.DTPomoziMi.exception.EntityMissingException;
import NULL.DTPomoziMi.repository.UserRepo;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.service.UserService;
import NULL.DTPomoziMi.util.UserPrincipalGetter;

@SpringBootTest
@ActiveProfiles("dev")
public class FetchTest {

	@Autowired
	private UserService service;

	@Autowired
	private UserRepo userRepo;

	//dohvat samog sebe
	@Test
	@WithUserDetails(value = "jan.rocek@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test1() {
		UserPrincipal principal = UserPrincipalGetter.getPrincipal();
		assertEquals(principal.getUser(), service.fetch(3));
	}

	//dohvat postojeceg korisnika
	@Test
	@WithUserDetails(value = "jan.rocek@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test2() { assertEquals(userRepo.findByEmail("matea.lipovac@gmail.com"), service.fetch(12)); }

	//dohvat nepostojeceg korisnika
	@Test
	@WithUserDetails(value = "jan.rocek@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test3() { assertThrows(EntityMissingException.class, () -> service.fetch(90)); }

	//neprijavljeni korisnik
	@Test
	public void test4() { assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.fetch(12)); }
}
