package NULL.DTPomoziMi.util;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode=ScopedProxyMode.TARGET_CLASS)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestHolder implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private HttpServletRequest request;
}
