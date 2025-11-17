package com.gymapp.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymapp.common.WorkloadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadMessageProducer {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public void sendWorkload(WorkloadRequest req) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(req);

            jmsTemplate.convertAndSend("worload.queue", jsonPayload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send workload payload", e);
        }
    }
}
