package uk.nhs.hee.tis.revalidation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TraineeRequestDTO {
    private String sortColumn;
    private String sortOrder;
    private boolean underNotice;
    private int pageNumber;
    private String searchQuery;
}
