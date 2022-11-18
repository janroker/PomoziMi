package NULL.DTPomoziMi.web.assemblers;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import NULL.DTPomoziMi.model.Location;
import NULL.DTPomoziMi.model.User;
import NULL.DTPomoziMi.web.DTO.LocationDTO;
import NULL.DTPomoziMi.web.DTO.UserDTO;
import NULL.DTPomoziMi.web.controller.UsersController;

@Component
public class UserDTOModelAssembler extends RepresentationModelAssemblerSupport<User, UserDTO> {

	private final ModelMapper modelMapper;

	@Autowired
	public UserDTOModelAssembler(ModelMapper modelMapper, LocationDTOAssembler locationAssembler) {
		super(UsersController.class, UserDTO.class);
		this.modelMapper = modelMapper;
		configureUserToUserDTO(locationAssembler);
	}

	@Override
	public UserDTO toModel(User entity) {
		UserDTO userDTO = createModelWithId(entity.getIdUser(), entity);
		modelMapper.map(entity, userDTO);

		return userDTO;
	}

	private void configureUserToUserDTO(LocationDTOAssembler locationAssembler) {
		Converter<Location, LocationDTO> locationConverter
			= context -> (context.getSource() == null ? null : locationAssembler.toModel(context.getSource()));

		modelMapper.addMappings(new PropertyMap<User, UserDTO>() {
			@Override
			protected void configure() {
				using(locationConverter).map(source.getLocation()).setLocation(null);
				map().setEmail(source.getEmail());
				map().setFirstName(source.getFirstName());
				map().setLastName(source.getLastName());
				map().setIdUser(source.getIdUser());
				map().setEnabled(source.getEnabled());
			}
		});

	}

}
