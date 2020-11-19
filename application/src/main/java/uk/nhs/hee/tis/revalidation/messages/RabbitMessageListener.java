package uk.nhs.hee.tis.revalidation.messages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.hee.tis.revalidation.dto.DoctorsForDbDto;
import uk.nhs.hee.tis.revalidation.service.DoctorsForDBService;

@Slf4j
@Component
public class RabbitMessageListener {

  @Autowired
  private DoctorsForDBService doctorsForDBService;

  @RabbitListener(queues = "${app.rabbit.queue}")
  public void receivedMessage(final DoctorsForDbDto gmcDoctor) {
    log.info("Message received from rabbit: {}", gmcDoctor);
    doctorsForDBService.updateTrainee(gmcDoctor);
  }

  @RabbitListener(queues = "${app.rabbit.remove.dbc.queue}")
  public void receiveRemoveDoctorDesignatedBodyCodeMessage(final String gmcId) {
    log.info("Message received to remove designated body code from rabbit, GmcId: {}", gmcId);
    doctorsForDBService.removeDesignatedBodyCode(gmcId);
  }

}
