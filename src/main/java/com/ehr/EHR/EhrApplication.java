package com.ehr.EHR;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EhrApplication {

	public static void main(String[] args) {
		SpringApplication.run(EhrApplication.class, args);
	}
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper()
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	}
}
