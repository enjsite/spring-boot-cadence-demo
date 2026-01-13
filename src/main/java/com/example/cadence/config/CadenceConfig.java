package com.example.cadence.config;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowClientOptions;
import com.uber.cadence.serviceclient.ClientOptions;
import com.uber.cadence.serviceclient.WorkflowServiceTChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CadenceConfig {

    @Value("${cadence.host}")
    private String host;

    @Value("${cadence.port}")
    private int port;

    @Value("${cadence.domain}")
    private String domain;

    @Bean
    public WorkflowServiceTChannel workflowService() {
        return new WorkflowServiceTChannel(
                ClientOptions.newBuilder()
                        .setHost(host)
                        .setPort(port)
                        .build()
        );
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceTChannel service) {
        return WorkflowClient.newInstance(service,
                WorkflowClientOptions.newBuilder()
                        .setDomain(domain)
                        .build()
        );
    }
}