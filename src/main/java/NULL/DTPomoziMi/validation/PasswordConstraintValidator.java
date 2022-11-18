package NULL.DTPomoziMi.validation;

import org.passay.*;
import org.passay.spring.SpringMessageResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.stream.Collectors;

@Component
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

	@Autowired
	private MessageSource messageSource;

	@Override
	public void initialize(ValidPassword constraintAnnotation) {}

	@Override
	public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
		MessageResolver resolver = new SpringMessageResolver(messageSource);

		PasswordValidator val = new PasswordValidator(
			resolver, new LengthRule(8, 50), new CharacterRule(CroatianCharacterData.UpperCase, 1),
			new CharacterRule(EnglishCharacterData.Digit, 1), new CharacterRule(EnglishCharacterData.Special, 1), new WhitespaceRule()
		);

		RuleResult result = val.validate(new PasswordData(s));
		if (result.isValid()) return true;

		String message = val.getMessages(result).stream().collect(Collectors.joining("] | ["));

		constraintValidatorContext.disableDefaultConstraintViolation();
		constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();

		return false;
	}
}
