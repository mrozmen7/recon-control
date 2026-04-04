package com.yavuzozmen.reconcontrol.fraud.application.port.out;

import com.yavuzozmen.reconcontrol.fraud.domain.FraudCase;

public interface FraudAlertEventPublisher {

    void publishFraudDetected(FraudCase fraudCase);
}
