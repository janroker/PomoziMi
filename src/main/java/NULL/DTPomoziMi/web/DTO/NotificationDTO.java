package NULL.DTPomoziMi.web.DTO;

import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

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
@Relation(collectionRelation = "notifications", itemRelation = "notification")
public class NotificationDTO extends RepresentationModel<NotificationDTO>{

	@Include
	private Long idNotification;

	@NotBlank
	private String message;
	
	private Boolean received;

	@Valid
	private UserDTO user;
	
	private LocalDateTime tstmp;
	
}
