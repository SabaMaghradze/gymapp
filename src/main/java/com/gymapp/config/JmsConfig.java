package com.gymapp.config;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@EnableJms
public class JmsConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user:}")
    private String user;

    @Value("${spring.activemq.password:}")
    private String password;

    @Value("${spring.activemq.pool.enabled:true}")
    private boolean poolEnabled;

    @Value("${spring.activemq.pool.max-connections:10}")
    private int maxConnections;

    @Value("${spring.activemq.pool.idle-timeout:30000}")
    private long idleTimeout;

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(brokerUrl);

        if (!user.isBlank()) {
            factory.setUserName(user);
            factory.setPassword(password);
        }
        return factory;
    }

    @Bean
    public ConnectionFactory connectionFactory(ActiveMQConnectionFactory activeMQConnectionFactory) {
        if (!poolEnabled) {
            return activeMQConnectionFactory;
        }
        JmsPoolConnectionFactory pool = new JmsPoolConnectionFactory();
        pool.setConnectionFactory(activeMQConnectionFactory);
        pool.setMaxConnections(maxConnections);
        pool.setConnectionIdleTimeout(Math.toIntExact(idleTimeout));
        return pool;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setPubSubDomain(false); // set to true if you use topics
        return template;
    }
}
