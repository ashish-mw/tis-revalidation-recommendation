package uk.nhs.hee.tis.revalidation.messages.publisher;

import org.springframework.stereotype.Component;

@Component
public class GmcDoctorsForDbSyncStartPublisher {

  private String startMessage = "${app.gmc.nightlySyncStartMessage}";

  private MessagePublisher messagePublisher;

  public GmcDoctorsForDbSyncStartPublisher(MessagePublisher messagePublisher) {
    this.messagePublisher = messagePublisher;
  }

  /**
   * Publishes message to broker
   */
  public void publishNightlySyncStartMessage() {
    messagePublisher.publishToBroker(startMessage);
  }

}
