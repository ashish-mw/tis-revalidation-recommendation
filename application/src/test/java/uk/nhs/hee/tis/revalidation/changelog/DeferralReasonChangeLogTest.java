package uk.nhs.hee.tis.revalidation.changelog;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.hee.tis.revalidation.entity.DeferralReason;

@ExtendWith(MockitoExtension.class)
class DeferralReasonChangeLogTest {

  private DeferralReasonChangeLog changeLog;

  @Mock
  private MongockTemplate template;

  @BeforeEach
  void setUp() {
    changeLog = new DeferralReasonChangeLog();
  }

  @Test
  void shouldAddInitialDeferralReasons() {
    changeLog.insertInitialDeferralReason(template);

    ArgumentCaptor<Collection> collectionCaptor = ArgumentCaptor.forClass(Collection.class);
    verify(template).insertAll(collectionCaptor.capture());

    Collection<?> deferralReasons = collectionCaptor.getValue();
    assertThat("Unexpected collection size.", deferralReasons.size(), is(9));
    Set<String> deferralReasonsValues = deferralReasons.stream()
        .filter(dr -> dr instanceof DeferralReason)
        .map(dr -> ((DeferralReason) dr).getAbbr())
        .collect(Collectors.toSet());

    assertThat("Unexpected number of deferral reasons.", deferralReasonsValues.size(),
        is(9));
    assertThat("Unexpected deferral reasons.", deferralReasonsValues,
        hasItems("INSUFFICIENT_EVIDENCE",
            "ONGOING_PROCESS",
            "BELOW_1_YEAR_TO_CCT",
            "SICK_CARERS_LEAVE",
            "PARENTAL_LEAVE",
            "EXAM_FAILURE",
            "OTHER",
            "OUT_OF_CLINICAL_TRAINING",
            "BELOW_5_YEARS_FULL_REG"));
  }
}
