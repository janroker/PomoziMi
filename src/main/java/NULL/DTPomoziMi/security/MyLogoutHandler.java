package NULL.DTPomoziMi.security;

import NULL.DTPomoziMi.jwt.JwtUtil;
import NULL.DTPomoziMi.properties.JwtConstants;
import NULL.DTPomoziMi.service.TokenService;
import NULL.DTPomoziMi.util.CookieUtil;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyLogoutHandler implements LogoutSuccessHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private TokenService tokenService;

	@Override
	public void onLogoutSuccess(
		HttpServletRequest request, HttpServletResponse response, Authentication authentication
	) throws IOException, ServletException {
		String token = CookieUtil.getValue(request, JwtConstants.JWT_COOKIE_NAME);
		String username = null;
		try {
			username = jwtUtil.extractUsername(token);
			tokenService.updateToken(null, username);

		} catch (JwtException e) {
			logger.debug("Problems while deleting token: {} for username: {}", token, username);
		}
		CookieUtil.clear(response, JwtConstants.JWT_COOKIE_NAME);
		CookieUtil.clear(response, JwtConstants.JWT_REFRESH_COOKIE_NAME);
	}
}
