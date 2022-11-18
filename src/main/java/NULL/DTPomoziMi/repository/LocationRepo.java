package NULL.DTPomoziMi.repository;

import java.math.BigDecimal;

import org.springframework.data.repository.CrudRepository;

import NULL.DTPomoziMi.model.Location;

public interface LocationRepo extends CrudRepository<Location, Long> {

	Location findByLatitudeAndLongitude(BigDecimal latitude, BigDecimal longitude);

}
