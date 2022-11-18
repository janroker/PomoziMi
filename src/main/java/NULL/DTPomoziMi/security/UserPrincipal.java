package NULL.DTPomoziMi.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import NULL.DTPomoziMi.model.Role;
import NULL.DTPomoziMi.model.User;

public class UserPrincipal implements UserDetails {

	private static final long serialVersionUID = -6328324723198029932L;

	private final User user;

	public UserPrincipal(User user) { super(); this.user = user; }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() { return mapToGrantedAuthorities(user.getEnumRoles()); }

	private List<? extends GrantedAuthority> mapToGrantedAuthorities(List<Role> roles) {
		return roles.stream().map(r -> new SimpleGrantedAuthority(r.toString())).collect(Collectors.toList());
	}

	@Override
	public String getPassword() { return user.getPassword(); }

	@Override
	public String getUsername() { return user.getEmail(); }

	@Override
	public boolean isAccountNonExpired() { return true; }

	@Override
	public boolean isAccountNonLocked() { return true; }

	@Override
	public boolean isCredentialsNonExpired() { return true; }

	@Override
	public boolean isEnabled() { return user.getEnabled(); }

	public User getUser() { return user; }

}
