package edu.pk.qurduplex.userDataService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserDataServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserDataServiceApplication.class, args);
    }
}