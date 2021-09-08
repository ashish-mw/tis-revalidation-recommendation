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

package uk.nhs.hee.tis.revalidation.it;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import uk.nhs.hee.tis.revalidation.RevalidationApplication;
import uk.nhs.hee.tis.revalidation.exception.RecommendationException;
import uk.nhs.hee.tis.revalidation.service.DeferralReasonService;

@Slf4j
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = RevalidationApplication.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
class DeferralReasonServiceIT {

  @Autowired
  private DeferralReasonService service;

  @Test
  void testGetDeferralReasonByCode() {
    final var deferralReasonByCode = service.getDeferralReasonByCode("1");
    assertNotNull(deferralReasonByCode);
    assertThat(deferralReasonByCode.getCode(), is("1"));
    assertThat(deferralReasonByCode.getReason(),
        is("Insufficient evidence for a positive recommendation"));
    assertThat(deferralReasonByCode.getDeferralSubReasons(), hasSize(8));
  }

  @Test
  void testGetDeferralReasonByCodeHavingNoSubReason() {
    final var deferralReasonByCode = service.getDeferralReasonByCode("2");
    assertNotNull(deferralReasonByCode);
    assertThat(deferralReasonByCode.getCode(), is("2"));
    assertThat(deferralReasonByCode.getReason(), is("The doctor is subject to an ongoing process"));
    assertThat(deferralReasonByCode.getDeferralSubReasons(), hasSize(0));
  }

  @Test
  void testGetDeferralReasonAndSubReasonByCode() {
    final var deferralReasonByCode = service
        .getDeferralSubReasonByReasonCodeAndReasonSubCode("1", "4");
    assertNotNull(deferralReasonByCode);
    assertThat(deferralReasonByCode.getCode(), is("4"));
    assertThat(deferralReasonByCode.getReason(), is("CPD"));
  }

  @Test
  void testGetAllDeferralReason() {
    final var allDeferralReasons = service.getAllCurrentDeferralReasons();
    assertThat(allDeferralReasons, hasSize(2));
  }

  @Test
  void shouldThrowExceptionWhenDeferralCodeIsInvalid() {
    Assertions.assertThrows(RecommendationException.class, () -> {
      final var allDeferralReasons = service.getDeferralReasonByCode("6");
    });
  }

}
