package com.ed.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ed.entity.CitizenAppEntity;

public interface CitizenAppEntityRepository extends JpaRepository<CitizenAppEntity, Serializable> {

}
