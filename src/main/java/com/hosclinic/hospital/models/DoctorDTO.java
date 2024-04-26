package com.hosclinic.hospital.models;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

public class DoctorDTO {

    @NotEmpty(message = "The name is required")
    private String name;

    @Size(min = 10, message = "The description should be at least 10 characters")
    @Size(max = 100, message = "The description cannot exceed 100 characters")
    private String description;

    @NotEmpty(message = "The specialty is required")
    private String especialidad;

    @NotEmpty(message = "The turno is required")
    private String turno;

    private MultipartFile imageFile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
