package NULL.DTPomoziMi.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public final class MutableHttpServletRequest extends HttpServletRequestWrapper {

	private final Map<String, String> customHeaders;

	public MutableHttpServletRequest(HttpServletRequest request) { super(request); this.customHeaders = new HashMap<>(); }

	public void putHeader(String name, String value) { customHeaders.put(name, value); }

	@Override
	public String getHeader(String name) {
		String value = customHeaders.get(name);

		if (value != null) return value;

		return ((HttpServletRequest) this.getRequest()).getHeader(name);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		Set<String> set = new HashSet<>(customHeaders.keySet());

		Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
		while (e.hasMoreElements()) { set.add(e.nextElement()); }

		return Collections.enumeration(set);
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		Set<String> headerValues = new HashSet<>();
		headerValues.add(this.customHeaders.get(name));

		Enumeration<String> underlyingHeaderValues = ((HttpServletRequest) getRequest()).getHeaders(name);
		while (underlyingHeaderValues.hasMoreElements()) { headerValues.add(underlyingHeaderValues.nextElement()); }

		return Collections.enumeration(headerValues);
	}

}
