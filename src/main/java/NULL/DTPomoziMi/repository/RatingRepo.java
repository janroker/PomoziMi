package NULL.DTPomoziMi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import NULL.DTPomoziMi.model.Rating;

public interface RatingRepo extends PagingAndSortingRepository<Rating, Long> {

	@Query("select o from ocjenjivanje o where o.rated.IdUser = :rated")
	Page<Rating> findByRated(Pageable pageable, @Param("rated") long rated);

}
