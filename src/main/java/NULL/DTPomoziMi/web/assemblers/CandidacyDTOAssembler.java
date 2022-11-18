package NULL.DTPomoziMi.web.assemblers;

import java.util.Collection;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import NULL.DTPomoziMi.model.Candidacy;
import NULL.DTPomoziMi.model.Location;
import NULL.DTPomoziMi.model.User;
import NULL.DTPomoziMi.web.DTO.CandidacyDTO;
import NULL.DTPomoziMi.web.DTO.LocationDTO;
import NULL.DTPomoziMi.web.DTO.UserDTO;
import NULL.DTPomoziMi.web.controller.CandidacyController;

@Component
public class CandidacyDTOAssembler extends RepresentationModelAssemblerSupport<Candidacy, CandidacyDTO> {

	private final ModelMapper modelMapper;

	@Autowired
	public CandidacyDTOAssembler(ModelMapper modelMapper, LocationDTOAssembler locationAssembler, UserDTOModelAssembler userAssembler) {
		super(CandidacyController.class, CandidacyDTO.class);
		this.modelMapper = modelMapper;
		configureCandidacyToCandidacyDTO(locationAssembler, userAssembler);
	}

	@Override
	public CandidacyDTO toModel(Candidacy entity) {
		CandidacyDTO candidacyDTO = createModelWithId(entity.getIdCandidacy(), entity);
		modelMapper.map(entity, candidacyDTO);

		return candidacyDTO;
	}

	private void configureCandidacyToCandidacyDTO(LocationDTOAssembler locationAssembler, UserDTOModelAssembler userAssembler) {

		Converter<Location, LocationDTO> locationConverter
			= context -> (context.getSource() == null ? null : locationAssembler.toModel(context.getSource()));

		Converter<Collection<User>, CollectionModel<UserDTO>> usersConverter
			= context -> context.getSource() == null ? null : userAssembler.toCollectionModel(context.getSource());

		modelMapper.addMappings(new PropertyMap<Candidacy, CandidacyDTO>() {
			@Override
			protected void configure() {
				using(locationConverter).map(source.getLocation()).setLocation(null);
				using(usersConverter).map(source.getUsers()).setUsers(null);
				map().setIdCandidacy(source.getIdCandidacy());
				map().setYear(source.getYear());
			}
		});

	}

}
