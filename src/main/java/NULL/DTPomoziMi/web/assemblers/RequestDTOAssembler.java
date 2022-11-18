package NULL.DTPomoziMi.web.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import NULL.DTPomoziMi.model.Location;
import NULL.DTPomoziMi.model.Rating;
import NULL.DTPomoziMi.model.Request;
import NULL.DTPomoziMi.model.User;
import NULL.DTPomoziMi.web.DTO.LocationDTO;
import NULL.DTPomoziMi.web.DTO.RatingDTO;
import NULL.DTPomoziMi.web.DTO.RequestDTO;
import NULL.DTPomoziMi.web.DTO.UserDTO;
import NULL.DTPomoziMi.web.controller.RatingController;
import NULL.DTPomoziMi.web.controller.RequestController;

@Component
public class RequestDTOAssembler extends RepresentationModelAssemblerSupport<Request, RequestDTO> {

	private final ModelMapper modelMapper;

	@Autowired
	public RequestDTOAssembler(ModelMapper modelMapper, UserDTOModelAssembler userAssembler, LocationDTOAssembler locationAssembler) {
		super(RequestController.class, RequestDTO.class);
		this.modelMapper = modelMapper;
		configureRequestToRequestDTO(userAssembler, locationAssembler);
	}

	@Override
	public RequestDTO toModel(Request entity) {
		RequestDTO requestDTO = createModelWithId(entity.getIdRequest(), entity);
		modelMapper.map(entity, requestDTO);

		return requestDTO;
	}

	private void configureRequestToRequestDTO(UserDTOModelAssembler userAssembler, LocationDTOAssembler locationAssembler) {

		Converter<User, UserDTO> userConverter
			= context -> (context.getSource() == null ? null : userAssembler.toModel(context.getSource()));

		Converter<Location, LocationDTO> locationConverter
			= context -> (context.getSource() == null ? null : locationAssembler.toModel(context.getSource()));
		
		Converter<Set<Rating>, Set<RatingDTO>> ratingsConverter = context -> (context.getSource() == null ? null : convertRatings(context.getSource(), userAssembler));

		modelMapper.addMappings(new PropertyMap<Request, RequestDTO>() {
			@Override
			protected void configure() {
				using(userConverter).map(source.getAuthor()).setAuthor(null);
				using(userConverter).map(source.getExecutor()).setExecutor(null);
				using(locationConverter).map(source.getLocation()).setLocation(null);
				map().setDescription(source.getDescription());
				map().setIdRequest(source.getIdRequest());
				map().setPhone(source.getPhone());
				map().setStatus(source.getStatus());
				map().setTstmp(source.getTstmp());
				map().setExecTstmp(source.getExecTstmp());
				map().setConfirmed(source.isConfirmed());
				using(ratingsConverter).map(source.getRatings()).setRatings(null);
			}
		});

	}
	
	private Set<RatingDTO> convertRatings(Set<Rating> source, UserDTOModelAssembler userAssembler){
		Set<RatingDTO> set = new HashSet<>();
		
		source.forEach(s -> {
			RatingDTO r = new RatingDTO();
			r.setIdRating(s.getIdRating());
			r.setComment(s.getComment());
			r.setRate(s.getRate());
			r.setRated(userAssembler.toModel(s.getRated()));
			r.setRator(userAssembler.toModel(s.getRator()));
			r.add(linkTo(methodOn(RatingController.class).getRatingById(s.getIdRating(), null)).withSelfRel());
			
			set.add(r);
		});
		
		return set;
	}

}
