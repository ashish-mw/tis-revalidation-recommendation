package uk.nhs.hee.tis.revalidation.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api("/api/v1/doctors")
@RequestMapping("/api/v1/doctors")
public class DoctorsForDBController {

    @Autowired
    private DoctorsForDBService doctorsForDBService;

    @ApiOperation(value = "All trainee doctors information", notes = "It will return all the information about trainee doctors", response = GmcDoctorDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Trainee gmc all doctors data", response = GmcDoctorDTO.class)})
    @GetMapping
    public ResponseEntity<GmcDoctorDTO> getTraineeDoctorsInformation() {
        final var allTraineeDoctorDetails = doctorsForDBService.getAllTraineeDoctorDetails();
        return ResponseEntity.ok().body(allTraineeDoctorDetails);
    }
}
