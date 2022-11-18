package NULL.DTPomoziMi.model.specification;

import org.springframework.data.jpa.domain.Specification;

import NULL.DTPomoziMi.model.User;

public class UserSpecs {

	public static Specification<User> firstNameLike(String firstName) {
		return (root, query, builder) -> {
			if (firstName == null) return builder.conjunction();// nemoj filtrirat ak je null

			return builder.like(builder.lower(root.<String>get("firstName")), "%" + firstName.toLowerCase() + "%");
		};
	}

	public static Specification<User> lastNameLike(String lastName) {
		return (root, query, builder) -> {
			if (lastName == null) return builder.conjunction();// nemoj filtrirat ak je null

			return builder.like(builder.lower(root.<String>get("lastName")), "%" + lastName.toLowerCase() + "%");
		};
	}

	public static Specification<User> emailLike(String email) {
		return (root, query, builder) -> {
			if (email == null) return builder.conjunction();// nemoj filtrirat ak je null

			return builder.like(builder.lower(root.<String>get("email")), "%" + email.toLowerCase() + "%");
		};
	}

}
