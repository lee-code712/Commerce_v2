package com.digital.v2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.digital.v2.lucene.DataHandler;

@SpringBootApplication
@EnableScheduling
public class CommerceApplication {

	public static void main(String[] args) {
		System.out.println("hello");
		SpringApplication.run(CommerceApplication.class, args);
		new DataHandler();
	}

}
