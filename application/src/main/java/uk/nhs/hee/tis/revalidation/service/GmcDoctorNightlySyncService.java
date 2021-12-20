package uk.nhs.hee.tis.revalidation.service;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.revalidation.messages.publisher.GmcDoctorsForDbSyncStartPublisher;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;

@Service
public class GmcDoctorNightlySyncService {

  private GmcDoctorsForDbSyncStartPublisher gmcDoctorsForDbSyncStartPublisher;

  private DoctorsForDBService doctorsForDBService;

  public GmcDoctorNightlySyncService(
      DoctorsForDBService doctorsForDBService,
      GmcDoctorsForDbSyncStartPublisher gmcDoctorsForDbSyncStartPublisher
  ) {
    this.gmcDoctorsForDbSyncStartPublisher = gmcDoctorsForDbSyncStartPublisher;
    this.doctorsForDBService = doctorsForDBService;
  }

  @Scheduled(cron="${app.gmc.nightlySyncStart.cronExpression}")
  @SchedulerLock(name = "GmcNightlySyncJob")
  public void startNightlyGmcDoctorSync() {
    this.doctorsForDBService.hideAllDoctors();
    this.gmcDoctorsForDbSyncStartPublisher.publishNightlySyncStartMessage();
  }

}
