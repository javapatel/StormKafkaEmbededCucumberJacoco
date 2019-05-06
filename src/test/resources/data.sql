DROP TABLE payment;
CREATE TABLE payment
(
    paymentId varchar(111) NULL,
    paymentDate varchar(100) NULL,
    paymentFromParty varchar(100) NULL,
    paymentToParty varchar(100)  NULL,
    PRIMARY KEY (paymentId)
);