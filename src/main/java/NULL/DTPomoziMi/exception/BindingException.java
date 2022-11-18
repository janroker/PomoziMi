package NULL.DTPomoziMi.exception;

import java.util.List;

import org.springframework.validation.FieldError;

public class BindingException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final List<FieldError> fieldErrors;

	public BindingException(List<FieldError> fieldErrors) { this.fieldErrors = fieldErrors; }

	public BindingException(String message, List<FieldError> fieldErrors) { super(message); this.fieldErrors = fieldErrors; }

	public BindingException(Throwable cause, List<FieldError> fieldErrors) { super(cause); this.fieldErrors = fieldErrors; }

	public BindingException(String message, Throwable cause, List<FieldError> fieldErrors) {
		super(message, cause);
		this.fieldErrors = fieldErrors;
	}

	public BindingException(
		String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<FieldError> fieldErrors
	) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.fieldErrors = fieldErrors;
	}

	public List<FieldError> getFieldErrors() { return fieldErrors; }

}
