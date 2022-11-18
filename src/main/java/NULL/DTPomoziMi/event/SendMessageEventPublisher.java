package NULL.DTPomoziMi.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SendMessageEventPublisher {

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	public void publishMessageEvent(final String message, final long idReciever) {
		System.out.println("Publishing custom event. ");
		SendMessageEvent sme = new SendMessageEvent(this, message, idReciever);
		applicationEventPublisher.publishEvent(sme);
	}

}
