package NULL.DTPomoziMi.util;

import java.util.List;

import org.springframework.validation.FieldError;

public class Common {

	/**
	 * The Class Counter.
	 */
	static class Counter {

		/** The c. */
		public int c;

		/**
		 * Instantiates a new counter.
		 *
		 * @param c the c
		 */
		public Counter(int c) { this.c = c; }
	}

	public static String stringifyErrors(List<FieldError> list) {
		Counter c = new Counter(0);
		return list
			.stream()
			.reduce("[", (partialRes, e) -> { c.c++; return partialRes + (c.c == 1 ? "" : ", ") + e.toString(); }, (s1, s2) -> s1 + s2)
			+ "]";
	}

}
