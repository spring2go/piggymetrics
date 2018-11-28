package io.spring2go.piggymetrics.gateway;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

import io.spring2go.cathelper.CatRestInterceptor;

@SpringBootApplication
@EnableDiscoveryClient
@EnableApolloConfig
@EnableZuulProxy
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
	
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		// for CAT tracing
		restTemplate.setInterceptors(Collections.singletonList(new CatRestInterceptor()));
		return restTemplate;
	}
	
}
