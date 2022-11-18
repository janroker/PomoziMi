package NULL.DTPomoziMi.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import NULL.DTPomoziMi.service.NotificationService;
import NULL.DTPomoziMi.web.DTO.NotificationDTO;

@Component
public class SendMessageEventListener implements ApplicationListener<SendMessageEvent> {

	@Autowired
	private NotificationService notifService;

	@Override
	public void onApplicationEvent(SendMessageEvent event) {
		NotificationDTO dto = new NotificationDTO(null, event.getMessage(), false, null, null);
		notifService.create(event.getIdReciever(), dto);
	}

}
