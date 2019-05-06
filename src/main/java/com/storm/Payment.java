package com.storm;

import java.io.Serializable;


public class Payment implements Serializable {
    private String paymentId;
    private String paymentDate;
    private String paymentFromParty;
    private String paymentToParty;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentFromParty() {
        return paymentFromParty;
    }

    public void setPaymentFromParty(String paymentFromParty) {
        this.paymentFromParty = paymentFromParty;
    }

    public String getPaymentToParty() {
        return paymentToParty;
    }

    public void setPaymentToParty(String paymentToParty) {
        this.paymentToParty = paymentToParty;
    }
}
