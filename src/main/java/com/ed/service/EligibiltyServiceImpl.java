package com.ed.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ed.entity.ChildrenEntity;
import com.ed.entity.CitizenAppEntity;
import com.ed.entity.CoTriggerEntity;
import com.ed.entity.DcCasesEntity;
import com.ed.entity.EducationEntity;
import com.ed.entity.EligDtlsEntity;
import com.ed.entity.IncomeEntity;
import com.ed.entity.PlanMasterEntity;
import com.ed.repository.ChildrenEntityRepository;
import com.ed.repository.CitizenAppEntityRepository;
import com.ed.repository.CoTriggerRepository;
import com.ed.repository.DcCasesEntityRepository;
import com.ed.repository.EducationEntityRepository;
import com.ed.repository.EligDtlsRepository;
import com.ed.repository.IncomeEntityRepository;
import com.ed.repository.PlanMasterEntityRepository;
import com.ed.response.EligibilityResponse;

@Service
public class EligibiltyServiceImpl implements EligibilityService {
	@Autowired
	private CitizenAppEntityRepository citizenAppEntityRepo;

	@Autowired
	private DcCasesEntityRepository dcCasesEntityRepo;

	@Autowired
	private ChildrenEntityRepository childrenEntityRepo;

	@Autowired
	private EducationEntityRepository educationEntityRepo;

	@Autowired
	private IncomeEntityRepository incomeEntityRepo;

	@Autowired
	private PlanMasterEntityRepository planMasterEntityRepo;

	@Autowired
	private EligDtlsRepository eligDtlsRepo;

	@Autowired
	private CoTriggerRepository coTriggerRepo;

	@Override
	public EligibilityResponse determineEligibility(Long caseNum) {

		Optional<DcCasesEntity> caseEntity = dcCasesEntityRepo.findById(caseNum);
		Integer planId = null;
		String planName = null;
		Integer appId = null;

		if (caseEntity.isPresent()) {
			planId = caseEntity.get().getPlanId();
			appId = caseEntity.get().getAppId();
		}

		Optional<PlanMasterEntity> planEntity = planMasterEntityRepo.findById(planId);
		if (planEntity.isPresent()) {
			PlanMasterEntity plan = planEntity.get();
			planName = plan.getPlanName();
		}

		Optional<CitizenAppEntity> findById = citizenAppEntityRepo.findById(appId);

		Integer age = 0;
		CitizenAppEntity citizenApiEntity = null;
		if (findById.isPresent()) {
			citizenApiEntity = findById.get();
			LocalDate dob = citizenApiEntity.getDateOfBirth();
			LocalDate now = LocalDate.now();
			age = Period.between(dob, now).getYears();
		}

		EligibilityResponse eligbleResponse = executePlanConditions(caseNum, planName, age);
		EligDtlsEntity eligEntity = new EligDtlsEntity();
		BeanUtils.copyProperties(eligbleResponse, eligEntity);

		eligEntity.setCaseNum(caseNum);
		eligEntity.setHolderName(citizenApiEntity.getFullName());
		eligEntity.setHolderSsn(citizenApiEntity.getSsn());
		eligDtlsRepo.save(eligEntity);

		CoTriggerEntity coEntity = new CoTriggerEntity();
		coEntity.setCaseNum(caseNum);
		coEntity.setTrgStatus("Pending");

		coTriggerRepo.save(coEntity);
		return eligbleResponse;
	}

	private EligibilityResponse executePlanConditions(Long caseNum, String planName, Integer age) {
		EligibilityResponse response = new EligibilityResponse();
		response.setPlanName(planName);

		IncomeEntity income = incomeEntityRepo.findByCaseNum(caseNum);
		if ("FDP".equals(planName)) {
			Double empIncome = income.getEmpIncome();
			if (empIncome <= 300) {
				response.setPlanStatus("Approved");
			} else {
				response.setPlanStatus("Denied");
				response.setDenialReason("High Income");
			}

		} else if ("CHLD".equals(planName)) {

			boolean ageCondition = true;
			boolean kidsCountCondition = false;
			List<ChildrenEntity> childs = childrenEntityRepo.findByCaseNum(caseNum);
			if (!childs.isEmpty()) {
				kidsCountCondition = true;
				for (ChildrenEntity entity : childs) {
					Integer childAge = entity.getChildAge();
					if (childAge > 16) {
						ageCondition = false;
						break;
					}
				}

			}
			if (income.getEmpIncome() <= 300 && kidsCountCondition && ageCondition) {
				response.setPlanStatus("Approved");
			} else {
				response.setPlanStatus("Denied");
				response.setDenialReason("Not satisfied busines rules");
			}

		} else if ("HLTH".equals(planName)) {
			Double empIncome = income.getEmpIncome();
			Double propertyIncome = income.getPropertyIncome();
			if (empIncome <= 300 && propertyIncome == 0) {
				response.setPlanStatus("Approved");
			} else {
				response.setPlanStatus("Denied");
				response.setDenialReason("High Income");
			}

		} else if ("HLTH".equals(planName)) {

			if (age >= 65) {
				response.setPlanStatus("Approved");
			} else {
				response.setPlanStatus("Denied");
				response.setDenialReason("Age not matched");
			}

		} else if ("EDCP".equals(planName)) {
			EducationEntity educationEntity = educationEntityRepo.findByCaseNum(caseNum);
			Integer graduationYear = educationEntity.getGraduationYear();
			int currentYear = LocalDate.now().getYear();
			if (income.getEmpIncome() <= 0 && graduationYear < currentYear) {
				response.setPlanStatus("Approved");
			} else {
				response.setPlanStatus("Denied");
				response.setDenialReason("Rules not satisfied");
			}
		}
		if (response.getPlanStatus() == "Approved") {

			response.setPlanStartDate(LocalDate.now());
			response.setPlanEndDate(LocalDate.now().plusMonths(6));
			response.setBenefitAmt(350.00);
		}

		return response;

	}

}
