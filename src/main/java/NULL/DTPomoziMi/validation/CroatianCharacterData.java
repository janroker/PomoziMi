package NULL.DTPomoziMi.validation;

import org.passay.CharacterData;

public enum CroatianCharacterData implements CharacterData {
	/** Lower case characters. */
	LowerCase("INSUFFICIENT_LOWERCASE", "abcčćdđefghijklmnnoprsštuvzžqxyw"),

	/** Upper case characters. */
	UpperCase("INSUFFICIENT_UPPERCASE", "ABCČĆDĐEFGHIJKLMNOPRSŠTUVZŽQWYX");

	/** Error code. */
	private final String errorCode;

	/** Characters. */
	private final String characters;

	/**
	 * Creates a new croatian character data.
	 *
	 * @param code       Error code.
	 * @param charString Characters as string.
	 */
	CroatianCharacterData(final String code, final String charString) { errorCode = code; characters = charString; }

	@Override
	public String getErrorCode() { return errorCode; }

	@Override
	public String getCharacters() { return characters; }
}
