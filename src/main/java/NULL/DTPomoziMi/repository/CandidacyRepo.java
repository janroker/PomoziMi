package NULL.DTPomoziMi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import NULL.DTPomoziMi.model.Candidacy;

public interface CandidacyRepo extends PagingAndSortingRepository<Candidacy, Long>, JpaSpecificationExecutor<Candidacy> {

	List<Candidacy> findByYear(int year);

}
