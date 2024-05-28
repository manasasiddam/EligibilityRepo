package com.ed.service;

import com.ed.response.EligibilityResponse;

public interface EligibilityService {
	
	public EligibilityResponse determineEligibility(Long caseNum);
}
