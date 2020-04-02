package uk.nhs.hee.tis.revalidation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RevalidationRequestDTO {
    private String sortColumn;
    private String sortOrder;
}
