package com.hosclinic.hospital.services;

import com.hosclinic.hospital.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorsRepository extends JpaRepository<Doctor, Integer> {
}
