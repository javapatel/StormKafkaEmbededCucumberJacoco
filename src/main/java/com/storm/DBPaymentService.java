package com.storm;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class DBPaymentService implements PaymentStoreService {

    private static JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());


    public static DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        dataSource.setUrl("jdbc:hsqldb:file:d:/workspace/db/paymentdb");
        dataSource.setUsername("SA");
        dataSource.setPassword("");
        return dataSource;
    }

    public List<Payment> findAll() {
        return jdbcTemplate.query("select * from payment",
                new PaymentMapper());
    }

    public Payment findPaymentById(String id) {
        return jdbcTemplate.queryForObject(
                "select * from payment where paymentId=?",
                new Object[]{id}, new PaymentMapper());
    }

    public Payment create(final Payment payment) {
        final String sql = "insert into payment(paymentId,paymentDate,paymentFromParty,paymentToParty) values(?,?,?,?)";

       // KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, payment.getPaymentId());
                ps.setString(2, payment.getPaymentDate());
                ps.setString(3, payment.getPaymentFromParty());
                ps.setString(4, payment.getPaymentToParty());
                return ps;
            }
        });

        //String newUserId = holder.getKey().toString();
        //payment.setPaymentId(newUserId);
        //System.out.println("PaymentID:--------New " + newUserId + " OLD->" + payment.getPaymentId());

        return payment;
    }

    @Override
    public void store(Payment payment) {
        create(payment);
    }

    @Override
    public List<Payment> getPayment(String paymentId) {
        return Arrays.asList(findPaymentById(paymentId));
    }
}