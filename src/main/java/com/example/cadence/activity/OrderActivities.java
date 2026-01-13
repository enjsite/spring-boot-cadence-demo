package com.example.cadence.activity;

import com.uber.cadence.activity.ActivityMethod;

public interface OrderActivities {

    @ActivityMethod
    void createOrder(String orderId);

    @ActivityMethod
    void reserveItems(String orderId);

    @ActivityMethod
    void chargeMoney(String orderId);

    @ActivityMethod
    void shipOrder(String orderId);

    @ActivityMethod
    void completeOrder(String orderId);

    @ActivityMethod
    void cancelOrder(String orderId);

    @ActivityMethod
    void refundMoney(String orderId);

    @ActivityMethod
    void releaseItems(String orderId);
}
