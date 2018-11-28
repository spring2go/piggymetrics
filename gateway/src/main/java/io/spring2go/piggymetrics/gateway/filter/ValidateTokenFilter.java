package io.spring2go.piggymetrics.gateway.filter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import io.netty.util.internal.StringUtil;

// 令牌集中校验过滤器
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "piggymetrics")
public class ValidateTokenFilter extends ZuulFilter {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ValidationConfig config;

	@Override
	public boolean shouldFilter() {
		RequestContext requestContext = RequestContext.getCurrentContext();

		HttpServletRequest request = requestContext.getRequest();
		String url = request.getRequestURI();
		String requestMethod = requestContext.getRequest().getMethod();

		// check if token validation should be enabled
		if (url.startsWith("/accounts")) {
			// no auth for demo account
			if (url.startsWith("/accounts/demo"))
				return false;
			// no auth for new account registration
			if ("POST".equals(requestMethod))
				return false;
			return true;
		}

		if (url.startsWith("/statistics") || url.startsWith("/notifications")) {
			return true;
		}

		return false;

	}

	@Override
	public Object run() throws ZuulException {
		RequestContext requestContext = RequestContext.getCurrentContext();

		String token = requestContext.getRequest().getHeader("Authorization");
		if (StringUtil.isNullOrEmpty(token)) {
			throw new ZuulException("no token found", HttpStatus.SC_UNAUTHORIZED, "no token found");
		}

		token = token.replace("Bearer ", ""); // remove prefix

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + config.getBase64Credentials());

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("token", token);
		map.add("token_type_hint", "access_token");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

		String url = config.getTokenIntrospectEndpoint();
		@SuppressWarnings("unchecked")
		Map<String, Object> resultMap = restTemplate.postForEntity(url, request, Map.class)
				.getBody();

		Boolean active = (Boolean) resultMap.get("active");

		if (active == null || !active) {
			throw new ZuulException("token inactive", HttpStatus.SC_UNAUTHORIZED, "token inactive");
		}

		String username = (String) resultMap.get("username");
		if (StringUtil.isNullOrEmpty(username)) {
			throw new ZuulException("username empty", HttpStatus.SC_UNAUTHORIZED, "username empty");
		}

		requestContext.addZuulRequestHeader("X-S2G-USERNAME", username);

		return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 5;
	}

}
