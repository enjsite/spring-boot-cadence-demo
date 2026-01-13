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
import java.time.Duration;

@Component
public class WorkerStarter {

    private static final String DOMAIN = "test-domain";
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

//    public static void main(String[] args) throws Exception {
//        // Cadence service client
//         WorkflowClient client = WorkflowClient.newInstance(
//                new WorkflowServiceTChannel(
//                        ClientOptions.newBuilder()
//                                .setHost("localhost")
//                                .setPort(7933)
//                                .build()
//                ),
//                WorkflowClientOptions.newBuilder()
//                        .setDomain(DOMAIN)
//                        .build()
//        );
//
//        // Factory для worker
//        WorkerFactory factory = WorkerFactory.newInstance(client);
//        Worker worker = factory.newWorker(TASK_QUEUE);
//
//        // Регистрируем workflow и activity
//        worker.registerWorkflowImplementationTypes(OrderWorkflowImpl.class);
//        worker.registerActivitiesImplementations(new OrderActivitiesImpl());
//
//        // Запуск worker
//        factory.start();
//        logger.info("Worker started on task queue: {}", TASK_QUEUE);
//
//        // Демонстрационные сценарии
//        runDemoScenarios(client);
//    }

//    private static void runDemoScenarios(WorkflowClient client) throws InterruptedException {
//        logger.info("=== DEMO SCENARIO 1: Successful payment ===");
//        WorkflowOptions options = new WorkflowOptions.Builder()
//                .setTaskList(TASK_QUEUE) // <- Cadence использует TaskList
//                .setExecutionStartToCloseTimeout(Duration.ofHours(1)) // надо ли?
//                .build();
//
//        OrderWorkflow workflow1 = client.newWorkflowStub(OrderWorkflow.class,
//                new WorkflowOptions.Builder()
//                        .setTaskList(TASK_QUEUE) // <- Cadence использует TaskList
//                        .setExecutionStartToCloseTimeout(Duration.ofHours(1)) // надо ли?
//                        .build());
//        workflow1.processOrder("ORDER-1");
//        workflow1.paymentConfirmed(); // сразу платёж
//        logger.info("Status ORDER-1: {}", workflow1.getStatus());
//
//        logger.info("=== DEMO SCENARIO 2: Delayed payment ===");
//        OrderWorkflow workflow2 = client.newWorkflowStub(OrderWorkflow.class,
//                new WorkflowOptions.Builder()
//                        .setTaskList(TASK_QUEUE) // <- Cadence использует TaskList
//                        .setExecutionStartToCloseTimeout(Duration.ofHours(1)) // надо ли?
//                        .build());
//        new Thread(() -> {
//            try {
//                Thread.sleep(2000); // имитация задержки платежа
//                workflow2.paymentConfirmed();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
//        workflow2.processOrder("ORDER-2");
//        logger.info("Status ORDER-2: {}", workflow2.getStatus());
//
//        logger.info("=== DEMO SCENARIO 3: No payment (timeout) ===");
//        OrderWorkflow workflow3 = client.newWorkflowStub(OrderWorkflow.class,
//                new WorkflowOptions.Builder()
//                        .setTaskList(TASK_QUEUE) // <- Cadence использует TaskList
//                        .setExecutionStartToCloseTimeout(Duration.ofHours(1)) // надо ли?
//                        .build());
//        workflow3.processOrder("ORDER-3"); // не посылаем сигнал
//        logger.info("Status ORDER-3: {}", workflow3.getStatus());
//    }
}
