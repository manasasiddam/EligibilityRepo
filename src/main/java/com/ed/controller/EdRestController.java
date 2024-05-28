package com.ed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ed.response.EligibilityResponse;
import com.ed.service.EligibilityService;

@RestController
public class EdRestController {

	@Autowired
	private EligibilityService eligService;
	
	@GetMapping("/eligibility/{caseNum}")
	public EligibilityResponse determineEligibility(@PathVariable Long caseNum) {
		EligibilityResponse eligResponse = eligService.determineEligibility(caseNum);
		return eligResponse;
	}
}