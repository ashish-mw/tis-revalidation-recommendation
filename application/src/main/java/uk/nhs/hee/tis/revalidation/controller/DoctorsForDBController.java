package uk.nhs.hee.tis.revalidation.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.hee.tis.revalidation.dto.TraineeDoctorDTO;
import uk.nhs.hee.tis.revalidation.dto.RevalidationRequestDTO;
import uk.nhs.hee.tis.revalidation.service.DoctorsForDBService;

import java.util.List;

@Slf4j
@RestController
@Api("/api/v1/doctors")
@RequestMapping("/api/v1/doctors")
public class DoctorsForDBController {

    protected static final String SORT_COLUMN = "sortColumn";
    protected static final String SORT_ORDER = "sortOrder";
    protected static final String SUBMISSION_DATE = "submissionDate";
    protected static final String DESC = "desc";
    protected static final String ASC = "asc";

    @Value("${app.validation.sort.fields}")
    private List<String> sortFields;

    @Value("${app.validation.sort.order}")
    private List<String> sortOrder;

    @Autowired
    private DoctorsForDBService doctorsForDBService;

    @ApiOperation(value = "All trainee doctors information", notes = "It will return all the information about trainee doctors", response = TraineeDoctorDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Trainee gmc all doctors data", response = TraineeDoctorDTO.class)})
    @GetMapping
    public ResponseEntity<TraineeDoctorDTO> getTraineeDoctorsInformation(@RequestParam(name = SORT_COLUMN, defaultValue = SUBMISSION_DATE) final String sortColumn,
                                                                         @RequestParam(name = SORT_ORDER, defaultValue = DESC) final String sortOrder) {
        final var revalidationRequestDTO = RevalidationRequestDTO.builder()
                .sortColumn(sortColumn)
                .sortOrder(sortOrder)
                .build();

        validate(revalidationRequestDTO);

        final var allTraineeDoctorDetails = doctorsForDBService.getAllTraineeDoctorDetails(revalidationRequestDTO);
        return ResponseEntity.ok().body(allTraineeDoctorDetails);
    }

    //TODO: find a better way like separate validator
    private void validate(final RevalidationRequestDTO requestDTO) {
        if(!sortFields.contains(requestDTO.getSortColumn())) {
            log.warn("Invalid sort column name provided: {}, revert to default column: {}", requestDTO.getSortColumn(), SUBMISSION_DATE);
            requestDTO.setSortColumn(SUBMISSION_DATE);
        }

        if (!sortOrder.contains(requestDTO.getSortOrder())) {
            log.warn("Invalid sort order provided: {}, revert to default order: {}", requestDTO.getSortOrder(), DESC);
            requestDTO.setSortOrder(DESC);
        }

    }
}
