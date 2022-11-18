package NULL.DTPomoziMi.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import NULL.DTPomoziMi.exception.EntityMissingException;
import NULL.DTPomoziMi.exception.IllegalAccessException;
import NULL.DTPomoziMi.model.Notification;
import NULL.DTPomoziMi.repository.NotificationRepo;
import NULL.DTPomoziMi.security.UserPrincipal;
import NULL.DTPomoziMi.service.NotificationService;
import NULL.DTPomoziMi.service.UserService;
import NULL.DTPomoziMi.web.DTO.NotificationDTO;

@Service
@PreAuthorize("isAuthenticated()")
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private NotificationRepo notificationRepo;

	@Autowired
	private UserService userService;

	@Override
	public Notification create(long idUser, NotificationDTO notif) {
		Notification newN = new Notification(null, notif.getMessage(), false, userService.fetch(idUser), LocalDateTime.now());
		return notificationRepo.save(newN);
	}

	@Override
	public Page<Notification> findByUser(Pageable pageable, long userID, UserPrincipal principal) {

		if (!principal.getUser().getIdUser().equals(userID)) throw new IllegalAccessException("Only reciever can see notifications");

		return notificationRepo.findByUserOrderByTstmpDesc(pageable, principal.getUser());

	}

	@Override
	public Notification findById(long notifId, UserPrincipal principal) {
		Notification notif = fetch(notifId);

		if (!principal.getUser().getIdUser().equals(notif.getUser().getIdUser()))
			throw new IllegalAccessException("Only reciever can see notifications");

		return notif;
	}

	@Override
	public Notification fetch(long id) {
		return notificationRepo.findById(id).orElseThrow(() -> new EntityMissingException(Notification.class, id));
	}

	@Override
	public void markSeen(long idNotif, UserPrincipal principal) {
		Notification notif = fetch(idNotif);

		if (!principal.getUser().getIdUser().equals(notif.getUser().getIdUser()))
			throw new IllegalAccessException("Only reciever can see notifications");

		notif.setReceived(true);
		notificationRepo.save(notif);
	}

}
