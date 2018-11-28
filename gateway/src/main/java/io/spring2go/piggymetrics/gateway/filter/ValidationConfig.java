package io.spring2go.piggymetrics.gateway.filter;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix="piggymetrics")
public class ValidationConfig {
	@NotNull private String tokenIntrospectEndpoint;
	@NotNull private String clientCredentials;
	private String base64Credentials;
	
	public String getClientCredentials() {
		return clientCredentials;
	}
	
	public void setClientCredentials(String creds) {
		clientCredentials = creds;
	}
	
	public String getTokenIntrospectEndpoint() {
		return tokenIntrospectEndpoint;
	}
	
	public void setTokenIntrospectEndpoint(String endpoint) {
		tokenIntrospectEndpoint = endpoint;
	}
	
	public String getBase64Credentials() {
		return base64Credentials;
	}
	
	@PostConstruct
	public void initBase64Creds() {
		byte[] plainCredsBytes = clientCredentials.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		base64Credentials = new String(base64CredsBytes);
	}
}
