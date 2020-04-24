package uk.nhs.hee.tis.revalidation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeCoreDTO {

    private String gmcId;
    private LocalDate cctDate;
    private String programmeMembershipType;
    private String programmeName;
    private String currentGrade;
}
