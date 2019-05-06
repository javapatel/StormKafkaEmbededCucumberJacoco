package com.storm;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.kafka.spout.RecordTranslator;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

import java.util.*;


public class StormLauncher {

    static final long serialVersionUID = 42L;
    private static final int WINDOW_SIZE_MS = 10000;

    public static void main(String[] args) throws Exception {
        new StormLauncher().runMain();
    }

    protected void runMain() throws Exception {
        String topicName = "sample.request";
        boolean isLocalCluster = true;
        if (isLocalCluster) {
            submitTopologyLocalCluster(getSpeedTopolgy(topicName, "localhost:2181"), getConfig(), "test1");
        } else {
            //submitTopologyRemoteCluster("KafkaConnectTopology", getSpeedTopolgy(), getConfig());
        }

    }

    protected void submitTopologyLocalCluster(StormTopology topology, Config config, String topologyName) throws InterruptedException {
        LocalCluster cluster = new LocalCluster();
        // cluster.submitTopology(topologyName, config, topology);
        // cluster.killTopology(topologyName);

        cluster.submitTopology(topologyName, new HashMap(), topology);
        cluster.killTopology(topologyName);
    }

    protected void submitTopologyRemoteCluster(String topologyName, StormTopology topology, Config config) throws Exception {
        StormSubmitter.submitTopology(topologyName, config, topology);
    }


    protected Config getConfig() {
        Config config = new Config();
        config.setDebug(true);
//        config.setMessageTimeoutSecs((WINDOW_SIZE_MS / 1000) * 2);
        return config;
    }

    /**
     * @return the topology to run
     */
    protected StormTopology getSpeedTopolgy(String topicName, String brokerServer) {
        final TopologyBuilder tp = new TopologyBuilder();

        // consume from the truck_speed_events topic
        tp.setSpout("kafka_spout", new KafkaSpout<>(getKafkaSpoutConfig(topicName, brokerServer)), 1);

        // parse pipe-delimited speed events into a POJO
        tp.setBolt("parse_speed_event", new ConsumerBolt(new InMemoryPaymentStore())).shuffleGrouping("kafka_spout");
        ;
/*
        // calculate the average speed for driver-route over a 10 second window
        tp.setBolt("average_speed", new AverageSpeedBolt().withTumblingWindow(new BaseWindowedBolt.Duration(WINDOW_SIZE_MS, TimeUnit.MILLISECONDS)))
                .shuffleGrouping("parse_speed_event");
        //new Fields(ParseSpeedEventBolt.FIELD_DRIVER_ID, ParseSpeedEventBolt.FIELD_ROUTE_ID));

        // send results back to Kafka results topic
        tp.setBolt("kakfa_bolt", getKafkaBolt(topicName))
                .shuffleGrouping("average_speed");*/

        return tp.createTopology();
    }

    protected KafkaSpoutConfig<byte[], String> getKafkaSpoutConfig(String topicName, String brokerServer) {
        return KafkaSpoutConfig.builder(brokerServer, topicName)
                .setGroupId("speedTopologyGroup1")
                .setKey(ByteArrayDeserializer.class)
                .setValue(StringDeserializer.class)
                .setOffsetCommitPeriodMs(10)
                .setFirstPollOffsetStrategy(KafkaSpoutConfig.FirstPollOffsetStrategy.UNCOMMITTED_EARLIEST)
                .setMaxUncommittedOffsets(1)
                .build();
    }

    class TupleBuilder implements RecordTranslator<String, String> {
        @Override
        public Fields getFieldsFor(String s) {
            return null;
        }

        @Override
        public List<String> streams() {
            return null;
        }

        @Override
        public List<Object> apply(ConsumerRecord<String, String> consumerRecord) {
            try {

                ArrayList<Object> objects = new ArrayList<>();
                objects.add(consumerRecord.value());
                return objects;
            } catch (Exception e) {
                System.out.println("Failed to Parse {}. Throwing Exception {}" + consumerRecord.value());
                e.printStackTrace();
            }
            return null;
        }
    }

    protected KafkaBolt<String, String> getKafkaBolt(String topicName, String brokerServer) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokerServer);
        props.put("acks", "1");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        final KafkaBolt bolt = new KafkaBolt()
                .withProducerProperties(props)
                .withTopicSelector(new DefaultTopicSelector(topicName))
                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<>());
        return bolt;
    }

}
