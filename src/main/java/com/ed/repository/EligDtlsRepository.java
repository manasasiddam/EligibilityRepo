package com.ed.repository;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ed.entity.EligDtlsEntity;

public interface EligDtlsRepository extends JpaRepository<EligDtlsEntity, Serializable> {

}