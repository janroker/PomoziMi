package NULL.DTPomoziMi.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Component
@PropertySources(
	{ @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "classpath:/config/application.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file:./application.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file:./config/*/application.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file:./config/application.properties", ignoreResourceNotFound = true) }
)
public class JwtConstants {

	public static String JWT_COOKIE_NAME;
	public static String JWT_REFRESH_COOKIE_NAME;
	public static Long JWT_EXPIRATION;
	public static Long JWT_REFRESH_EXPIRATION;
	public static String SECRET_KEY;

	@Value("${jwt.refresh.cookie.name}")
	public void setJwtRefreshCookieName(String jwtRefreshCookieName) {
		if (jwtRefreshCookieName == null) throwException("jwt.refresh.cookie.name");
		JWT_REFRESH_COOKIE_NAME = jwtRefreshCookieName;
	}

	@Value("${jwt.expiration}")
	public void setJwtCookieExpiration(Long jwtExpiration) {
		if (jwtExpiration == null) throwException("jwt.expiration");
		JWT_EXPIRATION = jwtExpiration;
	}

	@Value("${jwt.refresh.expiration}")
	public void setJwtRefreshCookieExpiration(Long jwtRefreshExpiration) {
		if (jwtRefreshExpiration == null) throwException("jwt.refresh.expiration");
		JWT_REFRESH_EXPIRATION = jwtRefreshExpiration;
	}

	@Value("${secret.key}")
	public void setSecretKey(String secretKey) { if (secretKey == null) throwException("secret.key"); SECRET_KEY = secretKey; }

	@Value("${jwt.cookie.name}")
	public void setJwtCookieName(String jwtCookieName) {
		if (jwtCookieName == null) throwException("jwt.cookie.name");
		JWT_COOKIE_NAME = jwtCookieName;
	}

	private static void throwException(String prop) {
		throw new NullPointerException(prop + " property is missing from application.properties!");
	}
}
