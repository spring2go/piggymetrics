package io.spring2go.piggymetrics.account.service;

import io.spring2go.piggymetrics.account.CatAnnotation;
import io.spring2go.piggymetrics.account.client.StatisticsServiceClient;
import io.spring2go.piggymetrics.account.client.UserServiceClient;
import io.spring2go.piggymetrics.account.domain.Account;
import io.spring2go.piggymetrics.account.domain.Currency;
import io.spring2go.piggymetrics.account.domain.Saving;
import io.spring2go.piggymetrics.account.domain.User;
import io.spring2go.piggymetrics.account.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class AccountServiceImpl implements AccountService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private StatisticsServiceClient statisticsClient;
	
	@Autowired
	private UserServiceClient userClient;

	@Autowired
	private AccountRepository repository;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@CatAnnotation
	public Account findByName(String accountName) {
		Assert.hasLength(accountName);
		
		Account account = repository.findByName(accountName);
		return account;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@CatAnnotation
	public Account create(User user) {
		
		Account existing = repository.findByName(user.getUsername());
		Assert.isNull(existing, "account already exists: " + user.getUsername());
		
		userClient.createUser(user.getUsername(), user.getPassword());

		Saving saving = new Saving();
		saving.setAmount(new BigDecimal(0));
		saving.setCurrency(Currency.getDefault());
		saving.setInterest(new BigDecimal(0));
		saving.setDeposit(false);
		saving.setCapitalization(false);

		Account account = new Account();
		account.setName(user.getUsername());
		account.setLastSeen(new Date());
		account.setSaving(saving);

		repository.save(account);
			
		log.info("new account has been created: " + account.getName());

		return account;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@CatAnnotation
	public void saveChanges(String name, Account update) {

		Account account = repository.findByName(name);
		Assert.notNull(account, "can't find account with name " + name);

		account.setIncomes(update.getIncomes());
		account.setExpenses(update.getExpenses());
		account.setSaving(update.getSaving());
		account.setNote(update.getNote());
		account.setLastSeen(new Date());
		
		repository.save(account);

		log.debug("account {} changes has been saved", name);

		statisticsClient.updateStatistics(name, account);
	}
}
