package NULL.DTPomoziMi.web.controller;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import NULL.DTPomoziMi.exception.UserAlreadyExistException;
import NULL.DTPomoziMi.jwt.JwtUtil;
import NULL.DTPomoziMi.properties.JwtConstants;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.service.TokenService;
import NULL.DTPomoziMi.service.UserService;
import NULL.DTPomoziMi.util.CookieUtil;
import NULL.DTPomoziMi.web.DTO.UserRegisterDTO;
import io.jsonwebtoken.ExpiredJwtException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserService userService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private JwtUtil jwtUtil;

	@Resource(name = "myUserDetailsService")
	private UserDetailsService userDetailsService;

	@PostMapping(value = "/registration", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> register(@Valid UserRegisterDTO user, BindingResult bindingResult, HttpServletRequest request) {

		if (bindingResult.hasErrors()) {
			List<ObjectError> errors = bindingResult.getAllErrors();
			logger.debug("Binding errors {}", errors);

			return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		}

		logger.debug("Registering user account with info: {}", user);

		try {
			userService.registerUser(user);
		} catch (DataIntegrityViolationException e) {
			throw new UserAlreadyExistException(
				messageSource.getMessage("auth.registration.exists", new Object[] { user.getEmail() }, request.getLocale())
			);
		}

		return ResponseEntity.ok(messageSource.getMessage("auth.registration.success", null, request.getLocale()));
	}

	@PostMapping(value = "/login", produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<?> login(
		@RequestParam("email") String email, @RequestParam("password") String password, HttpServletResponse response,
		HttpServletRequest request
	) throws Exception {

		logger.debug("Login with username: {}", email);
		email = email == null ? null : email.trim();

		Authentication auth;
		try {
			auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		} catch (AuthenticationException e) {
			logger.debug("Incorect username: {} or password", email);
			return new ResponseEntity<String>(
				messageSource.getMessage("userDetails.service.notFound", null, request.getLocale()), HttpStatus.UNAUTHORIZED
			);
		}

		UserPrincipal user = (UserPrincipal) auth.getPrincipal();

		String refreshToken = null;
		if (user.getUser().getToken() == null) // nemoj odlogirat na drugim uredajima
		{
			refreshToken = jwtUtil.generateRefreshToken(user);
			tokenService.updateToken(refreshToken, user.getUsername());
		} else {
			refreshToken = user.getUser().getToken();
			
			boolean shouldUpdate = false;
			try {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.HOUR, 1);
				
				if(jwtUtil.extractExpiration(refreshToken).before(cal.getTime())) {
					refreshToken = jwtUtil.generateRefreshToken(user);
					shouldUpdate = true;
				}
			}catch(ExpiredJwtException e) {
				refreshToken = jwtUtil.generateRefreshToken(user);
				shouldUpdate = true;
			}
			
			if(shouldUpdate) 
				tokenService.updateToken(refreshToken, user.getUsername());
		}

		String token = jwtUtil.generateToken(user);

		CookieUtil.create(response, JwtConstants.JWT_COOKIE_NAME, token, false, -1, false);
		CookieUtil.create(response, JwtConstants.JWT_REFRESH_COOKIE_NAME, refreshToken, false, -1, true);

		return ResponseEntity.ok(messageSource.getMessage("auth.login.sucess", null, request.getLocale()));
	}

}
