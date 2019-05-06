package com.rest.api;

import com.storm.ConsumerBolt;
import com.storm.PaymentStoreService;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.apache.storm.Config;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.kafka.test.rule.KafkaEmbedded;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(Cucumber.class)
//@Profile(value = "test")
@CucumberOptions(features = "src/test/resources", format = {"pretty", "html:target/reports/cucumber/html",
        "json:target/cucumber.json"})
public class StormKafkaApplicationCucumberIT {

    public static final LocalTopologySubmitter TOPOLOGY_SUBMITTER = new LocalTopologySubmitter();
    protected static AtomicBoolean serverStatus = new AtomicBoolean(false);
    protected static PaymentStoreService paymentStoreService = PaymentStoreService.getDefaultInstance();

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(
            1, true, 1, TestCommon.INPUT_TOPIC_NAME, "messages");

    @BeforeClass
    public static void setup() throws InvalidTopologyException, AuthorizationException, AlreadyAliveException, SQLException {
        if (serverStatus.get() == false) {
            submitTopology();
            loadSQL();
        }
    }

    @AfterClass
    public static void tearDown() {
        try {
            TOPOLOGY_SUBMITTER.killTopology(TestCommon.TOPOLOGY_NAME);
        }catch(Exception e){
            System.out.println("Exception While tearDown:"+e);
        };
    }


    private static void loadSQL() throws SQLException {
        Resource resource = new ClassPathResource("data.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        databasePopulator.populate(PaymentStoreService.getDataSource().getConnection());
    }

    private static void submitTopology() throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        serverStatus.compareAndSet(false, true);
        Config config = new Config();
        config.setDebug(true);
        String bootstrapServers = embeddedKafka.getBrokersAsString();
        System.out.println("Broker -- is called ---------------------------------------------------" + bootstrapServers);
        StormTopology topology = new StormTopologyBuilder().build(new ConsumerBolt(paymentStoreService), TestCommon.INPUT_TOPIC_NAME, bootstrapServers, "storm-4");

        TOPOLOGY_SUBMITTER.submitTopology(TestCommon.TOPOLOGY_NAME, config, topology);
    }


}
