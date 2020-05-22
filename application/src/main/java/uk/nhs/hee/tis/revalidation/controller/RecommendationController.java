package uk.nhs.hee.tis.revalidation.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.hee.tis.revalidation.dto.RecommendationDTO;
import uk.nhs.hee.tis.revalidation.dto.TraineeInfoDTO;
import uk.nhs.hee.tis.revalidation.service.RecommendationService;

import java.util.Objects;

@Slf4j
@RestController
@Api("/api/recommendation")
@RequestMapping("/api/recommendation")
public class RecommendationController {

    @Autowired
    private RecommendationService service;

    @ApiOperation(value = "Get recommendation details of a trainee", notes = "It will return trainee's recommendation details", response = TraineeInfoDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Trainee recommendation details", response = TraineeInfoDTO.class)})
    @GetMapping("/{gmcId}")
    public ResponseEntity<RecommendationDTO> getRecommendation(@PathVariable("gmcId") final String gmcId) {
        log.info("Receive request to fetch recommendations for GmcId: {}", gmcId);
        if (Objects.nonNull(gmcId)) {
            final var recommendationDTO = service.getTraineeInfo(gmcId);
            return ResponseEntity.ok().body(recommendationDTO);
        }

        return new ResponseEntity<>(RecommendationDTO.builder().build(), HttpStatus.OK);
    }
}
