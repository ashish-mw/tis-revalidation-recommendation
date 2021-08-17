package uk.nhs.hee.tis.revalidation.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.revalidation.dto.DeferralReasonDto;
import uk.nhs.hee.tis.revalidation.entity.DeferralReason;
import uk.nhs.hee.tis.revalidation.entity.Status;
import uk.nhs.hee.tis.revalidation.exception.RecommendationException;
import uk.nhs.hee.tis.revalidation.repository.DeferralReasonRepository;

@Slf4j
@Service
public class DeferralReasonService {

  @Autowired
  private DeferralReasonRepository deferralReasonRepository;

  //get All deferral reasons
  public List<DeferralReasonDto> getAllDeferralReasons() {
    final var deferralReasons = deferralReasonRepository.findAll();
    return deferralReasons.stream().map(dr -> convertToDTO(dr)).collect(toList());
  }

  //get All CURRENT deferral reasons
  public List<DeferralReasonDto> getAllCurrentDeferralReasons() {
    final var deferralReasons = deferralReasonRepository.findAllByStatus(Status.CURRENT);
    return deferralReasons.stream().map(dr -> convertToDTO(dr)).collect(toList());
  }

  //get deferral reason by code
  public DeferralReason getDeferralReasonByCode(final String reasonCode) {
    final var deferralReason = deferralReasonRepository.findById(reasonCode);
    if (deferralReason.isEmpty()) {
      throw new RecommendationException("Deferral Reason code is invalid");
    }
    return deferralReason.get();
  }

  //get deferral subreason by code and sub reason code
  public DeferralReason getDeferralSubReasonByReasonCodeAndReasonSubCode(final String reasonCode,
      final String reasonSubCode) {
    final var deferralReason = getDeferralReasonByCode(reasonCode);
    return deferralReason.getSubReasonByCode(reasonSubCode);
  }

  private DeferralReasonDto convertToDTO(DeferralReason dr) {
    return DeferralReasonDto.builder().code(dr.getCode()).reason(dr.getReason())
        .subReasons(dr.getDeferralSubReasons().stream()
            .map(sub -> DeferralReasonDto.builder().code(sub.getCode())
                .reason(sub.getReason()).build()).collect(toList())).build();
  }
}
