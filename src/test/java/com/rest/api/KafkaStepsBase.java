package com.rest.api;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Assert;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.HashMap;
import java.util.Map;

public class KafkaStepsBase extends KafkaApplicationBase {

    @Given("^PaymentStore and PaymentMessageBus is up and running$")
    public void the_bag_is_empty() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        //setup();
    }

    @When("^Payment status change message received for Payment Ref number \"([^\"]*)\"$")
    public void i_put_potato_in_the_bag(String paymentId) throws Exception {
        // Write code here that turns the phrase above into concrete actions
        Map<String, Object> configs = new HashMap(KafkaTestUtils.producerProps(embeddedKafka));
        ProducerRecord producerRecord = new ProducerRecord<String, String>(TestCommon.INPUT_TOPIC_NAME, paymentId);
        Producer<String, String> producer = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new StringSerializer()).createProducer();
        producer.send(producerRecord);

    }

    @Then("^PaymentNotificationStore should contain new entry for Payment Ref number \"([^\"]*)\"$")
    public void payment_notification_store_should_contain_new_entry_for_payment_ref(String arg1) throws Exception {
        Thread.sleep(100000);
        Assert.assertEquals(false, paymentStoreService.getPayment(arg1).isEmpty());
    }
}