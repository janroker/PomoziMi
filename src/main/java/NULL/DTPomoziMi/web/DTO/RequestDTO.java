package NULL.DTPomoziMi.web.DTO;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonFormat;

import NULL.DTPomoziMi.model.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Getter
@Setter
//@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(collectionRelation = "requests", itemRelation = "request")
public class RequestDTO extends RepresentationModel<RequestDTO> {

	@Include
	@NotNull
	private Long IdRequest;

	@Pattern(regexp = "^[0-9]+$")
	@NotNull
	private String phone;

	@Future
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime tstmp;

	@NotNull
	private String description;

	private RequestStatus status;

	@NotNull
	@Valid
	private UserDTO author;

	@Valid
	private UserDTO executor;

	@Valid
	private LocationDTO location;
	
	private Set<RatingDTO> ratings = new HashSet<>();

	@Past
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime execTstmp;
	
	private boolean confirmed;
}
