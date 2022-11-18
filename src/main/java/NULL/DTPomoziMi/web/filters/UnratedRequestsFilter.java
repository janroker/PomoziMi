package NULL.DTPomoziMi.web.filters;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import NULL.DTPomoziMi.repository.RequestRepo;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.util.UserPrincipalGetter;

@Component
public class UnratedRequestsFilter extends OncePerRequestFilter {

	private List<AntPathRequestMatcher> matchers;

	@Autowired
	private RequestRepo repo;
	
	@Autowired
	private ObjectMapper objectMapper;

	public UnratedRequestsFilter() {
		matchers = new ArrayList<>();
		matchers.add(new AntPathRequestMatcher("/api/requests/**", HttpMethod.POST.toString()));
		matchers.add(new AntPathRequestMatcher("/api/requests/**", HttpMethod.PUT.toString()));
		matchers.add(new AntPathRequestMatcher("/api/requests/**", HttpMethod.PATCH.toString()));
		matchers.add(new AntPathRequestMatcher("/api/requests/**", HttpMethod.DELETE.toString()));
		matchers.add(new AntPathRequestMatcher("/api/users/**", HttpMethod.POST.toString()));
		matchers.add(new AntPathRequestMatcher("/api/users/**", HttpMethod.PUT.toString()));
		matchers.add(new AntPathRequestMatcher("/api/users/**", HttpMethod.PATCH.toString()));
		matchers.add(new AntPathRequestMatcher("/api/users/**", HttpMethod.DELETE.toString()));
		matchers.add(new AntPathRequestMatcher("/api/candidacies/**", HttpMethod.POST.toString()));
		matchers.add(new AntPathRequestMatcher("/api/candidacies/**", HttpMethod.PUT.toString()));
		matchers.add(new AntPathRequestMatcher("/api/candidacies/**", HttpMethod.PATCH.toString()));
		matchers.add(new AntPathRequestMatcher("/api/candidacies/**", HttpMethod.DELETE.toString()));
	}

	private boolean match(HttpServletRequest request) {
		for (AntPathRequestMatcher m : matchers) { if (m.matches(request)) return true; }

		return false;
	}
	
	@Override
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
	) throws ServletException, IOException {
		UserPrincipal principal = UserPrincipalGetter.getPrincipal();
		
		if (principal != null && match(request)) {
			boolean exists = repo.existsUnratedFinalizedRequest(principal.getUser().getIdUser());
			
			if(exists) {
				Map<String, Object> errDetails = new HashMap<>();
				errDetails.put("message", "User has unrated finalized requests");
				errDetails.put("status", "Unrated finalized requests");
				errDetails.put("code", 1001);
				
				response.setStatus(HttpStatus.FORBIDDEN.value()); // 403
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
				
				try {
					OutputStream os = response.getOutputStream();
					os.write(objectMapper.writeValueAsBytes(errDetails));
					os.flush();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
				filterChain.doFilter(request, response);
			
		} else
			filterChain.doFilter(request, response);
	}

}
