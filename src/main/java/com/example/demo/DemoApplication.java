package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale de l’application Spring Boot.
 * Lance le démarrage de l’application.
 */
@SpringBootApplication
public class DemoApplication {

	/**
	 * Point d’entrée principal de l’application Spring Boot.
	 *
	 * @param args arguments de ligne de commande
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
