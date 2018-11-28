package io.spring2go.piggymetrics.notification.controller;

import io.spring2go.piggymetrics.notification.domain.Recipient;
import io.spring2go.piggymetrics.notification.service.RecipientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/recipients")
public class RecipientController {

	@Autowired
	private RecipientService recipientService;

	@RequestMapping(path = "/current", method = RequestMethod.GET)
	public Object getCurrentNotificationsSettings(@RequestHeader("X-S2G-USERNAME") String accountName) {
		return recipientService.findByAccountName(accountName);
	}

	@RequestMapping(path = "/current", method = RequestMethod.PUT)
	public Object saveCurrentNotificationsSettings(@RequestHeader("X-S2G-USERNAME") String accountName, @Valid @RequestBody Recipient recipient) {
		return recipientService.save(accountName, recipient);
	}
}
