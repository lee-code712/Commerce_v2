package com.digital.v3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.digital.v3.lucene.DataHandler;

@SpringBootApplication
public class CommerceApplication {

	public static void main(String[] args) {
		System.out.println("hello");
		SpringApplication.run(CommerceApplication.class, args);
		new DataHandler();
	}

}
