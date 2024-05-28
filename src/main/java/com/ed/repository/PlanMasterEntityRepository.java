package com.ed.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ed.entity.PlanMasterEntity;

public interface PlanMasterEntityRepository extends JpaRepository<PlanMasterEntity, Serializable> {

}
