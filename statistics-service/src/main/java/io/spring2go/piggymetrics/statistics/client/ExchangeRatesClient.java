package io.spring2go.piggymetrics.statistics.client;

import io.spring2go.piggymetrics.statistics.CatAnnotation;
import io.spring2go.piggymetrics.statistics.domain.Currency;
import io.spring2go.piggymetrics.statistics.domain.ExchangeRatesContainer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "${rates.url}", name = "rates-client", fallback = ExchangeRatesClientFallback.class)
public interface ExchangeRatesClient {

    @RequestMapping(method = RequestMethod.GET, value = "/latest")
    @CatAnnotation
    ExchangeRatesContainer getRates(@RequestParam("base") Currency base);

}
