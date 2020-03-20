package uk.nhs.hee.tis.revalidation.messages;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.hee.tis.revalidation.command.DoctorForDBReceivedCommand;
import uk.nhs.hee.tis.revalidation.dto.DoctorsForDBDTO;

@Component
public class RabbitMessageListener {

    @Autowired
    private CommandGateway commandGateway;

    @RabbitListener(queues="${app.rabbit.queue}")
    public void receivedMessage(final DoctorsForDBDTO gmcDoctor) {
        final var command = DoctorForDBReceivedCommand.builder()
                .gmcReferenceNumber(gmcDoctor.getGmcReferenceNumber())
                .doctorFirstName(gmcDoctor.getDoctorFirstName())
                .doctorLastName(gmcDoctor.getDoctorLastName())
                .submissionDate(gmcDoctor.getSubmissionDate())
                .dateAdded(gmcDoctor.getDateAdded())
                .sanction(gmcDoctor.getSanction())
                .underNotice(gmcDoctor.getUnderNotice())
                .build();

        commandGateway.send(command);
    }

}
