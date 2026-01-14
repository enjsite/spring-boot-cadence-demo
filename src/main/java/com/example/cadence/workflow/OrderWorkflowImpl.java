package com.example.cadence.workflow;

import com.uber.cadence.workflow.Saga;
import com.uber.cadence.workflow.Workflow;
import com.uber.cadence.activity.ActivityOptions;

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
        // Инициализация Saga
        Saga saga = new Saga(new Saga.Options.Builder()
                .setParallelCompensation(false) // компенсации строго по порядку
                .build());

        try {

            // 1 Создание заказа
            activities.createOrder(orderId);
            saga.addCompensation(() -> activities.cancelOrder(orderId));
            status = "CREATED_ORDER";

            // 2️ Резервирование товаров
            activities.reserveItems(orderId);
            saga.addCompensation(() -> activities.releaseItems(orderId));
            status = "PAYMENT_PENDING";

            // 3 Ожидание оплаты (5 минут)
            boolean paymentArrived = Workflow.await(
                    Duration.ofMinutes(5),
                    () -> paymentReceived
            );

            if (!paymentArrived) {
                status = "CANCELED";
                saga.compensate();
                return;
            }

            // 4️ Оплата
            activities.chargeMoney(orderId);
            saga.addCompensation(() -> activities.refundMoney(orderId));
            status = "PAID";

            // 5️ Отгрузка
            activities.shipOrder(orderId);
            saga.addCompensation(() -> activities.returnShipment(orderId));
            status = "SHIPPED";

            // 6️ Завершение заказа
            activities.completeOrder(orderId);
            status = "COMPLETED";

        } catch (Exception e) {
            status = "CANCELED";
            saga.compensate();
            throw e;
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
