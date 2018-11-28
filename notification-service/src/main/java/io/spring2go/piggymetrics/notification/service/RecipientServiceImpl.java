package io.spring2go.piggymetrics.notification.service;

import io.spring2go.piggymetrics.notification.CatAnnotation;
import io.spring2go.piggymetrics.notification.domain.NotificationType;
import io.spring2go.piggymetrics.notification.domain.Recipient;
import io.spring2go.piggymetrics.notification.repository.RecipientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

@Service
public class RecipientServiceImpl implements RecipientService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private RecipientRepository repository;

	@Override
	@CatAnnotation
	public Recipient findByAccountName(String accountName) {
		Assert.hasLength(accountName);
		
		return repository.findByAccountName(accountName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@CatAnnotation
	public Recipient save(String accountName, Recipient recipient) {

		recipient.setAccountName(accountName);
		recipient.getScheduledNotifications().values()
				.forEach(settings -> {
					if (settings.getLastNotified() == null) {
						settings.setLastNotified(new Date());
					}
				});
		
		repository.save(recipient);
		
		log.info("recipient {} settings has been updated", recipient);

		return recipient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@CatAnnotation
	public List<Recipient> findReadyToNotify(NotificationType type) {
		switch (type) {
			case BACKUP:

				return repository.findReadyForBackup();
				
			case REMIND:

				return repository.findReadyForRemind();

			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@CatAnnotation
	public void markNotified(NotificationType type, Recipient recipient) {
		recipient.getScheduledNotifications().get(type).setLastNotified(new Date());
		
		repository.save(recipient);

	}
}
