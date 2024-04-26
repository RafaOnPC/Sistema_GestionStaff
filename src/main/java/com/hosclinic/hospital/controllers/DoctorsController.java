package com.hosclinic.hospital.controllers;

import com.hosclinic.hospital.models.Doctor;
import com.hosclinic.hospital.models.DoctorDTO;
import com.hosclinic.hospital.services.DoctorsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/doctors")
public class DoctorsController {

    @Autowired
    private DoctorsRepository doctorsRepository;

    @GetMapping("/principal")
    public String principalPage(){
        return "/static/index";
    }

    @GetMapping({"", "/"})
    public String showProductList(Model model){
            List<Doctor> doctors = doctorsRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            //List<Doctor> doctors = doctorsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            model.addAttribute("doctors", doctors);
            System.out.println("Doctors List:");
            for (Doctor doctor : doctors) {
                System.out.println(doctor.toString());
            }
            return "doctors/list";

        }

    @GetMapping("/create")
    public String showCreatePage(Model model){
        DoctorDTO doctorDTO = new DoctorDTO();
        model.addAttribute("doctorDTO", doctorDTO);
        return "doctors/createDoctor";
    }

    @PostMapping("/create")
    public String createDoctor(
            @Valid @ModelAttribute DoctorDTO doctorDTO,
            BindingResult result
    ) throws IOException {
        if(doctorDTO.getImageFile().isEmpty()){
            result.addError(new FieldError("doctorDTO", "imageFile", "The image file is not submit"));
        }

        if(result.hasErrors()){
            return "doctors/createDoctor";
        }

        //Save the image
        MultipartFile image = doctorDTO.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try{
            String uploadDir = "public/imagenes/";
            Path uploadPath = Paths.get(uploadDir);

            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = image.getInputStream()){
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        }catch(Exception ex){
            System.out.println("Exception:" + ex.getMessage());
        }

        Doctor doctor = new Doctor();
        doctor.setName(doctorDTO.getName());
        doctor.setEspecialidad(doctorDTO.getEspecialidad());
        doctor.setTurno(doctorDTO.getTurno());
        doctor.setDescription(doctorDTO.getDescription());
        doctor.setCreatedAt(createdAt);
        doctor.setImageFileName(storageFileName);

        doctorsRepository.save(doctor);

        return "redirect:/doctors";
    }

    @GetMapping("/edit")
    public String showEditPage(
            Model model,
            @RequestParam int id
    ){
        try{
            Doctor doctor = doctorsRepository.findById(id).get();
            model.addAttribute("doc", doctor);

            DoctorDTO doctorDTO = new DoctorDTO();
            doctorDTO.setName(doctor.getName());
            doctorDTO.setEspecialidad(doctor.getEspecialidad());
            doctorDTO.setDescription(doctor.getDescription());
            doctorDTO.setTurno(doctor.getTurno());
            model.addAttribute("doctorDTO", doctorDTO);

        }catch(Exception ex){
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/doctors";
        }

        return "doctors/EditDoctor";
    }

    @PostMapping("/edit")
    public String updateDoctor(
            Model model,
            @RequestParam int id,
            @Valid @ModelAttribute DoctorDTO doctorDTO,
            BindingResult result
    ){
        try {
            Doctor doctor = doctorsRepository.findById(id).get();
            model.addAttribute("doc", doctor);

            if(result.hasErrors()){
                return "doctors/EditDoctor";
            }

            if(!doctorDTO.getImageFile().isEmpty()){
                //delete old image
                String uploadDir = "public/imagenes/";
                Path oldImagePath = Paths.get(uploadDir + doctor.getImageFileName());

                try{
                    Files.delete(oldImagePath);
                }catch(Exception ex){
                    System.out.println("Exception: " + ex.getMessage());
                }

                //Save the new image
                MultipartFile image = doctorDTO.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()){
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }

                doctor.setImageFileName(storageFileName);
            }

            doctor.setName(doctorDTO.getName());
            doctor.setEspecialidad(doctorDTO.getEspecialidad());
            doctor.setTurno(doctorDTO.getTurno());
            doctor.setDescription(doctorDTO.getDescription());

            doctorsRepository.save(doctor);

        }catch(Exception ex){
            System.out.println("Exception: " + ex.getMessage());
        }

        return "redirect:/doctors";
    }

    @GetMapping("/delete")
    public String deleteProduct(
            @RequestParam int id
    ){
        try{
            Doctor doctor = doctorsRepository.findById(id).get();

            //delete old image
            Path imagePath = Paths.get("public/imagenes" + doctor.getImageFileName());

            try{
                Files.delete(imagePath);
            }catch(Exception ex){
                System.out.println("Exception: " + ex.getMessage());
            }

            //Delete the doctor
            doctorsRepository.delete(doctor);

        }catch(Exception ex){
            System.out.println("Exception: " + ex.getMessage());
        }

        return "redirect:/doctors";
    }


}


