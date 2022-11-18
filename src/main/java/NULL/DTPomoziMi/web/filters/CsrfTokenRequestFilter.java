package NULL.DTPomoziMi.web.filters;

import NULL.DTPomoziMi.util.CookieUtil;
import NULL.DTPomoziMi.util.MutableHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CsrfTokenRequestFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
	) throws ServletException, IOException {
		MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);

		String token = CookieUtil.getValue(request, "X-CSRF-COOKIE");// TODO JwtConstants

		if (token != null && !token.isBlank()) {
			// TODO JwtConstants
			mutableRequest.putHeader("X-CSRF-TOKEN", token);
		}

		filterChain.doFilter(mutableRequest, response);
	}
}
