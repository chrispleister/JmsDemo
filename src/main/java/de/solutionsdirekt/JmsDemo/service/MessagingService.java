package de.solutionsdirekt.JmsDemo.service;

import com.microsoft.azure.servicebus.Message;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.naming.NamingException;
import java.util.concurrent.atomic.AtomicInteger;

public interface MessagingService {
    void receiveMessage();
    void sendMessage(Message message) throws JMSException, NamingException;
}
