package com.ed.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {

	@Bean
	GroupedOpenApi handleApi() {
		return GroupedOpenApi.builder().group("ElibilityDeterminationModule").packagesToScan("com.ed.controller").build();
	}

}
