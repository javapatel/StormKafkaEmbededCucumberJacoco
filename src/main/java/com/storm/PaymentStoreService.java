package com.storm;

import javax.sql.DataSource;
import java.util.List;

public interface PaymentStoreService {
    static DBPaymentService instance = new DBPaymentService();

    void store(Payment payment);

    List<Payment> getPayment(String paymentId);

    static PaymentStoreService getDefaultInstance() {
        return instance;
    }

    static DataSource getDataSource() {
        return DBPaymentService.dataSource();
    }
}
