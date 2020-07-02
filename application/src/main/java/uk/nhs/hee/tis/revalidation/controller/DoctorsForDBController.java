package uk.nhs.hee.tis.revalidation.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.nhs.hee.tis.revalidation.dto.TraineeRequestDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeSummaryDto;
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
    protected static final String UNDER_NOTICE = "underNotice";
    protected static final String UNDER_NOTICE_VALUE = "false";
    protected static final String PAGE_NUMBER = "pageNumber";
    protected static final String PAGE_NUMBER_VALUE = "0";
    protected static final String SEARCH_QUERY = "searchQuery";
    protected static final String EMPTY_STRING = "";

    @Value("${app.validation.sort.fields}")
    private List<String> sortFields;

    @Value("${app.validation.sort.order}")
    private List<String> sortOrder;

    @Autowired
    private DoctorsForDBService doctorsForDBService;

    @ApiOperation(value = "All trainee doctors information", notes = "It will return all the information about trainee doctors", response = TraineeSummaryDto.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Trainee gmc all doctors data", response = TraineeSummaryDto.class)})
    @GetMapping
    public ResponseEntity<TraineeSummaryDto> getTraineeDoctorsInformation(@RequestParam(name = SORT_COLUMN, defaultValue = SUBMISSION_DATE, required = false) final String sortColumn,
                                                                          @RequestParam(name = SORT_ORDER, defaultValue = DESC, required = false) final String sortOrder,
                                                                          @RequestParam(name = UNDER_NOTICE, defaultValue = UNDER_NOTICE_VALUE, required = false) final boolean underNotice,
                                                                          @RequestParam(name = PAGE_NUMBER, defaultValue = PAGE_NUMBER_VALUE, required = false) final int pageNumber,
                                                                          @RequestParam(name = SEARCH_QUERY, defaultValue = EMPTY_STRING, required = false) final String searchQuery) {
        final var traineeRequestDTO = TraineeRequestDto.builder()
                .sortColumn(sortColumn)
                .sortOrder(sortOrder)
                .underNotice(underNotice)
                .pageNumber(pageNumber)
                .searchQuery(searchQuery)
                .build();

        validate(traineeRequestDTO);

        final var allTraineeDoctorDetails = doctorsForDBService.getAllTraineeDoctorDetails(traineeRequestDTO);
        return ResponseEntity.ok().body(allTraineeDoctorDetails);
    }

    @ApiOperation(value = "Update admin for trainee", notes = "It will update admin to recommend trainee", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Trainee's admin has been updated", response = ResponseEntity.class)})
    @PostMapping("/{gmcNumber}/admin/{adminEmail}")
    public ResponseEntity updateAdmin(@PathVariable("gmcNumber") final String gmcNumber,
                                      @PathVariable("adminEmail") final String adminEmail) {

        doctorsForDBService.updateTraineeAdmin(gmcNumber, adminEmail);
        return ResponseEntity.ok().build();
    }

    //TODO: find a better way like separate validator
    private void validate(final TraineeRequestDto requestDTO) {
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
