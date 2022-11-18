package NULL.DTPomoziMi.web.DTO;

import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;

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
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateRequestDTO {

	@Pattern(regexp = "^[0-9]+$")
	@NotNull
	private String phone;

	@Valid
	private LocationDTO location;

	@Future
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime tstmp;

	@NotNull
	private String description;
}
