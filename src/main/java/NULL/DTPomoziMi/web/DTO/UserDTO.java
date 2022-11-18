package NULL.DTPomoziMi.web.DTO;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
//@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(collectionRelation = "users", itemRelation = "user")
public class UserDTO extends RepresentationModel<UserDTO> {

	@NotNull
	private Long IdUser;

	@NotNull
	@Size(min = 1, message = "{Size.UserDTO.firstName}")
	private String firstName;

	@NotNull
	@Size(message = "{Size.UserDTO.lastName}", min = 1)
	private String lastName;

	@NotNull
	@Email(message = "{UserDTO.email}")
	private String email;

	boolean enabled;

	@Valid
	private LocationDTO location;
	
	String picture;

}
