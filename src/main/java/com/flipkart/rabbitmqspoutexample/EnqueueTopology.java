package com.flipkart.rabbitmqspoutexample;

import io.latent.storm.rabbitmq.RabbitMQBolt;
import io.latent.storm.rabbitmq.config.ConnectionConfig;
import io.latent.storm.rabbitmq.config.ProducerConfig;
import io.latent.storm.rabbitmq.config.ProducerConfigBuilder;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;

/**
 * Created by samkit.shah on 31/07/17.
 */
public class EnqueueTopology {
    private static String exchangeName = "exchange1";
    public static void main(String[] args){
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("simpleSpout", new SimpleSpout());
        ConnectionConfig connectionConfig = ConnectionConfig.getFromStormConfig((Utils.getRabbitMQConfig()));
        ProducerConfig producerConfig =  new ProducerConfigBuilder().connection(connectionConfig)
                                            .contentEncoding("UTF-8")
                                            .exchange(exchangeName)
                                            .routingKey("")
                                            .build();

        UrlQueueDeclarator urlQueueDeclarator = new UrlQueueDeclarator(exchangeName, "queue2");


        topologyBuilder.setBolt("rabbitMQBolt", new RabbitMQBolt(Utils.getTupleToMessage(exchangeName), urlQueueDeclarator))
                .addConfigurations(producerConfig.asMap())
                .shuffleGrouping("simpleSpout");

        StormTopology topology = topologyBuilder.createTopology();
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("EnqueueTopology", Utils.getStormConfiguration(), topology);
        System.out.println("Submitted storm topology to local cluster successfully.");
    }
}
