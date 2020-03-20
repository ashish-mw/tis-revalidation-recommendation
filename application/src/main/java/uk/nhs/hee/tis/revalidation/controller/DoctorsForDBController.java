package uk.nhs.hee.tis.revalidation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.hee.tis.revalidation.dto.GmcDoctorDTO;
import uk.nhs.hee.tis.revalidation.service.DoctorsForDBService;

@Slf4j
@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorsForDBController {

    @Autowired
    private DoctorsForDBService doctorsForDBService;

    @GetMapping
    public ResponseEntity<GmcDoctorDTO> getTraineeDoctorsInformation() {
        final var allTraineeDoctorDetails = doctorsForDBService.getAllTraineeDoctorDetails();
        return ResponseEntity.ok().body(allTraineeDoctorDetails);
    }
}
