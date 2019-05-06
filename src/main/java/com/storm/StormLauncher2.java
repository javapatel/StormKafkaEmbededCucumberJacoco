package com.storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.testing.IdentityBolt;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.Properties;

public class StormLauncher2 {
    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {


        final TopologyBuilder builder = new TopologyBuilder();
        final Fields fields = new Fields("topic", "key", "message");

        // Properties
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "1");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        /*props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.jaas.config", "com.sun.security.auth.module.Krb5LoginModule required "
                + "useTicketCache=false "
                + "renewTicket=true "
                + "serviceName=\"kafka\" "
                + "useKeyTab=true "
                + "keyTab=\"/home/pvillard/pvillard.keytab\" "
                + "principal=\"pvillard@EXAMPLE.COM\";");
*/
        // Kafka spout getting data from "inputTopicStorm"
        KafkaSpoutConfig<String, String> kafkaSpoutConfig = KafkaSpoutConfig
                .builder(props.getProperty("bootstrap.servers"), "sample.request")
                .setGroupId("storm1")
                .setOffsetCommitPeriodMs(1000)
                .setFirstPollOffsetStrategy(KafkaSpoutConfig.FirstPollOffsetStrategy.LATEST)
                .setProp(props)
                .setRecordTranslator((r) -> new Values(r.topic(), r.key(), r.value()), new Fields("topic", "key", "message"))
                .build();


        KafkaSpout<String, String> kafkaSpout = new KafkaSpout<>(kafkaSpoutConfig);

        // Identity bolt (just for testing, doing nothing)
        IdentityBolt identityBolt = new IdentityBolt(fields);

        // Kafka bolt to send data into "outputTopicStorm"
        KafkaBolt<String, String> kafkaBolt = new KafkaBolt<String, String>()
                .withProducerProperties(props)
                .withTopicSelector(new DefaultTopicSelector("outputTopicStorm"))
                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<>());

        // Building the topology: KafkaSpout -> Identity -> KafkaBolt
        builder.setSpout("kafka-spout", kafkaSpout);
        builder.setBolt("identity", identityBolt).shuffleGrouping("kafka-spout");
        builder.setBolt("kafka-bolt", new ConsumerBolt(new InMemoryPaymentStore()), 2).globalGrouping("identity");

        // Submit the topology
        Config conf = new Config();
        conf.setDebug(true);

        //if(args!=null && args.length > 0)
        {
        /*    conf.setNumWorkers(3);
            StormSubmitter.submitTopology("kafka-storm-kafka", conf, builder.createTopology());*/
        }
        //else
            {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("kafka-storm-kafka", conf, builder.createTopology());

            /*Utils.sleep(10000);
            cluster.killTopology("kafka-storm-kafka");
            cluster.shutdown();*/
        }
    }
}

