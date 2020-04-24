package uk.nhs.hee.tis.revalidation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.dto.TraineeInfoDTO;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;

import static java.util.List.of;

@Transactional
@Service
public class RecommendationService {

    @Autowired
    private DoctorsForDBRepository doctorsForDBRepository;

    @Autowired
    private TraineeCoreService traineeCoreService;

    public TraineeInfoDTO getTraineeInfo(final String gmcId) {
        final var optionalDoctorsForDB = doctorsForDBRepository.findById(gmcId);

        if (optionalDoctorsForDB.isPresent()) {
            final var doctorsForDB = optionalDoctorsForDB.get();
            final var traineeCoreInfo = traineeCoreService.getTraineeInformationFromCore(of(gmcId));
            final var traineeInfoDTOBuilder = TraineeInfoDTO.builder()
                    .gmcReferenceNumber(doctorsForDB.getGmcReferenceNumber())
                    .doctorFirstName(doctorsForDB.getDoctorFirstName())
                    .doctorLastName(doctorsForDB.getDoctorLastName())
                    .submissionDate(doctorsForDB.getSubmissionDate())
                    .dateAdded(doctorsForDB.getDateAdded())
                    .underNotice(doctorsForDB.getUnderNotice())
                    .sanction(doctorsForDB.getSanction())
                    .doctorStatus(doctorsForDB.getDoctorStatus());

            if (traineeCoreInfo.get(gmcId) != null) {
                final var traineeCoreDTO = traineeCoreInfo.get(gmcId);
                traineeInfoDTOBuilder
                        .cctDate(traineeCoreDTO.getCctDate())
                        .programmeName(traineeCoreDTO.getProgrammeName())
                        .programmeMembershipType(traineeCoreDTO.getProgrammeMembershipType())
                        .currentGrade(traineeCoreDTO.getCurrentGrade());
            }

            return traineeInfoDTOBuilder.build();
        }

        return null;
    }
}
