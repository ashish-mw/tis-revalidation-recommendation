/*
 * The MIT License (MIT)
 *
 * Copyright 2021 Crown Copyright (Health Education England)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
