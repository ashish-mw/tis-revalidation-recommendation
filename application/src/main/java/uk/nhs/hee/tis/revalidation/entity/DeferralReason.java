package uk.nhs.hee.tis.revalidation.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "deferralReason")
@ApiModel(description = "Recommendation's deferral reason")
public class DeferralReason {

    @Id
    private String code;
    private String reason;
    private List<DeferralReason> deferralSubReasons;

    public DeferralReason getSubReasonByCode(final String subReason) {
        final var deferralSubReason = this.getDeferralSubReasons().stream().filter(sr -> subReason.equals(sr.getCode())).findFirst();
        return deferralSubReason.isPresent() ? deferralSubReason.get() : null;
    }

}
