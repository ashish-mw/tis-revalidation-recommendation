package uk.nhs.hee.tis.revalidation.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.nhs.hee.tis.revalidation.dto.TraineeInfoDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationRecordDto;
import uk.nhs.hee.tis.revalidation.service.RecommendationService;
import uk.nhs.hee.tis.revalidation.validator.TraineeRecommendationRecordDTOValidator;

import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@Api("/api/recommendation")
@RequestMapping("/api/recommendation")
public class RecommendationController {

    @Autowired
    private RecommendationService service;

    @Autowired
    private TraineeRecommendationRecordDTOValidator traineeRecommendationRecordDTOValidator;

    @ApiOperation(value = "Get recommendation details of a trainee", notes = "It will return trainee's recommendation details", response = TraineeInfoDto.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Trainee recommendation details", response = TraineeInfoDto.class)})
    @GetMapping("/{gmcId}")
    public ResponseEntity<TraineeRecommendationDto> getRecommendation(@PathVariable("gmcId") final String gmcId) {
        log.info("Receive request to fetch recommendations for GmcId: {}", gmcId);
        if (Objects.nonNull(gmcId)) {
            final var recommendationDTO = service.getTraineeInfo(gmcId);
            return ResponseEntity.ok().body(recommendationDTO);
        }

        return new ResponseEntity<>(TraineeRecommendationDto.builder().build(), HttpStatus.OK);
    }

    @ApiOperation(value = "Save new recommendation", notes = "It will allow user to save new recommendation", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "New recommendation is saved", response = ResponseEntity.class)})
    @PostMapping
    public ResponseEntity saveRecommendation(@RequestBody final TraineeRecommendationRecordDto traineeRecommendationDTO,
                                             final BindingResult bindingResult) {

        log.info("recommendation: {}", traineeRecommendationDTO);
        traineeRecommendationRecordDTOValidator.validate(traineeRecommendationDTO, bindingResult);
        if(bindingResult.hasErrors()) {
            return buildErrorResponse(bindingResult);
        }
        service.saveRecommendation(traineeRecommendationDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "update recommendation", notes = "It will allow user to update recommendation", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Recommendation is updated", response = ResponseEntity.class)})
    @PutMapping
    public ResponseEntity updateRecommendation(@RequestBody final TraineeRecommendationRecordDto traineeRecommendationDTO,
                                             final BindingResult bindingResult) {

        log.info("recommendation: {}", traineeRecommendationDTO);
        if (StringUtils.isEmpty(traineeRecommendationDTO.getRecommendationId())) {
            return new ResponseEntity<>("Recommendation Id should not be empty", HttpStatus.BAD_REQUEST);
        }

        traineeRecommendationRecordDTOValidator.validate(traineeRecommendationDTO, bindingResult);
        if(bindingResult.hasErrors()) {
            return buildErrorResponse(bindingResult);
        }
        service.updateRecommendation(traineeRecommendationDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Submit recommendation to gmc", notes = "It will allow user to submit recommendation to gmc", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "New recommendation is submitted to gmc", response = ResponseEntity.class)})
    @PostMapping("/{gmcId}/submit/{recommendationId}")
    public ResponseEntity submitRecommendation(@PathVariable("gmcId") final String gmcNumber,
                                               @PathVariable("recommendationId") final String recommendationId) {

        service.submitRecommendation(recommendationId, gmcNumber);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    private ResponseEntity buildErrorResponse(final BindingResult bindingResult) {
        final var errors = bindingResult.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
