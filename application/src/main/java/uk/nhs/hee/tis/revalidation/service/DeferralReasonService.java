package uk.nhs.hee.tis.revalidation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.revalidation.dto.DeferralReasonDto;
import uk.nhs.hee.tis.revalidation.entity.DeferralReason;
import uk.nhs.hee.tis.revalidation.exception.InvalidDeferralReasonException;
import uk.nhs.hee.tis.revalidation.repository.DeferralReasonRepository;

import javax.annotation.PostConstruct;
import java.util.List;

import static java.util.stream.Collectors.toList;

//Its a temporary class to create a Deferral Reason table in Database. Will get rid of this once we have Reference DB access
@Slf4j
@Service
public class DeferralReasonService {

    @Autowired
    private DeferralReasonRepository deferralReasonRepository;

    @PostConstruct
    public void init()  {
        insertDeferralReason();
    }

    //insert DeferralReason's data in DB on startup
    public void insertDeferralReason() {
        final var insufficientEvidence = buildDeferralReason("1", "Insufficient evidence", buildDeferralSubReason("1"));
        final var doctorSubjectToProcess = buildDeferralReason("2", "The doctor is subject to an ongoing process", buildDeferralSubReason("2"));
        deferralReasonRepository.saveAll(List.of(insufficientEvidence, doctorSubjectToProcess));
    }

    //get All deferral reasons
    public List<DeferralReasonDto> getAllDeferralReasons() {
        final var deferralReasons = deferralReasonRepository.findAll();
        return deferralReasons.stream().map(dr ->  {
            return convertToDTO(dr);
        }).collect(toList());
    }

    //get deferral reason by code
    public DeferralReason getDeferralReasonByCode(final String reasonCode) {
        final var deferralReason = deferralReasonRepository.findById(reasonCode);
        if (deferralReason.isEmpty()) {
            throw new InvalidDeferralReasonException("Deferral Reason code is invalid");
        }
        return deferralReason.get();
    }

    //get deferral subreason by code and sub reason code
    public DeferralReason getDeferralSubReasonByReasonCodeAndReasonSubCode(final String reasonCode, final String reasonSubCode) {
        final var deferralReason = getDeferralReasonByCode(reasonCode);
        return deferralReason.getSubReasonByCode(reasonSubCode);
    }

    private DeferralReason buildDeferralReason(final String code, final String reason, final List<DeferralReason> subReason){
        return DeferralReason.builder()
                .code(code)
                .reason(reason)
                .deferralSubReasons(subReason)
                .build();
    }

    private List<DeferralReason> buildDeferralSubReason(final String reasonCode) {
        if ("1".equalsIgnoreCase(reasonCode)) {
            return List.of(DeferralReason.builder().code("1").reason("Appraisal activity").build(),
                    DeferralReason.builder().code("2").reason("Colleague feedback").build(),
                    DeferralReason.builder().code("3").reason("Compliments and Complaints").build(),
                    DeferralReason.builder().code("4").reason("CPD").build(),
                    DeferralReason.builder().code("5").reason("Interruption to practice").build(),
                    DeferralReason.builder().code("6").reason("Patient feedback").build(),
                    DeferralReason.builder().code("7").reason("QIA").build(),
                    DeferralReason.builder().code("8").reason("The doctor is subject to an ongoing process").build()
            );
        }
            return List.of();
    }

    private DeferralReasonDto convertToDTO(DeferralReason dr) {
        return DeferralReasonDto.builder().code(dr.getCode()).reason(dr.getReason())
                .subReasons(dr.getDeferralSubReasons().stream().map(sub -> DeferralReasonDto.builder().code(sub.getCode())
                        .reason(sub.getReason()).build()).collect(toList())).build();
    }
}
