package com.ed.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ed.entity.DcCasesEntity;

public interface DcCasesEntityRepository extends JpaRepository<DcCasesEntity, Serializable> {

}
