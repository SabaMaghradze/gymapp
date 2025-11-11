package com.gymapp.producer;

import com.gymapp.dto.request.workloadrequest.WorkloadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadMessageProducer {

    private final JmsTemplate jmsTemplate;

    public void sendWorkload(WorkloadRequest req) {
        jmsTemplate.convertAndSend("workload.queue", req);
    }
}
