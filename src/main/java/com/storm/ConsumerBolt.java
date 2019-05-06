package com.storm;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import javax.imageio.spi.IIORegistry;
import java.util.Date;
import java.util.Map;

/**
 * @author Amit Kumar
 */
public class ConsumerBolt extends BaseRichBolt {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ConsumerBolt.class);
    private OutputCollector collector;
    private PaymentStoreService paymentStoreService;

    public ConsumerBolt(PaymentStoreService paymentStoreService) {
        // this.paymentStoreService=paymentStoreService;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.paymentStoreService = PaymentStoreService.getDefaultInstance();
    }

    @Override
    public void execute(Tuple input) {
        LOG.info("--------------------------- MSG--------");
        System.out.println("--------------------------- MSG--------");
        String message = input.getStringByField("message");
        LOG.info(message);
        Payment payment = new Payment();
        payment.setPaymentId(message);
        payment.setPaymentDate(new Date().toString());
        payment.setPaymentFromParty("Ajay Patel");
        payment.setPaymentToParty("Prem");
        System.out.println("Object -> " + paymentStoreService);
        paymentStoreService.store(payment);
        collector.emit(new Values(input));
        collector.ack(input);
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("message"));
    }
}