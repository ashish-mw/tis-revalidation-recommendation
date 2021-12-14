package uk.nhs.hee.tis.revalidation.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.mongodb.client.MongoDatabase;
import net.javacrumbs.shedlock.core.LockProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

@ExtendWith(MockitoExtension.class)
class SchedulingConfigTest {

  @Mock
  MongoTemplate mockTemplate;

  @Mock
  private MongoDatabase mockDatabase;

  @Test
  void lockProvider() {
    when(mockTemplate.getDb()).thenReturn(mockDatabase);
    SchedulingConfig testObj = new SchedulingConfig();
    final LockProvider actual = testObj.lockProvider(mockTemplate);

    verify(mockTemplate).getDb();
    verifyNoMoreInteractions(mockTemplate);
  }
}