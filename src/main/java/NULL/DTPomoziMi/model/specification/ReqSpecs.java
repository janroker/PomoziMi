package NULL.DTPomoziMi.model.specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;

import org.springframework.data.jpa.domain.Specification;

import NULL.DTPomoziMi.model.Location;
import NULL.DTPomoziMi.model.Request;
import NULL.DTPomoziMi.model.RequestStatus;
import NULL.DTPomoziMi.model.User;

public class ReqSpecs {
	public static Specification<Request> statusEqual(RequestStatus status) {
		return (root, query, builder) -> {
			if (status == null)
				return builder.conjunction();// nemoj filtrirat ak je null

			return builder.equal(root.<RequestStatus>get("status"), status);
		};
	}

	public static <R, P> Specification<R> atributeEqualNotEqual(String attribute, P type, boolean equal) {
		return (root, query, builder) -> {
			if (type == null)
				return builder.conjunction();// nemoj filtrirat ak je null

			if (equal)
				return builder.equal(root.<P>get(attribute), type);
			else
				return builder.notEqual(root.<P>get(attribute), type);
		};
	}

	public static Specification<Request> getByStatusOrderByLocation(RequestStatus status, Location location,
			double radius) {
		return (root, query, builder) -> {
			if (status == null)
				return builder.disjunction();

			if (location == null || Math.abs(radius - 0.0) < 1E-8)
				return builder.and(builder.equal(root.get("status"), status), builder.isNull(root.get("location")));

			Join<Request, Location> join = root.join("location", JoinType.LEFT);

			Expression<Double> expr = builder.function("CALCULATE_DISTANCE", Double.class,
					builder.literal(location.getLatitude()), builder.literal(location.getLongitude()),
					join.get("latitude"), join.get("longitude"));

			Order order = builder.asc(expr);
			query.orderBy(order);

			return builder.and(
					builder.or(builder.isNull(root.get("location")), builder.lessThanOrEqualTo(expr, radius)),
					builder.equal(root.get("status"), status));
		};
	}
	
	/**
	 * Authors attribute like.
	 *
	 * @param attribute the attribute
	 * @param name the name
	 * @return the specification
	 */
	public static Specification<Request> autAttrLike(String attribute, String name){
		return (root, query, builder) -> {
			if(attribute == null)
				return builder.conjunction();
			
			Join<Request, User> join = root.join("author", JoinType.LEFT);
			
			return builder.like(builder.lower(join.<String>get(name)), "%" + attribute.toLowerCase() + "%");
		};
	} 

}
