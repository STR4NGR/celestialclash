package com.celestialclash.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ItemsRMQ {

    private String rabbitmqHost = "localhost"; // Хост RabbitMQ
    private String rabbitmqQueue = "minecraft"; // Название очереди RabbitMQ
    private String rabbitmqUsername = "guest"; // Имя пользователя RabbitMQ
    private String rabbitmqPassword = "guest"; // Пароль RabbitMQ

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;


    public void connectToRabbitMQ() {
        try {
            factory = new ConnectionFactory();
            factory.setHost(rabbitmqHost);
            factory.setUsername(rabbitmqUsername);
            factory.setPassword(rabbitmqPassword);
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(rabbitmqQueue, false, false, false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectFromRabbitMQ() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToRabbitMQ(String message) {
        try {
            channel.basicPublish("", rabbitmqQueue, null, message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
