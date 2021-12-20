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

package uk.nhs.hee.tis.revalidation.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;


@ExtendWith(MockitoExtension.class)
class GmcDoctorConnectionSyncServiceTest {

  @Captor
  ArgumentCaptor<String> syncStartMessage;
  @InjectMocks
  private GmcDoctorConnectionSyncService gmcDoctorConnectionSyncService;
  @Mock
  private QueueMessagingTemplate queueMessagingTemplate;
  @Mock
  private DoctorsForDBRepository doctorsForDBRepository;

  @BeforeEach
  void setUp() {
  }

  @Test
  void shouldRetrieveAllDoctors() {
    gmcDoctorConnectionSyncService.receiveMessage("gmcSyncStart");

    verify(doctorsForDBRepository).findAll();
  }

  @Test
  void shouldNotRetireveDoctorsIfNullMessageSupplied() {
    gmcDoctorConnectionSyncService.receiveMessage(null);

    verify(doctorsForDBRepository, never()).findAll();
  }

  @Test
  void shouldNotRetireveDoctorsIfIncorrectMessageSupplied() {
    gmcDoctorConnectionSyncService.receiveMessage("anyString");

    verify(doctorsForDBRepository, never()).findAll();
  }
}