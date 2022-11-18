package NULL.DTPomoziMi.web.assemblers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import NULL.DTPomoziMi.model.Location;
import NULL.DTPomoziMi.web.DTO.LocationDTO;
import NULL.DTPomoziMi.web.controller.LocationController;

@Component
public class LocationDTOAssembler extends RepresentationModelAssemblerSupport<Location, LocationDTO> {

	@Autowired
	private ModelMapper modelMapper;

	public LocationDTOAssembler() { super(LocationController.class, LocationDTO.class); }

	@Override
	public LocationDTO toModel(Location entity) {
		LocationDTO locationDTO = createModelWithId(entity.getIdLocation(), entity);
		modelMapper.map(entity, locationDTO);

		return locationDTO;
	}

}
