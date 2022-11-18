package NULL.DTPomoziMi.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

import NULL.DTPomoziMi.exception.IllegalActionException;
import NULL.DTPomoziMi.model.RequestStatus;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.service.RequestService;
import NULL.DTPomoziMi.util.UserPrincipalGetter;

@SpringBootTest
@ActiveProfiles("dev")

public class PickForExecutionTest {
	@Autowired
	private RequestService service;

	//uspjesno odabrano izvrsavanje
	@Test
	@WithUserDetails(value = "iva.boksic@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test1() {
		UserPrincipal principal = UserPrincipalGetter.getPrincipal();
		service.pickForExecution(31, principal).getStatus();
		assertEquals(RequestStatus.EXECUTING, service.fetch(31).getStatus());
	}

	//neaktivan zahtjev
	@Test
	@WithUserDetails(value = "iva.boksic@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test2() {
		UserPrincipal principal = UserPrincipalGetter.getPrincipal();
		assertThrows(IllegalActionException.class, () -> service.pickForExecution(24, principal));
	}

	//neprijavljen korisnik
	@Test
	public void test3() {
		UserPrincipal principal = UserPrincipalGetter.getPrincipal();
		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.deleteRequest(14, principal).getStatus());
	}
}
