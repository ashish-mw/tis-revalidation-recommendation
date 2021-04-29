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

package uk.nhs.hee.tis.revalidation.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.RecommendationStatus;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.service.GmcDoctorSyncService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(GmcDoctorSyncController.class)
public class GmcDoctorSyncControllerTest {

  private final List<DoctorsForDB> allDoctors = new ArrayList<>();
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GmcDoctorSyncService gmcDoctorSyncService;

  @InjectMocks
  private GmcDoctorSyncController gmcDoctorSyncController;

  /**
   * setup data for testing.
   */
  @BeforeEach
  public void setup() {
    DoctorsForDB doctorsForDB1 = DoctorsForDB.builder()
        .gmcReferenceNumber("101")
        .doctorFirstName("AAA")
        .doctorLastName("BBB")
        .submissionDate(LocalDate.now())
        .dateAdded(LocalDate.now())
        .underNotice(UnderNotice.NO)
        .sanction("sanc")
        .doctorStatus(RecommendationStatus.NOT_STARTED)
        .lastUpdatedDate(LocalDate.now())
        .designatedBodyCode("PQR")
        .admin("Reval Admin").build();

    allDoctors.add(doctorsForDB1);

    DoctorsForDB doctorsForDB2 = DoctorsForDB.builder()
        .gmcReferenceNumber("201")
        .doctorFirstName("CCC")
        .doctorLastName("DDD")
        .submissionDate(LocalDate.now())
        .dateAdded(LocalDate.now().minusDays(3))
        .underNotice(UnderNotice.NO)
        .sanction("sanc")
        .doctorStatus(RecommendationStatus.NOT_STARTED)
        .lastUpdatedDate(LocalDate.now())
        .designatedBodyCode("XYZ")
        .admin("Reval Admin").build();

    allDoctors.add(doctorsForDB2);

  }

  @Test
  public void shouldSendMessageToSqs() throws Exception {
    this.mockMvc.perform(get("/api/v1/sqs/send-doctor"))
        .andExpect(status().isOk())
        .andExpect(content().string("success"));

    assertThat(allDoctors.size(), is(2));
    assertThat(allDoctors.get(0).getGmcReferenceNumber(), is("101"));
    assertThat(allDoctors.get(1).getGmcReferenceNumber(), is("201"));
    assertThat(allDoctors.get(0).getDesignatedBodyCode(), is("PQR"));
    assertThat(allDoctors.get(1).getDesignatedBodyCode(), is("XYZ"));

  }
}
