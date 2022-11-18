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
import NULL.DTPomoziMi.exception.IllegalAccessException;
import NULL.DTPomoziMi.exception.IllegalActionException;
import NULL.DTPomoziMi.model.RequestStatus;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.util.UserPrincipalGetter;

@SpringBootTest
@ActiveProfiles("dev")
public class DeleteRequestTest {

	@Autowired
	private RequestServiceImpl service;

	//brisanje vlastitog aktivnog zahtjeva
	@Test
	@WithUserDetails(value = "matea.lipovac@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test1() {
		UserPrincipal principal = UserPrincipalGetter.getPrincipal();

		assertEquals(RequestStatus.DELETED, service.deleteRequest(35, principal).getStatus());
		assertThrows(EntityMissingException.class, () -> { service.fetch(35); });
	}

	//admin brise zahtjev
	@Test
	@WithUserDetails(value = "jan.rocek@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test2() {
		UserPrincipal principal = UserPrincipalGetter.getPrincipal();

		assertEquals(RequestStatus.DELETED, service.deleteRequest(11, principal).getStatus());
		assertThrows(EntityMissingException.class, () -> { service.fetch(11); });
	}

	//nevlasteno brisanje tudeg zahtjeva
	@Test
	@WithUserDetails(value = "robert.dakovic@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test3() {
		UserPrincipal principal = UserPrincipalGetter.getPrincipal();
		assertThrows(IllegalAccessException.class, () -> service.deleteRequest(25, principal).getStatus());
	}

	// zahtjev već ima izvrsitelja
	@Test
	@WithUserDetails(value = "matea.lipovac@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test4() {
		UserPrincipal principal = UserPrincipalGetter.getPrincipal();

		assertThrows(IllegalActionException.class, () -> service.deleteRequest(34, principal).getStatus());
	}

	//neprijavljen korisnik
	@Test
	public void test5() {
		UserPrincipal principal = UserPrincipalGetter.getPrincipal();
		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.deleteRequest(6, principal).getStatus());
	}
}
