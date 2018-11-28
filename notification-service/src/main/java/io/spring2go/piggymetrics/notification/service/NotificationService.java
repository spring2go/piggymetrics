package io.spring2go.piggymetrics.notification.service;

public interface NotificationService {

	void sendBackupNotifications();

	void sendRemindNotifications();
}
