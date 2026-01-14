package com.example.cadence.demo;

import com.example.cadence.worker.WorkerStarter;
import com.example.cadence.workflow.OrderWorkflow;
import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class DemoRunner implements CommandLineRunner {

    private final WorkflowClient client;
    private static final String TASK_QUEUE = "ORDER_TASK_QUEUE";
    private static final Logger logger = LoggerFactory.getLogger(WorkerStarter.class);

    public DemoRunner(WorkflowClient client) {
        this.client = client;
    }

    @Override
    public void run(String... args) {
        logger.info("=== DEMO SCENARIO 1: Successful payment ===");
        WorkflowOptions options = new WorkflowOptions.Builder()
                .setTaskList(TASK_QUEUE)
                .setExecutionStartToCloseTimeout(Duration.ofMinutes(15))
                .build();

        OrderWorkflow workflow1 = client.newWorkflowStub(OrderWorkflow.class, options);
        // Асинхронно стартуем workflow1
        WorkflowClient.start(workflow1::processOrder, "ORDER-1");
        workflow1.paymentConfirmed(); // сразу платёж
        logger.info("Status ORDER-1: {}", workflow1.getStatus());

        logger.info("=== DEMO SCENARIO 2: Delayed payment ===");
        OrderWorkflow workflow2 = client.newWorkflowStub(OrderWorkflow.class, options);
        // Асинхронно стартуем workflow2
        WorkflowClient.start(workflow2::processOrder, "ORDER-2");

        new Thread(() -> {
            try {
                Thread.sleep(10000); // имитация задержки платежа
                workflow2.paymentConfirmed();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        logger.info("Status ORDER-2: {}", workflow2.getStatus());

        logger.info("=== DEMO SCENARIO 3: No payment (timeout) ===");
        OrderWorkflow workflow3 = client.newWorkflowStub(OrderWorkflow.class, options);
        WorkflowClient.start(workflow3::processOrder, "ORDER-3");
        logger.info("Status ORDER-3: {}", workflow3.getStatus());
    }
}