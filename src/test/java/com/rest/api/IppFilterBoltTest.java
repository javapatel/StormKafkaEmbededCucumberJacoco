package com.rest.api;

import com.storm.ConsumerBolt;
import com.storm.InMemoryPaymentStore;
import com.storm.IppFilterBolt;
import com.storm.PaymentStoreService;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.storm.Config;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

@RunWith(JUnit4.class)
public class IppFilterBoltTest {
    public IppFilterBolt filterBolt = new IppFilterBolt();

    @Test
    public void testFilteringIPPMessageWhenTransactionIdStartsWithTxPaymentId() throws Exception {
        OutputCollector outputCollector = Mockito.mock(OutputCollector.class);
        filterBolt.prepare(null, null, outputCollector);
        Tuple tuple = Mockito.mock(Tuple.class);
        Mockito.when(tuple.getStringByField("message")).thenReturn("TxPaymentId2009");
        filterBolt.execute(tuple);
        Mockito.verify(outputCollector).emit(new Values("TxPaymentId2009"));
        Mockito.verify(outputCollector).ack(tuple);
    }
}

