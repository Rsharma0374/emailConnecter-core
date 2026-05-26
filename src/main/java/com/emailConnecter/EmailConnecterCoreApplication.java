package com.emailConnecter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main application class for the Email Connecter Core application.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class EmailConnecterCoreApplication {

	/**
	 * Main method to start the Spring Boot application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(EmailConnecterCoreApplication.class, args);
	}

}
