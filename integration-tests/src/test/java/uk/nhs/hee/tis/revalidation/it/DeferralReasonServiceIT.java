package uk.nhs.hee.tis.revalidation.it;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.hee.tis.revalidation.RevalidationApplication;
import uk.nhs.hee.tis.revalidation.exception.RecommendationException;
import uk.nhs.hee.tis.revalidation.service.DeferralReasonService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RevalidationApplication.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
public class DeferralReasonServiceIT {

    @Autowired
    private DeferralReasonService service;

    @Test
    public void testGetDeferralReasonByCode() {
        final var deferralReasonByCode = service.getDeferralReasonByCode("1");
        assertNotNull(deferralReasonByCode);
        assertThat(deferralReasonByCode.getCode(), is("1"));
        assertThat(deferralReasonByCode.getReason(), is("Insufficient evidence for a positive recommendation"));
        assertThat(deferralReasonByCode.getDeferralSubReasons(), hasSize(8));
    }

    @Test
    public void testGetDeferralReasonByCodeHavingNoSubReason() {
        final var deferralReasonByCode = service.getDeferralReasonByCode("2");
        assertNotNull(deferralReasonByCode);
        assertThat(deferralReasonByCode.getCode(), is("2"));
        assertThat(deferralReasonByCode.getReason(), is("The doctor is subject to an ongoing process"));
        assertThat(deferralReasonByCode.getDeferralSubReasons(), hasSize(0));
    }

    @Test
    public void testGetDeferralReasonAndSubReasonByCode() {
        final var deferralReasonByCode = service.getDeferralSubReasonByReasonCodeAndReasonSubCode("1", "4");
        assertNotNull(deferralReasonByCode);
        assertThat(deferralReasonByCode.getCode(), is("4"));
        assertThat(deferralReasonByCode.getReason(), is("CPD"));
    }

    @Test
    public void testGetAllDeferralReason() {
        final var allDeferralReasons = service.getAllDeferralReasons();
        assertThat(allDeferralReasons, hasSize(2));
    }

    @Test(expected = RecommendationException.class)
    public void shouldThrowExceptionWhenDeferralCodeIsInvalid() {
        final var allDeferralReasons = service.getDeferralReasonByCode("6");
    }

}
