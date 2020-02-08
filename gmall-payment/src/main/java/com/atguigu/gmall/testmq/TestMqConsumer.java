package com.atguigu.gmall.testmq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class TestMqConsumer {

    public static void main(String[] args) {
        String brokURL = "tcp://192.168.12.130:61616";
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokURL);
        Connection connection = null;

        try {
            connection = connectionFactory.createConnection();
            connection.setClientID("zhengguo");//设置消息的持久化
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Queue queue = session.createQueue("drink");
            //MessageConsumer consumer = session.createConsumer(queue);
            Topic topic = session.createTopic("drink");

            MessageConsumer consumer = session.createDurableSubscriber(topic, "zhengguo");

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                 TextMessage mqTextMessage = (TextMessage)message;
                    try {
                        System.out.println(mqTextMessage.getText());
                        //session.commit();
                    } catch (JMSException e) {
                        try {
                            session.rollback();
                        } catch (JMSException e1) {
                            e1.printStackTrace();
                        }
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
