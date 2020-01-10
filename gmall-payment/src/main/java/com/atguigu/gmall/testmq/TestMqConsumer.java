package com.atguigu.gmall.testmq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

public class TestMqConsumer {

    public static void main(String[] args) {
        String brokURL = "tcp://localhost:61616";
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokURL);
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("drink");

            MessageConsumer consumer = session.createConsumer(queue);

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                 TextMessage mqTextMessage = (TextMessage)message;
                    try {
                        System.out.println(mqTextMessage.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
            /*ActiveMQTextMessage  message = (ActiveMQTextMessage) consumer.receive();
            System.out.println(message.getText());*/
            //session.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
