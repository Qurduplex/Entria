package edu.pk.qurduplex.appRegistryService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AppRegistryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppRegistryServiceApplication.class, args);
	}
}
