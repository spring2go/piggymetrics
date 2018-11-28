package io.spring2go.piggymetrics.statistics.service;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.ImmutableMap;

import io.spring2go.piggymetrics.statistics.CatAnnotation;
import io.spring2go.piggymetrics.statistics.client.ExchangeRatesClient;
import io.spring2go.piggymetrics.statistics.domain.Currency;
import io.spring2go.piggymetrics.statistics.domain.ExchangeRatesContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

@Service
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

	private static final Logger log = LoggerFactory.getLogger(ExchangeRatesServiceImpl.class);

	private ExchangeRatesContainer container;

	@Autowired
	private ExchangeRatesClient client;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@CatAnnotation
	public Map<Currency, BigDecimal> getCurrentRates() {

		if (container == null || !container.getDate().equals(LocalDate.now())) {
			
			Transaction dbTransaction = Cat.newTransaction(CatConstants.TYPE_CALL, "get_current_rates");

			try {
				container = client.getRates(Currency.getBase());
				log.info("exchange rates has been updated: {}", container);
				dbTransaction.setStatus(Transaction.SUCCESS);
			} catch (Exception e) {
				Cat.getProducer().logError(e);
				dbTransaction.setStatus(e);
				throw e;
			} finally {
				dbTransaction.complete();
			}
		}

		return ImmutableMap.of(
				Currency.EUR, container.getRates().get(Currency.EUR.name()),
				Currency.RUB, container.getRates().get(Currency.RUB.name()),
				Currency.USD, BigDecimal.ONE
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@CatAnnotation
	public BigDecimal convert(Currency from, Currency to, BigDecimal amount) {

		Assert.notNull(amount);

		Map<Currency, BigDecimal> rates = getCurrentRates();
		BigDecimal ratio = rates.get(to).divide(rates.get(from), 4, RoundingMode.HALF_UP);

		return amount.multiply(ratio);
	}
}
