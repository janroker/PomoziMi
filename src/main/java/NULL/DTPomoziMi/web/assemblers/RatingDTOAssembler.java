package NULL.DTPomoziMi.web.assemblers;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import NULL.DTPomoziMi.model.Rating;
import NULL.DTPomoziMi.model.Request;
import NULL.DTPomoziMi.model.User;
import NULL.DTPomoziMi.web.DTO.RatingDTO;
import NULL.DTPomoziMi.web.DTO.RequestDTO;
import NULL.DTPomoziMi.web.DTO.UserDTO;
import NULL.DTPomoziMi.web.controller.UsersController;

@Component
public class RatingDTOAssembler extends RepresentationModelAssemblerSupport<Rating, RatingDTO> {

	private final ModelMapper modelMapper;

	@Autowired
	public RatingDTOAssembler(ModelMapper modelMapper, UserDTOModelAssembler userAssembler, RequestDTOAssembler requestAssembler) {
		super(UsersController.class, RatingDTO.class);
		this.modelMapper = modelMapper;
		configureRatingToRatingDTO(userAssembler, requestAssembler);
	}

	@Override
	public RatingDTO toModel(Rating entity) {
		RatingDTO ratingDTO = createModelWithId(entity.getIdRating(), entity);
		modelMapper.map(entity, ratingDTO);

		return ratingDTO;
	}

	private void configureRatingToRatingDTO(UserDTOModelAssembler userAssembler, RequestDTOAssembler requestAssembler) {

		Converter<User, UserDTO> userConverter
			= context -> (context.getSource() == null ? null : userAssembler.toModel(context.getSource()));

		Converter<Request, RequestDTO> requestConverter
			= context -> (context.getSource() == null ? null : requestAssembler.toModel(context.getSource()));

		modelMapper.addMappings(new PropertyMap<Rating, RatingDTO>() {
			@Override
			protected void configure() {
				using(userConverter).map(source.getRator()).setRator(null);
				using(userConverter).map(source.getRated()).setRated(null);
				using(requestConverter).map(source.getRequest()).setRequest(null);
				map().setRate(source.getRate());
				map().setIdRating(source.getIdRating());
				map().setComment(source.getComment());
			}
		});

	}
}
