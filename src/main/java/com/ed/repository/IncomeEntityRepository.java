package com.ed.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ed.entity.IncomeEntity;

public interface IncomeEntityRepository extends JpaRepository<IncomeEntity, Serializable> {

	public IncomeEntity findByCaseNum(Long caseNum);
}
