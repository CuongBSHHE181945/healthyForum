package com.healthyForum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.healthyForum.repository")
@EntityScan("com.healthyForum.model")
public class HealthyForumApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthyForumApplication.class, args);
	}


}
