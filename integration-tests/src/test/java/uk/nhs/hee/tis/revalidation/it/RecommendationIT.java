package uk.nhs.hee.tis.revalidation.it;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.hee.tis.revalidation.RevalidationApplication;
import uk.nhs.hee.tis.revalidation.dto.TraineeInfoDTO;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;
import uk.nhs.hee.tis.revalidation.service.RecommendationService;

import java.util.List;

import static java.util.Map.of;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RevalidationApplication.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
public class RecommendationIT extends BaseIT {
    
    @Autowired
    private RecommendationService service;

    @Autowired
    private DoctorsForDBRepository repository;

    @Before
    public void setup() {
        repository.deleteAll();
        setupData();
    }
    
    @Test
    public void shouldReturnCoreDataForTrainee() throws Exception {
        repository.saveAll(List.of(doc1));
        final var coreData = of(gmcRef1, coreDTO1);
        stubCoreRequest(coreData);
        
        final TraineeInfoDTO traineeInfo = service.getTraineeInfo(gmcRef1);

        assertThat(traineeInfo.getGmcReferenceNumber(), is(gmcRef1));
        assertThat(traineeInfo.getDoctorFirstName(), is(fName1));
        assertThat(traineeInfo.getDoctorLastName(), is(lName1));
        assertThat(traineeInfo.getSubmissionDate(), is(subDate1));
        assertThat(traineeInfo.getDateAdded(), is(addedDate1));
        assertThat(traineeInfo.getUnderNotice(), is(un1.value()));
        assertThat(traineeInfo.getSanction(), is(sanction1));
        assertThat(traineeInfo.getDoctorStatus(), is(status1.value()));
        assertThat(traineeInfo.getCctDate(), is(cctDate1));
        assertThat(traineeInfo.getProgrammeName(), is(progName1));
        assertThat(traineeInfo.getProgrammeMembershipType(), is(memType1));
        assertThat(traineeInfo.getCurrentGrade(), is(grade1));
    }

    @Test
    public void shouldReturnRecommendationWhenTcsReturn404() throws Exception {
        repository.saveAll(List.of(doc1));
        stubCoreRequestReturn404();

        final TraineeInfoDTO traineeInfo = service.getTraineeInfo(gmcRef1);

        assertThat(traineeInfo.getGmcReferenceNumber(), is(gmcRef1));
        assertThat(traineeInfo.getDoctorFirstName(), is(fName1));
        assertThat(traineeInfo.getDoctorLastName(), is(lName1));
        assertThat(traineeInfo.getSubmissionDate(), is(subDate1));
        assertThat(traineeInfo.getDateAdded(), is(addedDate1));
        assertThat(traineeInfo.getUnderNotice(), is(un1.value()));
        assertThat(traineeInfo.getSanction(), is(sanction1));
        assertThat(traineeInfo.getDoctorStatus(), is(status1.value()));
        assertThat(traineeInfo.getCctDate(), is(nullValue()));
        assertThat(traineeInfo.getProgrammeName(), is(nullValue()));
        assertThat(traineeInfo.getProgrammeMembershipType(), is(nullValue()));
        assertThat(traineeInfo.getCurrentGrade(), is(nullValue()));
    }

    @Test
    public void shouldReturnRecommendationWhenTcsReturn400() throws Exception {
        repository.saveAll(List.of(doc1));
        stubCoreRequestReturn400();

        final TraineeInfoDTO traineeInfo = service.getTraineeInfo(gmcRef1);

        assertThat(traineeInfo.getGmcReferenceNumber(), is(gmcRef1));
        assertThat(traineeInfo.getDoctorFirstName(), is(fName1));
        assertThat(traineeInfo.getDoctorLastName(), is(lName1));
        assertThat(traineeInfo.getSubmissionDate(), is(subDate1));
        assertThat(traineeInfo.getDateAdded(), is(addedDate1));
        assertThat(traineeInfo.getUnderNotice(), is(un1.value()));
        assertThat(traineeInfo.getSanction(), is(sanction1));
        assertThat(traineeInfo.getDoctorStatus(), is(status1.value()));
        assertThat(traineeInfo.getCctDate(), is(nullValue()));
        assertThat(traineeInfo.getProgrammeName(), is(nullValue()));
        assertThat(traineeInfo.getProgrammeMembershipType(), is(nullValue()));
        assertThat(traineeInfo.getCurrentGrade(), is(nullValue()));
    }

    @Test
    public void shouldReturnRecommendationWhenTcsReturn500() throws Exception {
        repository.saveAll(List.of(doc1));
        stubCoreRequestReturn500();

        final TraineeInfoDTO traineeInfo = service.getTraineeInfo(gmcRef1);

        assertThat(traineeInfo.getGmcReferenceNumber(), is(gmcRef1));
        assertThat(traineeInfo.getDoctorFirstName(), is(fName1));
        assertThat(traineeInfo.getDoctorLastName(), is(lName1));
        assertThat(traineeInfo.getSubmissionDate(), is(subDate1));
        assertThat(traineeInfo.getDateAdded(), is(addedDate1));
        assertThat(traineeInfo.getUnderNotice(), is(un1.value()));
        assertThat(traineeInfo.getSanction(), is(sanction1));
        assertThat(traineeInfo.getDoctorStatus(), is(status1.value()));
        assertThat(traineeInfo.getCctDate(), is(nullValue()));
        assertThat(traineeInfo.getProgrammeName(), is(nullValue()));
        assertThat(traineeInfo.getProgrammeMembershipType(), is(nullValue()));
        assertThat(traineeInfo.getCurrentGrade(), is(nullValue()));
    }
}
