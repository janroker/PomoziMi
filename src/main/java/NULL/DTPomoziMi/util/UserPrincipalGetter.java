package NULL.DTPomoziMi.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import NULL.DTPomoziMi.security.UserPrincipal;

public class UserPrincipalGetter {

	/**
	 * 
	 * Returns currently authenticated UserPrincipal or null if user is not
	 * authenticated
	 * 
	 */
	public static UserPrincipal getPrincipal() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) return null;

		return (UserPrincipal) auth.getPrincipal();
	}

}
