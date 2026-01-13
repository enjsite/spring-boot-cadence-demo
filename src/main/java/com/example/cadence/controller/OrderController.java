package com.example.cadence.controller;

import com.uber.cadence.WorkflowExecution;
import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowOptions;
import com.example.cadence.workflow.OrderWorkflow;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final String TASK_QUEUE = "ORDER_TASK_QUEUE";

    private final WorkflowClient client;

    public OrderController(WorkflowClient client) {
        this.client = client;
    }

    @PostMapping("/{orderId}")
    public String startOrder(@PathVariable String orderId) {
        String workflowId = "ORDER-" + orderId;

        WorkflowOptions options = new WorkflowOptions.Builder()
                .setWorkflowId(workflowId)
                .setTaskList(TASK_QUEUE)
                .setExecutionStartToCloseTimeout(Duration.ofHours(1))
                .build();

        OrderWorkflow workflow = client.newWorkflowStub(OrderWorkflow.class, options);
        try {
            WorkflowClient.start(workflow::processOrder, workflowId);
        } catch (Exception e) {
            return "Already created order with id " + orderId;
        }

        return "Order workflow started: " + orderId;
    }

    @PostMapping("/{orderId}/paid")
    public String confirmPayment(@PathVariable String orderId) {
        String workflowId = "ORDER-" + orderId;

        OrderWorkflow workflow = client.newWorkflowStub(OrderWorkflow.class, workflowId);

        workflow.paymentConfirmed();
        return "Payment confirmed for order: " + orderId;
    }

    @GetMapping("/{orderId}/status")
    public String getStatus(@PathVariable String orderId) {
        String workflowId = "ORDER-" + orderId;

        OrderWorkflow workflow = client.newWorkflowStub(OrderWorkflow.class, workflowId);

        return "Order " + orderId + " status: " + workflow.getStatus();
    }
}
