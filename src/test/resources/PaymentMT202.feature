Feature: Payment processing status notification functionality for MT202

  Scenario: New Payment processing status messages received for MT202 from IPP
    Given PaymentStore and PaymentMessageBus is up and running
    When Payment status change message received for Payment Ref number "TxPaymentId2004"
    Then PaymentNotificationStore should contain new entry for Payment Ref number "TxPaymentId2004"


  @regression
  Scenario: Modified Payment processing status messages received for MT202 from IPP
    Given PaymentStore and PaymentMessageBus is up and running
    When Payment status change message received for Payment Ref number "TxPaymentId2009"
    Then PaymentNotificationStore should contain new entry for Payment Ref number "TxPaymentId2009"