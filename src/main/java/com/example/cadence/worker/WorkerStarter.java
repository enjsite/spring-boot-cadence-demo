package com.example.cadence.worker;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowClientOptions;
import com.uber.cadence.client.WorkflowOptions;
import com.uber.cadence.serviceclient.ClientOptions;
import com.uber.cadence.serviceclient.WorkflowServiceTChannel;
import com.uber.cadence.worker.WorkerFactory;
import com.uber.cadence.worker.Worker;

import com.example.cadence.workflow.OrderWorkflow;
import com.example.cadence.workflow.OrderWorkflowImpl;
import com.example.cadence.activity.OrderActivitiesImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class WorkerStarter {

    private static final String TASK_QUEUE = "ORDER_TASK_QUEUE";
    private static final Logger logger = LoggerFactory.getLogger(WorkerStarter.class);

    private final WorkflowClient workflowClient;

    public WorkerStarter(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @PostConstruct
    public void startWorkers() {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
        Worker worker = factory.newWorker(TASK_QUEUE);

        worker.registerWorkflowImplementationTypes(OrderWorkflowImpl.class);
        worker.registerActivitiesImplementations(new OrderActivitiesImpl());

        factory.start();
        logger.info("Worker started on task queue: {}", TASK_QUEUE);
    }
}
