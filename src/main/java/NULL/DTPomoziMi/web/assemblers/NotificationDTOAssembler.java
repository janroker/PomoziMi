package NULL.DTPomoziMi.web.assemblers;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import NULL.DTPomoziMi.model.Notification;
import NULL.DTPomoziMi.model.User;
import NULL.DTPomoziMi.web.DTO.NotificationDTO;
import NULL.DTPomoziMi.web.DTO.UserDTO;
import NULL.DTPomoziMi.web.controller.NotificationController;

@Component
public class NotificationDTOAssembler extends RepresentationModelAssemblerSupport<Notification, NotificationDTO> {

	private final ModelMapper modelMapper;

	@Autowired
	public NotificationDTOAssembler(ModelMapper modelMapper, UserDTOModelAssembler userAssembler) {
		super(NotificationController.class, NotificationDTO.class);
		this.modelMapper = modelMapper;
		configureNotificationToNotificationDTO(userAssembler);
	}

	@Override
	public NotificationDTO toModel(Notification entity) {
		NotificationDTO notificationDTO = createModelWithId(entity.getIdNotification(), entity);
		modelMapper.map(entity, notificationDTO);

		return notificationDTO;
	}

	private void configureNotificationToNotificationDTO(UserDTOModelAssembler userAssembler) {

		Converter<User, UserDTO> userConverter
			= context -> (context.getSource() == null ? null : userAssembler.toModel(context.getSource()));

		modelMapper.addMappings(new PropertyMap<Notification, NotificationDTO>() {
			@Override
			protected void configure() {
				using(userConverter).map(source.getUser()).setUser(null);
				map().setMessage(source.getMessage());
				map().setIdNotification(source.getIdNotification());
				map().setReceived(source.getReceived());
				map().setTstmp(source.getTstmp());
			}
		});

	}
}
