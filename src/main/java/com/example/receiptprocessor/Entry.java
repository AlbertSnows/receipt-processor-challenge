package com.example.receiptprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.receiptprocessor.data.repositories")
public class Entry {

	public static void main(String[] args) {
		SpringApplication.run(Entry.class, args);
	}

}
