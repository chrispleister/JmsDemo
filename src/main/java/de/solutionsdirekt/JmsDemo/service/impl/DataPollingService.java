package de.solutionsdirekt.JmsDemo.service.impl;

import com.microsoft.azure.servicebus.Message;
import de.solutionsdirekt.JmsDemo.service.MessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.naming.NamingException;
import java.time.Instant;

@Service
public class DataPollingService {
    private static final Logger log = LoggerFactory.getLogger( DataPollingService.class );

    MessagingService messagingService;

    public DataPollingService(MessagingService topicService) {
        this.messagingService = topicService;
    }

    @Scheduled(fixedRate = 1000)
    public void getDataAndPublish() throws JMSException, NamingException {
        Message message = new Message(String.format("It is %s and thats nice, isn't it ?", Instant.now()));
        this.messagingService.sendMessage(message);
    }
}
