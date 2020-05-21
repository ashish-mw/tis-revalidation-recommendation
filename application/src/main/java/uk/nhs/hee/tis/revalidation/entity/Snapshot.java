package uk.nhs.hee.tis.revalidation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "snapshot")
public class Snapshot {

    private String id;
    private String concerns;
    private String legacyRevalidationId;
    private String legacyTisId;
    private String gmcNumber;
    private SnapshotRevalidation revalidation;
}
