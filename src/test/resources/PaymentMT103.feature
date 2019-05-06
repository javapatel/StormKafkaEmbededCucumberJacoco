Feature: Payment processing status notification functionality for MT103


  Scenario: New Payment processing status messages received of MT103 from IP
    Given PaymentStore and PaymentMessageBus is up and running
    When Payment status change message received for Payment Ref number "TxPaymentId1007"
    Then PaymentNotificationStore should contain new entry for Payment Ref number "TxPaymentId1007"
#  Example:
#  |paymentRef|
#  |1003      |
#  |1004      |

#  Scenario: Updated Payment processing status messages received of MT103 for existing Payment from IPP
#    Given PaymentStore and PaymentMessageBus is up and running
#    When Payment status change message received for Payment Ref number "TxPaymentId1005"
#    Then PaymentNotificationStore should contain new entry for Payment Ref number "TxPaymentId1005"
