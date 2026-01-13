package com.example.cadence.workflow;

import com.uber.cadence.workflow.WorkflowMethod;
import com.uber.cadence.workflow.SignalMethod;
import com.uber.cadence.workflow.QueryMethod;

public interface OrderWorkflow {

    @WorkflowMethod
    void processOrder(String orderId);

    @SignalMethod
    void paymentConfirmed();

    @QueryMethod
    String getStatus();
}
