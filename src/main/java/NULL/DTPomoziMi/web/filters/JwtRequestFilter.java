package NULL.DTPomoziMi.web.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import NULL.DTPomoziMi.jwt.JwtUtil;
import NULL.DTPomoziMi.properties.JwtConstants;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.util.CookieUtil;
import NULL.DTPomoziMi.util.UserPrincipalGetter;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
	) throws ServletException, IOException {
		logger.debug("JWT filtering");

		String token = CookieUtil.getValue(request, JwtConstants.JWT_COOKIE_NAME);
		String refreshToken = CookieUtil.getValue(request, JwtConstants.JWT_REFRESH_COOKIE_NAME);

		String emailFromToken = null;
		String emailFromRefreshToken = null;

		try {
			logger.debug("Extracting email from token");

			if (token != null && !token.isBlank()) emailFromToken = jwtUtil.extractUsername(token);

		} catch (SignatureException e) {
			logger.debug("Deleting cookies because of signature exception: {}", e.getMessage());

			deleteCookies(response);

		} catch (JwtException e) {
			try {
				logger.debug("Extracting email from refresh token: {}", e.getMessage());

				if (refreshToken != null && !refreshToken.isBlank()) emailFromRefreshToken = jwtUtil.extractUsername(refreshToken);

			} catch (JwtException ex) {
				deleteCookies(response);
			}
		}

		boolean valid = false;
		if (emailFromToken != null && UserPrincipalGetter.getPrincipal() == null) {
			logger.debug("Validating token");
			if (jwtUtil.validateToken(token)) {

				setAuthAndGenerateToken(emailFromToken, false, null);
				valid = true;
			}
		}
		else if (!valid && emailFromRefreshToken != null && UserPrincipalGetter.getPrincipal() == null) {
			logger.debug("Validating refresh token");

			if (jwtUtil.validateRefreshToken(refreshToken)) {
				setAuthAndGenerateToken(emailFromRefreshToken, true, response);

				Map<String, Object> claims = new HashMap<>();
				claims.put(JwtUtil.CLAIM_ROLES, jwtUtil.extractRoles(refreshToken));

			} else {
				logger.debug("Deleting cookies because of invalid refresh token");

				deleteCookies(response);
			}
		}else {
			deleteCookies(response);
		}

		filterChain.doFilter(request, response);
	}

	private void setAuthAndGenerateToken(String email, boolean generate, HttpServletResponse response) {
		logger.debug("Setting auth");

		UserDetails user = null;
		try {
			user = userDetailsService.loadUserByUsername(email);
		} catch (UsernameNotFoundException e) {
			logger.debug(e.getMessage());
		}

		if (user != null) {
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
				= new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

			if (generate) { generateToken(response, user); }
		}
	}

	private void generateToken(HttpServletResponse response, UserDetails user) {
		String newtoken = jwtUtil.generateToken((UserPrincipal) user);
		CookieUtil.create(response, JwtConstants.JWT_COOKIE_NAME, newtoken, false, -1, false);
	}

	private void deleteCookies(HttpServletResponse response) {
		CookieUtil.clear(response, JwtConstants.JWT_REFRESH_COOKIE_NAME);
		CookieUtil.clear(response, JwtConstants.JWT_COOKIE_NAME);
	}
}
