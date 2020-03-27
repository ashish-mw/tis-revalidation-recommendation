package uk.nhs.hee.tis.revalidation.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "List of trainee doctors with total count")
public class GmcDoctorDTO {

    private List<DoctorsForDBDTO> doctorsForDB;
    private int count;
}
