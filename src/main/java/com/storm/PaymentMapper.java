package com.storm;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

class PaymentMapper implements RowMapper<Payment> {
    @Override
    public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getString("paymentId"));
        payment.setPaymentDate(rs.getString("paymentDate"));
        payment.setPaymentFromParty(rs.getString("paymentFromParty"));
        payment.setPaymentToParty(rs.getString("paymentToParty"));
        return payment;
    }
}