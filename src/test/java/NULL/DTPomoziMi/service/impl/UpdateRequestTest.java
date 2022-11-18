package NULL.DTPomoziMi.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

import NULL.DTPomoziMi.exception.IllegalAccessException;
import NULL.DTPomoziMi.model.Request;
import NULL.DTPomoziMi.model.RequestStatus;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.util.UserPrincipalGetter;
import NULL.DTPomoziMi.web.DTO.RequestDTO;
import NULL.DTPomoziMi.web.assemblers.RequestDTOAssembler;

@SpringBootTest
@ActiveProfiles("dev")
public class UpdateRequestTest {
	@Autowired
	private RequestServiceImpl service;

	@Autowired
	private RequestDTOAssembler assembler;

	private RequestDTO request;

	public void setup() { request = assembler.toModel(service.fetch(27)); }

	//uspjsan update
	@Test
	@WithUserDetails(value = "dominik.curic@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test1() {
		setup();
		UserPrincipal principal = UserPrincipalGetter.getPrincipal();

		final String phone = "091";
		final LocalDateTime ldt = LocalDateTime.of(2022, 1, 1, 1, 1, 1);
		final String desc = "abc";

		request.setDescription(desc);
		request.setPhone(phone);
		request.setTstmp(ldt);
		request.setLocation(null);
		request.setAuthor(null);
		request.setStatus(RequestStatus.FINALIZED);

		service.updateRequest(27, request, principal);
		Request updated = service.fetch(27);

		assertEquals(phone, updated.getPhone());
		assertEquals(ldt, updated.getTstmp());
		assertEquals(desc, updated.getDescription());
		assertNull(updated.getLocation());
		assertNotEquals(123L, updated.getIdRequest());
		assertNotNull(updated.getAuthor());
		assertNotEquals(RequestStatus.FINALIZED, updated.getIdRequest());

	}

	//id requesta i id reguestDTOa razliciti
	@Test
	@WithUserDetails(value = "dominik.curic@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test2() {
		setup();
		UserPrincipal principal = UserPrincipalGetter.getPrincipal();
		assertThrows(IllegalArgumentException.class, () -> service.updateRequest(3, request, principal));
	}

	//nije autor
	@Test
	@WithUserDetails(value = "jan.rocek@gmail.com", userDetailsServiceBeanName = "myUserDetailsService")
	public void test3() {
		setup();
		UserPrincipal principal = UserPrincipalGetter.getPrincipal();
		assertThrows(IllegalAccessException.class, () -> service.updateRequest(27, request, principal));
	}

	//neprijavljen korisnik
	@Test
	public void test4() {
		UserPrincipal principal = UserPrincipalGetter.getPrincipal();
		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.updateRequest(19, request, principal));
	}
}
