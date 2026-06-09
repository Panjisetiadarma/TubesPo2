package com.visualgallery.service;

import com.visualgallery.model.Notification;
import com.visualgallery.repository.NotificationRepository;

import java.util.List;

/**
 * NotificationService - Business logic for notifications.
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService() {
        this.notificationRepository = new NotificationRepository();
    }

    public List<Notification> getNotificationsForUser(int userId) {
        return notificationRepository.findByUserId(userId);
    }

    public int getUnreadCount(int userId) {
        return notificationRepository.countUnread(userId);
    }

    public boolean markAllAsRead(int userId) {
        return notificationRepository.markAllAsRead(userId);
    }

    public boolean markAsRead(int notificationId) {
        return notificationRepository.markAsRead(notificationId);
    }

    public void sendNotification(int toUserId, int fromUserId, Integer postId,
                                 String message, Notification.Type type) {
        Notification notif = new Notification(toUserId, message, type);
        notif.setSenderId(fromUserId);
        notif.setPostId(postId);
        notificationRepository.save(notif);
    }

    public List<Notification> getAllForAdmin() {
        return notificationRepository.findAllForAdmin();
    }
}
