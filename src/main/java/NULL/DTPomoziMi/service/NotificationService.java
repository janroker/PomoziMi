package NULL.DTPomoziMi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import NULL.DTPomoziMi.exception.EntityMissingException;
import NULL.DTPomoziMi.model.Notification;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.web.DTO.NotificationDTO;

public interface NotificationService {

	/**
	 * Find by user.
	 *
	 * @param pageable  the pageable
	 * @param userID    the user ID
	 * @param principal the principal
	 * @return the page
	 * @throws IllegalAccessException if trying to fetch someone elses notifications
	 */
	Page<Notification> findByUser(Pageable pageable, long userID, UserPrincipal principal);

	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 */
	Notification fetch(long id);

	/**
	 * Find by id.
	 *
	 * @param notifId   the notif id
	 * @param principal the principal
	 * @return the notification
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 * @throws IllegalAccessException if trying to fetch someone elses notifications
	 */
	Notification findById(long notifId, UserPrincipal principal);

	/**
	 * Creates the notification.
	 *
	 * @param idUser    the id user
	 * @param principal the principal
	 * @return the notification
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 */
	Notification create(long idUser, NotificationDTO notif);

	/**
	 * Mark seen.
	 *
	 * @param idNotif   the id notif
	 * @param principal the principal
	 * @throws EntityMissingException - if element with given <code>id</code> does
	 *                                not exist
	 * @throws IllegalAccessException if trying to mark someone elses notifications
	 */
	void markSeen(long idNotif, UserPrincipal principal);

}
