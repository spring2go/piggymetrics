package io.spring2go.piggymetrics.notification.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.spring2go.piggymetrics.notification.CatAnnotation;

@FeignClient(name = "account-service")
public interface AccountServiceClient {

	@RequestMapping(method = RequestMethod.GET, value = "/accounts/{accountName}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@CatAnnotation
	String getAccount(@PathVariable("accountName") String accountName);

}
