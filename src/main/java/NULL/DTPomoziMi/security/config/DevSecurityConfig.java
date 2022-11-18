package NULL.DTPomoziMi.security.config;

import NULL.DTPomoziMi.web.filters.CsrfTokenRequestFilter;
import NULL.DTPomoziMi.web.filters.JwtRequestFilter;
import NULL.DTPomoziMi.web.filters.UnratedRequestsFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;

@Profile("dev")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class DevSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService myUserDetailsService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	private CsrfTokenRequestFilter csrfTokenRequestFilter;

	@Autowired
	private LogoutSuccessHandler myLogoutHandler;

	@Autowired
	private UnratedRequestsFilter unratedRequestsFilter;

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception { return super.authenticationManagerBean(); }

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.cors()
			.and()
			.csrf()
			.csrfTokenRepository(cookieCsrfTokenRepository())
			.ignoringAntMatchers("/h2/**");

		http
			.authorizeRequests()
			.antMatchers("/h2/**")
			.permitAll()
			.antMatchers(HttpMethod.GET, "/api/getCsrf")
			.permitAll()
			.antMatchers(HttpMethod.POST, "/api/auth/*")
			.permitAll()
			.anyRequest()
			.authenticated();

		http.headers().frameOptions().disable();

		http.formLogin().disable().logout().logoutSuccessHandler(myLogoutHandler);

		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(csrfTokenRequestFilter, CsrfFilter.class);
		http.addFilterAfter(unratedRequestsFilter, JwtRequestFilter.class);

	}

	CookieCsrfTokenRepository cookieCsrfTokenRepository() {
		CookieCsrfTokenRepository repo = new CookieCsrfTokenRepository();
		repo.setCookieHttpOnly(false);
		repo.setCookieName("X-CSRF-COOKIE"); //TODO constants
		repo.setHeaderName("X-CSRF-TOKEN");

		return repo;
	}

	@Override
	public void configure(WebSecurity web) {
		//web.ignoring().antMatchers("/*");
		web.ignoring().antMatchers("/*.ico");
		web.ignoring().antMatchers("/*.js");
		web.ignoring().antMatchers("/*.json");
		web.ignoring().antMatchers("/*.png");
		web.ignoring().antMatchers("/static/**");
	}

	@Bean
	public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(11); }

}
