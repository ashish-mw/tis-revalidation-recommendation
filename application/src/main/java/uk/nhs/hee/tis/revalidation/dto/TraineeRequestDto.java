package uk.nhs.hee.tis.revalidation.dto;

import io.swagger.annotations.ApiModel;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(description = "Request for trainee data for summary page")
public class TraineeRequestDto {
    private String sortColumn;
    private String sortOrder;
    private boolean underNotice;
    private int pageNumber;
    private List<String> dbcs;
    private String searchQuery;
}
