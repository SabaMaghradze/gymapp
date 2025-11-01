package com.gymapp.client;

import com.gymapp.dto.request.workloadrequest.WorkloadRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "/api/trainer/workload")
public interface WorkloadServiceClient {

    @PostExchange
    void sendWorkload(@RequestBody WorkloadRequest workloadRequest);
}
