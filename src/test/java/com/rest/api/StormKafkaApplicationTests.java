package com.rest.api;

import com.storm.ConsumerBolt;
import com.storm.InMemoryPaymentStore;
import com.storm.PaymentStoreService;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.storm.Config;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RunWith(JUnit4.class)
public class StormKafkaApplicationTests {
    public static final String TOPOLOGY_NAME = "kafka-storm-kafka";
    static LocalTopologySubmitter stormTopologySubmitter = new LocalTopologySubmitter();
    static StormTopologyBuilder stormTopologyBuilder = new StormTopologyBuilder();

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(
            2, true, 1, TestCommon.INPUT_TOPIC_NAME, "messages");

    @BeforeClass
    public static void setup() throws InvalidTopologyException, AuthorizationException, AlreadyAliveException, SQLException {
        submitTopology();
        Resource resource = new ClassPathResource("data.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        databasePopulator.populate(PaymentStoreService.getDataSource().getConnection());
    }

    @AfterClass
    public static void tearDown() {
        try {
            stormTopologySubmitter.killTopology(TOPOLOGY_NAME);
        }catch(Exception e){
            System.out.println(e);
        };
    }


    @Test
    public void testReceive() throws Exception {

      /*  EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(1, true);
        embeddedKafkaBroker.afterPropertiesSet();
        KafkaServer kafkaServer = embeddedKafkaBroker.getKafkaServer(0);
        BrokerAddress brokerAddress = embeddedKafkaBroker.getBrokerAddress(0);
        kafkaServer.startup();*/

        //submitTopology();

        Map<String, Object> configs = new HashMap(KafkaTestUtils.producerProps(embeddedKafka));
        String payloadMessage = "paymentId-8";
        ProducerRecord producerRecord = new ProducerRecord<String, String>(TestCommon.INPUT_TOPIC_NAME, payloadMessage);
        Producer<String, String> producer = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new StringSerializer()).createProducer();
        producer.send(producerRecord);


        Thread.sleep(100000);
        PaymentStoreService defaultInstance = PaymentStoreService.getDefaultInstance();
        System.out.println(defaultInstance);
        Assert.assertEquals(false, defaultInstance.getPayment(payloadMessage).isEmpty());

    }

    private static void submitTopology() throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        Config config = new Config();
        config.setDebug(true);
        String bootstrapServers = embeddedKafka.getBrokersAsString();
        System.out.println("Broker -- " + bootstrapServers);
        StormTopology topology = stormTopologyBuilder.build(new ConsumerBolt(new InMemoryPaymentStore()), TestCommon.INPUT_TOPIC_NAME, bootstrapServers, "storm1");

        stormTopologySubmitter.submitTopology(TOPOLOGY_NAME, config, topology);
    }
}

