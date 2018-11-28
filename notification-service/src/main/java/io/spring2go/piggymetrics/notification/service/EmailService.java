package io.spring2go.piggymetrics.notification.service;

import io.spring2go.piggymetrics.notification.domain.NotificationType;
import io.spring2go.piggymetrics.notification.domain.Recipient;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailService {

	void send(NotificationType type, Recipient recipient, String attachment) throws MessagingException, IOException;

}
