package NULL.DTPomoziMi.web.DTO;

import java.math.BigDecimal;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import NULL.DTPomoziMi.validation.MatchPassword;
import NULL.DTPomoziMi.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@MatchPassword(message = "{UserDTO.MatchPassword}")
public class UserRegisterDTO {

	@NotNull
	@Size(min = 1, message = "{Size.UserDTO.firstName}")
	private String firstName;

	@NotNull
	@Size(message = "{Size.UserDTO.lastName}", min = 1)
	private String lastName;

	@ValidPassword
	private String password;
	private String secondPassword;

	@NotNull
	@Email(message = "{UserDTO.email}")
	private String email;

	private String adress;

	private String state;

	private String town;

	@Max(180)
	@Min(-180)
	private BigDecimal longitude;

	@Max(90)
	@Min(-90)
	private BigDecimal latitude;

}
