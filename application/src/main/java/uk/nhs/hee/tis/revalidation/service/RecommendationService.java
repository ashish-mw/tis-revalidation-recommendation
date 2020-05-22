package uk.nhs.hee.tis.revalidation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uk.nhs.hee.tis.revalidation.dto.RecommendationDTO;
import uk.nhs.hee.tis.revalidation.dto.RevalidationDTO;
import uk.nhs.hee.tis.revalidation.entity.RevalidationGmcOutcome;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;
import uk.nhs.hee.tis.revalidation.repository.SnapshotRepository;

import java.util.List;

import static java.lang.String.format;
import static java.util.List.of;
import static java.util.stream.Collectors.toList;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.formatDate;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.formatDateTime;

@Slf4j
@Transactional
@Service
public class RecommendationService {

    @Autowired
    private DoctorsForDBRepository doctorsForDBRepository;

    @Autowired
    private SnapshotRepository snapshotRepository;

    @Autowired
    private TraineeCoreService traineeCoreService;

    public RecommendationDTO getTraineeInfo(final String gmcId) {
        log.info("Fetching trainee info for GmcId: {}", gmcId);
        final var optionalDoctorsForDB = doctorsForDBRepository.findById(gmcId);

        if (optionalDoctorsForDB.isPresent()) {
            final var doctorsForDB = optionalDoctorsForDB.get();
            final var traineeCoreInfo = traineeCoreService.getTraineeInformationFromCore(of(gmcId));

            final var recommendationDTOBuilder = RecommendationDTO.builder()
                    .fullName(format("%s %s", doctorsForDB.getDoctorFirstName(), doctorsForDB.getDoctorLastName()))
                    .gmcNumber(doctorsForDB.getGmcReferenceNumber());
            if (traineeCoreInfo.get(gmcId) != null) {
                final var traineeCoreDTO = traineeCoreInfo.get(gmcId);
                recommendationDTOBuilder.cctDate(traineeCoreDTO.getCctDate());
                recommendationDTOBuilder.currentGrade(traineeCoreDTO.getCurrentGrade());
                recommendationDTOBuilder.programmeMembershipType(traineeCoreDTO.getProgrammeMembershipType());
            }

            recommendationDTOBuilder.revalidations(getCurrentAndLegacyRevalidation(gmcId));
            return recommendationDTOBuilder.build();
        }

        return null;
    }

    private List<RevalidationDTO> getCurrentAndLegacyRevalidation(final String gmcId) {
        log.info("Fetching snapshot record for GmcId: {}", gmcId);
        final var snapshots = snapshotRepository.findByGmcNumber(gmcId);

        return snapshots.stream().map(snapshot -> {
            final var revalidation = snapshot.getRevalidation();
            return RevalidationDTO.builder()
                    .deferralDate(revalidation.getDeferralDate())
                    .deferralReason(revalidation.getDeferralReason())
                    .deferralComment(revalidation.getDeferralComment())
                    .gmcOutcome(RevalidationGmcOutcome.APPROVED.name()) //TODO: fetch information from 'checkStatusForRecommendation'
                    .revalidationStatus(toUpperCase(revalidation.getRevalidationStatusCode()))
                    .revalidationType(toUpperCase(revalidation.getProposedOutcomeCode()))
                    .gmcSubmissionDate(formatDateTime(revalidation.getGmcSubmissionDateTime()))
                    .actualSubmissionDate(formatDate(revalidation.getSubmissionDate()))
                    .admin(revalidation.getAdmin())
                    .build();
        }).collect(toList());
    }

    private String toUpperCase(final String code) {
        return !StringUtils.isEmpty(code) ? code.toUpperCase() : code;
    }
}
