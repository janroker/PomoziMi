package NULL.DTPomoziMi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import NULL.DTPomoziMi.model.Notification;
import NULL.DTPomoziMi.model.User;

public interface NotificationRepo extends PagingAndSortingRepository<Notification, Long> {
	Page<Notification> findByUserOrderByTstmpDesc(Pageable pageable, User user);
}
