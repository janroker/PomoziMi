package NULL.DTPomoziMi.model.specification;

import org.springframework.data.jpa.domain.Specification;

import NULL.DTPomoziMi.model.Candidacy;

public class CandidacySpecs {

	public static Specification<Candidacy> yearEqual(Integer year) {
		return (root, query, builder) -> {
			if (year == null) return builder.conjunction();// nemoj filtrirat ak je null

			return builder.equal(root.get("year"), year);
		};
	}

}
