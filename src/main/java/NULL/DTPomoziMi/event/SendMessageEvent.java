package NULL.DTPomoziMi.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageEvent extends ApplicationEvent{
	private static final long serialVersionUID = 1L;
	
	private String message;
	private long idReciever;
	
	public SendMessageEvent(Object source, String message, long idReciever) {
		super(source);
		this.message = message;
		this.idReciever = idReciever;
	}
	
}
