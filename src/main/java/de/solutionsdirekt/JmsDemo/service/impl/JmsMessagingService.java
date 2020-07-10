package de.solutionsdirekt.JmsDemo.service.impl;

import com.microsoft.azure.servicebus.Message;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import de.solutionsdirekt.JmsDemo.service.MessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class JmsMessagingService implements MessagingService {
    private static final Logger log = LoggerFactory.getLogger( JmsMessagingService.class );

    @Value("${spring.jms.servicebus.connection-string}")
    String connectionString;

    ConnectionStringBuilder csb;
    Context context;

    public JmsMessagingService() throws NamingException {
        csb = new ConnectionStringBuilder(connectionString);

        // set up the JNDI context
        Hashtable<String, String> hashtable = new Hashtable<>();
        hashtable.put("connectionfactory.SBCF", "amqps://" + csb.getEndpoint().getHost() + "?amqp.idleTimeout=120000&amqp.traceFrames=true");
        hashtable.put("topic.TOPIC", "shipdata");
        hashtable.put("queue.SUBSCRIPTION1", "nexustesting/shipdata/nexusSub");
        hashtable.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        this.context = new InitialContext(hashtable);
    }

    private void sendMessage(String connectionString, Message message) throws NamingException, JMSException {
        // The connection string builder is the only part of the azure-servicebus SDK library
        // we use in this JMS sample and for the purpose of robustly parsing the Service Bus
        // connection string.

        ConnectionFactory cf = (ConnectionFactory) context.lookup("SBCF");


        // Look up the topic
        Destination topic = (Destination) context.lookup("TOPIC");

        // we create a scope here so we can use the same set of local variables cleanly
        // again to show the receive side seperately with minimal clutter
        {
            // Create Connection
            Connection connection = cf.createConnection(csb.getSasKeyName(), csb.getSasKey());
            connection.start();
            // Create Session, no transaction, client ack
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            // Create producer
            MessageProducer producer = session.createProducer(topic);

            // Send messages
                BytesMessage bytesMessage = session.createBytesMessage();
                bytesMessage.writeBytes(String
                        .valueOf(message)
                        .getBytes());
                producer.send(bytesMessage);

            producer.close();
            session.close();
            connection.stop();
            connection.close();
        }
    }

    @Override
    public void receiveMessage()  {
        //TODO
    }

    @Override
    public void sendMessage(Message message) throws JMSException, NamingException {
        sendMessage(this.connectionString, message);
    }
}
