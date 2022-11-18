package NULL.DTPomoziMi.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import NULL.DTPomoziMi.properties.JwtConstants;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

	public static final String CLAIM_ID = "id";

	public static final String CLAIM_ROLES = "roles";

	@Autowired
	private TokenService tokenService;

	public String extractUsername(String token) { return extractClaim(token, Claims::getSubject); }

	public Date extractExpiration(String token) { return extractClaim(token, Claims::getExpiration); }

	@SuppressWarnings("unchecked")
	public List<String> extractRoles(String token) { return (List<String>) extractClaim(token, claims -> claims.get(CLAIM_ROLES)); }

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(JwtConstants.SECRET_KEY).parseClaimsJws(token).getBody();
	}

	private boolean isTokenExpired(String token) { return extractExpiration(token).before(new Date()); }

	public String generateToken(UserPrincipal principal) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_ROLES, principal.getAuthorities().stream().map(ga -> ga.getAuthority()).collect(Collectors.toList()));
		claims.put(CLAIM_ID, principal.getUser().getIdUser());
		return createToken(claims, principal.getUsername(), JwtConstants.JWT_EXPIRATION);
	}

	public String generateToken(Map<String, Object> claims, String username) {
		return createToken(claims, username, JwtConstants.JWT_EXPIRATION);
	}

	public String generateRefreshToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>(); // payload ... za sada prazno... moze ic sta god hoces spremit
		return createToken(claims, userDetails.getUsername(), JwtConstants.JWT_REFRESH_EXPIRATION);
	}

	private String createToken(Map<String, Object> claims, String subject, Long expiration) {
		return Jwts
			.builder()
			.setClaims(claims)
			.setSubject(subject)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expiration))
			.signWith(SignatureAlgorithm.HS384, JwtConstants.SECRET_KEY)
			.compact();
	}

	public boolean validateToken(String token) {
		try {
			return !isTokenExpired(token);
		} catch (JwtException e) {
			return false;
		}
	}

	public boolean validateRefreshToken(String token) {
		try {
			String username = extractUsername(token);
			return (!isTokenExpired(token) && token.equals(tokenService.getTokenByEmail(username)));
		} catch (JwtException e) {
			return false;
		}
	}

}
