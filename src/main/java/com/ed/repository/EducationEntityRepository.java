package com.ed.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ed.entity.EducationEntity;

public interface EducationEntityRepository extends JpaRepository<EducationEntity, Serializable> {

	public EducationEntity findByCaseNum(Long caseNum);

}
