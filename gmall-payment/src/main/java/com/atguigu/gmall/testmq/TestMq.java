package com.atguigu.gmall.testmq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

public class TestMq {
    public static void main(String[] args) {
        String brokURL = "tcp://localhost:61616";
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokURL);
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue("drink");
            MessageProducer producer = session.createProducer(queue);
            ActiveMQTextMessage mqTextMessage = new ActiveMQTextMessage();
            mqTextMessage.setText("hello  world");
            producer.send(mqTextMessage);
            session.commit();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
