package com.example.cadence.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderActivitiesImpl implements OrderActivities {

    private static final Logger logger = LoggerFactory.getLogger(OrderActivitiesImpl.class);

    @Override
    public void createOrder(String orderId) {
        logger.info("Creating order: {}", orderId);
        // Здесь можно добавить логику работы с базой или имитацию
    }

    @Override
    public void reserveItems(String orderId) {
        logger.info("Reserving items for order: {}", orderId);
    }

    @Override
    public void chargeMoney(String orderId) {
        logger.info("Charging money for order: {}", orderId);
    }

    @Override
    public void shipOrder(String orderId) {
        logger.info("Shipping order: {}", orderId);
    }

    @Override
    public void completeOrder(String orderId) {
        logger.info("Completing order: {}", orderId);
    }

    @Override
    public void cancelOrder(String orderId) {
        logger.info("Cancelling order: {}", orderId);
    }

    @Override
    public void refundMoney(String orderId) {
        logger.info("Refunding money for order: {}", orderId);
    }

    @Override
    public void releaseItems(String orderId) {
        logger.info("Releasing items for order: {}", orderId);
    }

    @Override
    public void returnShipment(String orderId) {
        logger.info("Return shipment for order: {}", orderId);
    }
}