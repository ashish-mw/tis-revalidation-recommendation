package uk.nhs.hee.tis.revalidation.messages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.hee.tis.revalidation.dto.ConnectionMessageDto;
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

  @RabbitListener(queues = "${app.rabbit.connection.queue}")
  public void receiveRemoveDoctorDesignatedBodyCodeMessage(final ConnectionMessageDto message) {
    log.info("Message received to update designated body code from rabbit, Message: {}", message);
    doctorsForDBService.removeDesignatedBodyCode(message);
  }

}
