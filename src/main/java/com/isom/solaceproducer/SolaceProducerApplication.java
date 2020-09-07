package com.isom.solaceproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@EnableEncryptableProperties
@SpringBootApplication
public class SolaceProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolaceProducerApplication.class, args);
	}

}
