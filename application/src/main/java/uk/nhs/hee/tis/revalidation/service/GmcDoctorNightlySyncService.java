package uk.nhs.hee.tis.revalidation.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.revalidation.messages.publisher.GmcDoctorsForDbSyncStartPublisher;
import uk.nhs.hee.tis.revalidation.service.DoctorsForDBService;

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

  public void startNightlyGmcDoctorSync() {
    this.doctorsForDBService.hideAllDoctors();
    this.gmcDoctorsForDbSyncStartPublisher.publishNightlySyncStartMessage();
  }

}
