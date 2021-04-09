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

package uk.nhs.hee.tis.revalidation.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.core.env.ResourceIdResolver;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

@ExtendWith(MockitoExtension.class)
class AwsSqsQueueConfigTest {

  AwsSqsQueueConfig awsSqsQueueConfig;
  ObjectMapper objectMapper;
  DateFormat df;
  @Mock
  MappingJackson2MessageConverter jackson2MessageConverter;
  @Mock
  private AmazonSQSAsync amazonSqs;
  @Mock
  private ResourceIdResolver resourceIdResolver;
  @InjectMocks
  private AmazonSQSAsyncClientBuilder clientBuilder;


  @BeforeEach
  void setUp() {
    awsSqsQueueConfig = new AwsSqsQueueConfig();
    objectMapper = new ObjectMapper();
    df = new SimpleDateFormat("dd/MM/yyyy");
    objectMapper.setDateFormat(df);
    jackson2MessageConverter.setObjectMapper(objectMapper);
  }

  @Test
  public void testAmazonSQSAsync() {
    AmazonSQSAsync config = clientBuilder.build();
    try (MockedStatic<AmazonSQSAsyncClientBuilder> dummy = Mockito
        .mockStatic(AmazonSQSAsyncClientBuilder.class)) {
      dummy.when(() -> AmazonSQSAsyncClientBuilder.defaultClient()).thenReturn(config);
    }
    assertNotNull("The returned value must not be null",
        String.valueOf(awsSqsQueueConfig.amazonSQSAsync()));
  }

  @Test
  public void testMappingJackson2MessageConverter() {
    when(jackson2MessageConverter.getObjectMapper()).thenReturn(objectMapper);
    assertThat(awsSqsQueueConfig.mappingJackson2MessageConverter(objectMapper).getObjectMapper()
        .getDateFormat(), is(jackson2MessageConverter.getObjectMapper().getDateFormat()));

  }
}