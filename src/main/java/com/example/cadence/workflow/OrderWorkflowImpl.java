package com.example.cadence.workflow;

import com.uber.cadence.workflow.Workflow;
import com.uber.cadence.activity.ActivityOptions;
import com.uber.cadence.workflow.WorkflowMethod;
import com.uber.cadence.workflow.Async;

import java.time.Duration;

import com.example.cadence.activity.OrderActivities;

public class OrderWorkflowImpl implements OrderWorkflow {

    private String status = "CREATED";  // начальное состояние
    private boolean paymentReceived = false;

    private final OrderActivities activities;

    public OrderWorkflowImpl() {

        ActivityOptions options = new ActivityOptions.Builder()
                .setScheduleToCloseTimeout(Duration.ofMinutes(10))
                .build();

        this.activities = Workflow.newActivityStub(OrderActivities.class, options);
    }

    @Override
    public void processOrder(String orderId) {
        status = "PAYMENT_PENDING";
        Workflow.sleep(Duration.ofSeconds(1)); // симуляция начальной обработки
        activities.createOrder(orderId);
        activities.reserveItems(orderId);

        // Ожидание оплаты с таймаутом 5 минут
        boolean paymentArrived = Workflow.await(Duration.ofMinutes(5), () -> paymentReceived);

        if (paymentArrived) {
            status = "COMPLETED";
            activities.chargeMoney(orderId);
            activities.shipOrder(orderId);
            activities.completeOrder(orderId);
        } else {
            status = "CANCELED";
            activities.releaseItems(orderId);
            activities.cancelOrder(orderId);
        }
    }

    @Override
    public void paymentConfirmed() {
        paymentReceived = true;
    }

    @Override
    public String getStatus() {
        return status;
    }
}
