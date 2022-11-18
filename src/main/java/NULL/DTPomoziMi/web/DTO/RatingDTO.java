package NULL.DTPomoziMi.web.DTO;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

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
@Relation(collectionRelation = "ratings", itemRelation = "rating")
public class RatingDTO extends RepresentationModel<RatingDTO> { // TODO validacija
	@Include
	private Long IdRating;

	private String comment;

	@NotNull
	@Min(1)
	@Max(5)
	private Integer rate;

	@Valid
	private UserDTO rated;

	@Valid
	private UserDTO rator;

	@Valid
	private RequestDTO request;

}
