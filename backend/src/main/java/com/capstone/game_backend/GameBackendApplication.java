package com.capstone.game_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class GameBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameBackendApplication.class, args);
	}

}
