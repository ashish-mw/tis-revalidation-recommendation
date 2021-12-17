package uk.nhs.hee.tis.revalidation.messages.publisher;

import org.springframework.stereotype.Component;

@Component
public class GmcDoctorsForDbSyncStartPublisher {

  private String startMessage = "${app.gmc.nightlySyncStartMessage}";

  private MessagePublisher<String> messagePublisher;

  public GmcDoctorsForDbSyncStartPublisher(MessagePublisher<String> messagePublisher) {
    this.messagePublisher = messagePublisher;
  }

  /**
   * Publishes message to broker
   */
  public void publishNightlySyncStartMessage() {
    messagePublisher.publishToBroker(startMessage);
  }

}
